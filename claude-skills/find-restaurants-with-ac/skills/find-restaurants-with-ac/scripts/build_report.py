#!/usr/bin/env python3
"""Render a find-restaurants-with-ac audit to a self-contained HTML table.

Usage:
    python3 build_report.py <combined.json> <out.html>

One fact per outlet: does it have air conditioning?
  verdict    : "yes" | "no" | "unknown"
  confidence : 0-100, how much to trust the verdict, from the NUMBER of sources, their
               RECENCY (old sources discounted) and TYPE (official site / amenity listing >
               article > review), dropped when sources conflict.

Input JSON schema (combined.json)
----------------------------------
{
  "meta": { "scope": "...", "date": "2026-06-24",
            "sources": ["website","amenity","article","review"], "notes": "..." },
  "restaurants": [
    {
      "name": "...", "area": "...", "website": "https://...",   # website optional
      "ac": {
        "verdict": "yes",                 # yes | no | unknown
        "confidence": 85,                 # 0-100
        "evidence": [                     # >=1 item for any non-"unknown" verdict
          { "text": "Lovely and cool inside thanks to the air conditioning.",
            "source": "review",           # website | amenity | article | review
            "date": "2025-07",            # YYYY | YYYY-MM | "current"
            "url": "https://..." }
        ]
      },
      "take": "optional 1-line note"
    }
  ]
}

A "unknown" verdict means NO evidence was found - never a guess, never a stand-in for "no".
Any non-"unknown" verdict MUST carry >=1 evidence item. Outlets with verdict "unknown" are
listed in a compact table at the foot, not ranked.
"""

import html
import json
import sys

AC_VERDICTS = {
    "yes":     {"label": "Has AC",  "tone": "great",
                "blurb": "Air conditioning present."},
    "no":      {"label": "No AC",   "tone": "bad",
                "blurb": "No air conditioning."},
    "unknown": {"label": "Unknown", "tone": "unknown",
                "blurb": "No published evidence either way."},
}

TONE_COLOURS = {
    "great":   ("#0d47a1", "#d6e7fb"),
    "bad":     ("#9a1c1c", "#f7d6d6"),
    "unknown": ("#555555", "#e7e7e7"),
}

SOURCE_LABELS = {
    "website": "website",
    "amenity": "listing",
    "article": "article",
    "review": "review",
}


def esc(value):
    return html.escape(str(value), quote=True)


def verdict_meta(verdict):
    if verdict not in AC_VERDICTS:
        raise ValueError(f"unknown verdict {verdict!r}; expected one of {sorted(AC_VERDICTS)}")
    return AC_VERDICTS[verdict]


def rank(dim):
    verdict = dim["verdict"]
    conf = dim.get("confidence", 0)
    if verdict == "yes":
        return 2000 + conf
    if verdict == "no":
        return 1000 + (100 - conf)
    return 0


def pill(dim):
    meta = verdict_meta(dim["verdict"])
    fg, bg = TONE_COLOURS[meta["tone"]]
    if dim["verdict"] == "unknown":
        return f'<span class="pill" style="color:{fg};background:{bg}" title="{esc(meta["blurb"])}">{esc(meta["label"])}</span>'
    conf = dim.get("confidence", 0)
    opacity = 0.45 + 0.55 * max(0, min(100, conf)) / 100
    return (f'<span class="pill" style="color:{fg};background:{bg};opacity:{opacity:.2f}" '
            f'title="{esc(meta["blurb"])}">{esc(meta["label"])}</span>')


def evidence_html(items):
    if not items:
        return '<span class="noev">no evidence</span>'
    rows = []
    for it in items:
        src = SOURCE_LABELS.get(it.get("source", ""), it.get("source", "?"))
        date = it.get("date")
        tag_text = f"{src} &middot; {esc(date)}" if date and date != "current" else src
        url = it.get("url")
        tag = (f'<a class="src" href="{esc(url)}" target="_blank" rel="noopener">{tag_text} &nearr;</a>'
               if url else f'<span class="src">{tag_text}</span>')
        rows.append(f'<div class="ev">{tag}<span class="q">{esc(it.get("text", ""))}</span></div>')
    return "".join(rows)


def ranked_row(i, r):
    ac = r["ac"]
    conf = ac.get("confidence", 0)
    name = esc(r["name"])
    if r.get("website"):
        name = f'<a class="site" href="{esc(r["website"])}" target="_blank" rel="noopener">{name}</a>'
    return f"""<tr>
      <td class="num">{i}</td>
      <td class="name">{name}<div class="area">{esc(r.get("area", ""))}</div></td>
      <td>{pill(ac)}</td>
      <td class="conf">{conf}%</td>
      <td class="evidence">{evidence_html(ac.get("evidence", []))}</td>
    </tr>"""


def unknown_table(rows):
    if not rows:
        return ""
    trs = []
    for r in sorted(rows, key=lambda x: x["name"].lower()):
        site = (f'<a href="{esc(r["website"])}" target="_blank" rel="noopener">website &nearr;</a>'
                if r.get("website") else "")
        trs.append(f'<tr><td>{esc(r["name"])}</td><td>{esc(r.get("area", ""))}</td><td>{site}</td></tr>')
    return f"""
  <section class="unknown-block">
    <h3>No AC info found &middot; {len(rows)} outlets</h3>
    <p class="sub">Neither the website, an amenity listing, an article nor any review mentions air
       conditioning for these outlets. Not ranked - "unknown" is the honest verdict, not a "no".</p>
    <div class="tablescroll">
      <table class="compact">
        <thead><tr><th>Outlet</th><th>Area</th><th></th></tr></thead>
        <tbody>{''.join(trs)}</tbody>
      </table>
    </div>
  </section>"""


def build(data):
    meta = data.get("meta", {})
    restaurants = data["restaurants"]
    ranked = sorted((r for r in restaurants if r["ac"]["verdict"] != "unknown"), key=lambda r: rank(r["ac"]), reverse=True)
    unknowns = [r for r in restaurants if r["ac"]["verdict"] == "unknown"]
    has_ac = sum(1 for r in restaurants if r["ac"]["verdict"] == "yes")
    no_ac = sum(1 for r in restaurants if r["ac"]["verdict"] == "no")
    notes = f'<p class="notes">{esc(meta["notes"])}</p>' if meta.get("notes") else ""
    rows = "\n".join(ranked_row(i + 1, r) for i, r in enumerate(ranked))
    table = (f"""<div class="tablescroll"><table class="ac">
      <thead><tr><th>#</th><th>Outlet</th><th>AC</th><th>Conf.</th><th>Evidence</th></tr></thead>
      <tbody>{rows}</tbody></table></div>""" if ranked else
             '<p class="notes">No outlet had any AC evidence either way.</p>')
    return f"""<title>Air-conditioned restaurants &middot; {esc(meta.get("scope", "audit"))}</title>
<style>
  :root {{ --ink:#222; --paper:#f6f8fb; --line:#dde3ec; }}
  * {{ box-sizing:border-box; }}
  body {{ margin:0; background:var(--paper); color:var(--ink);
         font:16px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif; }}
  .wrap {{ max-width:880px; margin:0 auto; padding:32px 20px 80px; }}
  header h1 {{ font-size:29px; margin:0 0 4px; letter-spacing:-.01em; }}
  header .scope {{ color:#5a6270; font-size:18px; margin:0; }}
  header .meta {{ color:#7a8392; font-size:13px; margin:8px 0 0; }}
  .stats {{ display:flex; flex-wrap:wrap; gap:12px; margin:22px 0; }}
  .stat {{ background:#fff; border:1px solid var(--line); border-radius:12px; padding:12px 16px; min-width:110px; }}
  .stat .n {{ font-size:24px; font-weight:700; }}
  .stat .l {{ font-size:12px; color:#72798a; text-transform:uppercase; letter-spacing:.04em; }}
  .notes {{ font-style:italic; color:#5a6270; }}
  .legend {{ background:#fff; border:1px solid var(--line); border-radius:12px; padding:13px 18px; margin:0 0 20px; font-size:13.5px; color:#46505f; }}
  .tablescroll {{ overflow-x:auto; }}
  table.ac {{ width:100%; border-collapse:collapse; background:#fff; border:1px solid var(--line); border-radius:12px; overflow:hidden; }}
  table.ac th, table.ac td {{ text-align:left; padding:11px 13px; border-bottom:1px solid var(--line); vertical-align:top; }}
  table.ac th {{ font-size:11px; text-transform:uppercase; letter-spacing:.04em; color:#72798a; background:#fbfcfe; }}
  table.ac tr:last-child td {{ border-bottom:none; }}
  td.num {{ color:#aab2c0; font-weight:700; width:30px; }}
  td.name {{ font-weight:600; min-width:150px; }}
  td.name a.site {{ color:#0d47a1; text-decoration:none; }}
  td.name .area {{ font-weight:400; color:#8a93a3; font-size:12.5px; }}
  td.conf {{ font-weight:700; white-space:nowrap; }}
  .pill {{ display:inline-block; font-weight:600; font-size:13px; padding:3px 11px; border-radius:999px; white-space:nowrap; }}
  td.evidence {{ font-size:12.5px; color:#46505f; max-width:340px; }}
  .ev {{ margin:0 0 6px; }}
  .ev:last-child {{ margin:0; }}
  .ev .src {{ color:#72798a; font-weight:600; margin-right:5px; }}
  a.src {{ color:#0d47a1; text-decoration:none; }}
  .ev .q {{ color:#33373d; }}
  .noev {{ color:#9aa2b0; font-style:italic; }}
  .unknown-block {{ margin:30px 0 0; }}
  .unknown-block h3 {{ font-size:17px; margin:0 0 4px; }}
  .unknown-block .sub {{ color:#7a8392; font-size:13px; margin:0 0 12px; }}
  table.compact {{ width:100%; border-collapse:collapse; background:#fff; border:1px solid var(--line); border-radius:10px; font-size:13.5px; }}
  table.compact th, table.compact td {{ text-align:left; padding:7px 12px; border-bottom:1px solid var(--line); white-space:nowrap; }}
  table.compact th {{ font-size:11px; text-transform:uppercase; letter-spacing:.04em; color:#8a93a3; }}
  table.compact tr:last-child td {{ border-bottom:none; }}
  table.compact td:first-child {{ font-weight:600; white-space:normal; }}
  table.compact a {{ color:#0d47a1; text-decoration:none; }}
</style>
<div class="wrap">
  <header>
    <h1>Air-conditioned restaurants</h1>
    <p class="scope">{esc(meta.get("scope", ""))}</p>
    <p class="meta">{esc(meta.get("date", ""))} &middot; {len(restaurants)} places ({len(ranked)} with a verdict, {len(unknowns)} no-evidence) &middot;
      sources: {esc(", ".join(SOURCE_LABELS.get(s, s) for s in meta.get("sources", [])))}</p>
    {notes}
  </header>
  <div class="stats">
    <div class="stat"><div class="n">{has_ac}</div><div class="l">Has AC</div></div>
    <div class="stat"><div class="n">{no_ac}</div><div class="l">No AC</div></div>
    <div class="stat"><div class="n">{len(unknowns)}</div><div class="l">No evidence</div></div>
  </div>
  <div class="legend"><strong>Confidence</strong> = how much to trust the verdict, from the number of sources,
    how recent they are (old reviews discounted) and their type (official site / amenity listing &gt; article &gt; review);
    it drops when sources disagree. A faded pill = a weakly-evidenced verdict. Sorted Has&nbsp;AC (by confidence) &rarr; No&nbsp;AC; outlets with no AC mention at all are listed separately at the foot.</div>
  {table}
  {unknown_table(unknowns)}
</div>"""


def main():
    if len(sys.argv) != 3:
        sys.exit("usage: build_report.py <combined.json> <out.html>")
    with open(sys.argv[1], encoding="utf-8") as fh:
        data = json.load(fh)
    if not data.get("restaurants"):
        sys.exit("combined.json has no 'restaurants'")
    with open(sys.argv[2], "w", encoding="utf-8") as fh:
        fh.write(build(data))
    print(f"wrote {sys.argv[2]} ({len(data['restaurants'])} places)")


if __name__ == "__main__":
    main()
