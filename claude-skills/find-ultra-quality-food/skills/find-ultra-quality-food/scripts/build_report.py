#!/usr/bin/env python3
"""Render a find-ultra-quality-food audit to a self-contained HTML leaderboard.

Usage:
    python3 build_report.py <combined.json> <out.html>

Each outlet is judged on two independent binary facts, each with a reliability score:

  * seed_oil_free - is there a way to eat here WITHOUT seed oil? (fries in dripping/tallow/
    lard/butter/olive/coconut, OR offers a clear seed-oil-free option)
  * organic       - are there organic options?

verdict is one of: "yes" | "no" | "unknown".
reliability (0-100) is how much to trust that verdict, set from the NUMBER of sources, their
RECENCY (old sources are discounted) and their TYPE (official site/menu > article > review),
and dropped when sources conflict. A single old review is low (~10%); an explicit current
website/menu statement plus corroborating recent reviews is high (~95%).

Input JSON schema (combined.json)
----------------------------------
{
  "meta": { "scope": "...", "date": "2026-06-22",
            "sources": ["website","menu","article","review"], "notes": "..." },
  "restaurants": [
    {
      "name": "Mr Chips Diss", "area": "Diss (IP22 4AB)", "website": "https://...",
      "seed_oil_free": {
        "verdict": "yes",                # yes | no | unknown
        "reliability": 95,               # 0-100
        "evidence": [                    # >=1 item for any non-"unknown" verdict
          { "text": "Choice of beef dripping or vegetable oil.",
            "source": "menu",            # website | menu | article | review
            "date": "current",           # YYYY | YYYY-MM | "current"  (for recency weighting)
            "url": "https://..." }
        ]
      },
      "organic": { "verdict": "no", "reliability": 60, "evidence": [ ... ] },
      "take": "1-2 line factual summary"   # optional
    }
  ]
}

A "unknown" verdict means NO evidence was found - it is NEVER a guess or a stand-in for "no".
Any verdict other than "unknown" MUST carry at least one evidence item. Outlets that are
"unknown" on BOTH facts are dropped from the ranking into a compressed table at the foot.

Ranking: seed_oil_free is primary, organic breaks ties. Within each fact the order is
yes -> no -> unknown, and within "yes" the most-reliable outlet ranks first.
"""

import html
import json
import sys

SEED_VERDICTS = {
    "yes":     {"label": "Seed-oil-free options", "tone": "great",
                "blurb": "There is a way to eat here without seed oil (non-seed frying fat, or a clear seed-oil-free option)."},
    "no":      {"label": "Uses seed oil",          "tone": "bad",
                "blurb": "Cooks in seed/vegetable oil with no seed-oil-free option."},
    "unknown": {"label": "Unknown",                "tone": "unknown",
                "blurb": "No published evidence either way."},
}

ORGANIC_VERDICTS = {
    "yes":     {"label": "Organic options",     "tone": "great",
                "blurb": "Organic dishes or ingredients are offered."},
    "no":      {"label": "No organic options",  "tone": "bad",
                "blurb": "No organic offering found."},
    "unknown": {"label": "Unknown",             "tone": "unknown",
                "blurb": "No published evidence either way."},
}

TONE_COLOURS = {
    "great":   ("#1b5e20", "#d7f0d2"),
    "bad":     ("#9a1c1c", "#f7d6d6"),
    "unknown": ("#555555", "#e7e7e7"),
}

TIER = {"yes": 2, "no": 1, "unknown": 0}

SOURCE_LABELS = {
    "website": "website",
    "menu": "menu",
    "article": "article",
    "review": "review",
}


def esc(value):
    return html.escape(str(value), quote=True)


def verdict_meta(table, verdict):
    if verdict not in table:
        raise ValueError(f"unknown verdict {verdict!r}; expected one of {sorted(table)}")
    return table[verdict]


def dim_rank(dim):
    verdict = dim["verdict"]
    rel = dim.get("reliability", 0)
    if verdict == "yes":
        return 2000 + rel
    if verdict == "no":
        return 1000 + (100 - rel)
    return 0


def both_unknown(r):
    return r["seed_oil_free"]["verdict"] == "unknown" and r["organic"]["verdict"] == "unknown"


def score(r):
    return (dim_rank(r["seed_oil_free"]), dim_rank(r["organic"]))


def badge(table, dim):
    meta = verdict_meta(table, dim["verdict"])
    fg, bg = TONE_COLOURS[meta["tone"]]
    if dim["verdict"] == "unknown":
        return (f'<span class="badge" style="color:{fg};background:{bg}" '
                f'title="{esc(meta["blurb"])}">{esc(meta["label"])}</span>')
    rel = dim.get("reliability", 0)
    opacity = 0.45 + 0.55 * max(0, min(100, rel)) / 100
    return (f'<span class="badge" style="color:{fg};background:{bg};opacity:{opacity:.2f}" '
            f'title="{esc(meta["blurb"])}">{esc(meta["label"])}'
            f'<span class="rel">{esc(rel)}%</span></span>')


def evidence_html(items):
    if not items:
        return '<div class="ev none">No evidence found.</div>'
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


def restaurant_card(rank, r):
    website = ""
    if r.get("website"):
        website = (f'<a class="site" href="{esc(r["website"])}" target="_blank" '
                   f'rel="noopener">website &nearr;</a>')
    take = f'<p class="take">{esc(r["take"])}</p>' if r.get("take") else ""
    return f"""
    <article class="card">
      <div class="rank">{rank}</div>
      <div class="body">
        <div class="head">
          <h2>{esc(r["name"])}</h2>
          <span class="area">{esc(r.get("area", ""))}</span>
          {website}
        </div>
        <div class="dims">
          <div class="dim">
            <span class="dimlab">Seed-oil-free?</span>{badge(SEED_VERDICTS, r["seed_oil_free"])}
            {evidence_html(r["seed_oil_free"].get("evidence", []))}
          </div>
          <div class="dim">
            <span class="dimlab">Organic?</span>{badge(ORGANIC_VERDICTS, r["organic"])}
            {evidence_html(r["organic"].get("evidence", []))}
          </div>
        </div>
        {take}
      </div>
    </article>"""


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
    <h3>No published evidence &middot; {len(rows)} outlets</h3>
    <p class="sub">Neither the cooking fat nor any organic sourcing is stated on these outlets'
       websites, menus, articles or reviews. Not ranked - "unknown" is the honest verdict, not a negative.</p>
    <div class="tablescroll">
      <table class="compact">
        <thead><tr><th>Outlet</th><th>Area</th><th></th></tr></thead>
        <tbody>{''.join(trs)}</tbody>
      </table>
    </div>
  </section>"""


def tally(restaurants):
    seed_yes = sum(1 for r in restaurants if r["seed_oil_free"]["verdict"] == "yes")
    seed_no = sum(1 for r in restaurants if r["seed_oil_free"]["verdict"] == "no")
    org_yes = sum(1 for r in restaurants if r["organic"]["verdict"] == "yes")
    return seed_yes, seed_no, org_yes


def build(data):
    meta = data.get("meta", {})
    restaurants = data["restaurants"]
    ranked = sorted((r for r in restaurants if not both_unknown(r)), key=score, reverse=True)
    unknowns = [r for r in restaurants if both_unknown(r)]
    seed_yes, seed_no, org_yes = tally(restaurants)
    notes = f'<p class="notes">{esc(meta["notes"])}</p>' if meta.get("notes") else ""
    cards = "\n".join(restaurant_card(i + 1, r) for i, r in enumerate(ranked))
    legend = """
    <div class="legend">
      <strong>How to read this</strong>
      <ul>
        <li><b>Seed-oil-free?</b> Is there a way to eat here without seed oil - frying in
            dripping/tallow/lard/butter/olive/coconut, or a clear seed-oil-free option? Seed/"vegetable" oils
            (rapeseed/canola, sunflower, soybean, corn, etc.) are the ones avoided.</li>
        <li><b>Organic?</b> Are organic dishes or ingredients offered?</li>
        <li><b>Reliability %</b> = how much to trust the verdict, from the number of sources, how recent they are
            (old sources are discounted), and their type (official site/menu &gt; article &gt; review); it drops when
            sources disagree. A faded badge = a weakly-evidenced verdict. Ranking is by reliability within each fact.</li>
        <li><b>Unknown</b> ranks below every verified verdict; outlets unknown on <i>both</i> facts are listed
            separately at the foot, not ranked.</li>
      </ul>
    </div>"""
    return f"""<title>Ultra-quality food &middot; {esc(meta.get("scope", "audit"))}</title>
<style>
  :root {{ --ink:#222; --paper:#faf7f2; --line:#e3ddd2; }}
  * {{ box-sizing:border-box; }}
  body {{ margin:0; background:var(--paper); color:var(--ink);
         font:16px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif; }}
  .wrap {{ max-width:920px; margin:0 auto; padding:32px 20px 80px; }}
  header h1 {{ font-size:30px; margin:0 0 4px; letter-spacing:-.01em; }}
  header .scope {{ color:#6a6256; font-size:18px; margin:0; }}
  header .meta {{ color:#8a8276; font-size:13px; margin:8px 0 0; }}
  .stats {{ display:flex; flex-wrap:wrap; gap:12px; margin:22px 0; }}
  .stat {{ background:#fff; border:1px solid var(--line); border-radius:12px; padding:12px 16px; min-width:120px; }}
  .stat .n {{ font-size:24px; font-weight:700; }}
  .stat .l {{ font-size:12px; color:#7a7264; text-transform:uppercase; letter-spacing:.04em; }}
  .legend {{ background:#fff; border:1px solid var(--line); border-radius:12px; padding:14px 18px; margin:0 0 24px; font-size:13.5px; color:#4a463d; }}
  .legend ul {{ margin:8px 0 0; padding-left:18px; }}
  .legend li {{ margin:3px 0; }}
  .notes {{ font-style:italic; color:#6a6256; }}
  .card {{ display:flex; gap:16px; background:#fff; border:1px solid var(--line);
          border-radius:14px; padding:16px 18px; margin:0 0 14px; }}
  .rank {{ font-size:22px; font-weight:800; color:#b9b0a0; min-width:30px; text-align:right; }}
  .body {{ flex:1; min-width:0; }}
  .head {{ display:flex; align-items:baseline; flex-wrap:wrap; gap:8px 12px; }}
  .head h2 {{ font-size:19px; margin:0; }}
  .area {{ color:#8a8276; font-size:13px; }}
  .site, .src {{ font-size:12px; }}
  .site {{ color:#1565c0; text-decoration:none; }}
  .dims {{ display:grid; grid-template-columns:1fr 1fr; gap:14px; margin:12px 0 0; }}
  @media (max-width:620px) {{ .dims {{ grid-template-columns:1fr; }} }}
  .dimlab {{ display:block; font-size:11px; text-transform:uppercase; letter-spacing:.05em; color:#9a9182; margin-bottom:5px; }}
  .badge {{ display:inline-flex; align-items:center; gap:7px; font-weight:600; font-size:13.5px;
           padding:4px 10px; border-radius:999px; }}
  .badge .rel {{ font-weight:700; font-size:11.5px; opacity:.85; }}
  .ev {{ font-size:13px; color:#4a463d; margin:8px 0 0; padding-left:10px; border-left:2px solid var(--line); }}
  .ev.none {{ color:#9a9182; font-style:italic; border-left-color:transparent; padding-left:0; }}
  .ev .src {{ display:inline-block; color:#7a7264; font-weight:600; margin-right:6px; }}
  a.src {{ color:#1565c0; text-decoration:none; }}
  .ev .q {{ color:#33302a; }}
  .take {{ margin:12px 0 0; color:#565147; font-size:14px; }}
  .unknown-block {{ margin:34px 0 0; }}
  .unknown-block h3 {{ font-size:18px; margin:0 0 4px; }}
  .unknown-block .sub {{ color:#8a8276; font-size:13px; margin:0 0 12px; }}
  .tablescroll {{ overflow-x:auto; }}
  table.compact {{ width:100%; border-collapse:collapse; background:#fff; border:1px solid var(--line); border-radius:10px; font-size:13.5px; }}
  table.compact th, table.compact td {{ text-align:left; padding:7px 12px; border-bottom:1px solid var(--line); white-space:nowrap; }}
  table.compact th {{ font-size:11px; text-transform:uppercase; letter-spacing:.04em; color:#9a9182; }}
  table.compact tr:last-child td {{ border-bottom:none; }}
  table.compact td:first-child {{ font-weight:600; white-space:normal; }}
  table.compact a {{ color:#1565c0; text-decoration:none; }}
</style>
<div class="wrap">
  <header>
    <h1>Ultra-quality food audit</h1>
    <p class="scope">{esc(meta.get("scope", ""))}</p>
    <p class="meta">{esc(meta.get("date", ""))} &middot; {len(restaurants)} places ({len(ranked)} ranked, {len(unknowns)} no-evidence) &middot;
      sources: {esc(", ".join(SOURCE_LABELS.get(s, s) for s in meta.get("sources", [])))}</p>
    {notes}
  </header>
  <div class="stats">
    <div class="stat"><div class="n">{seed_yes}</div><div class="l">Seed-oil-free</div></div>
    <div class="stat"><div class="n">{seed_no}</div><div class="l">Uses seed oil</div></div>
    <div class="stat"><div class="n">{org_yes}</div><div class="l">Organic options</div></div>
    <div class="stat"><div class="n">{len(unknowns)}</div><div class="l">No evidence</div></div>
  </div>
  {legend}
  {cards}
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
