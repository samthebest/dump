#!/usr/bin/env python3
"""Render a find-ultra-quality-food audit to a self-contained HTML leaderboard.

Usage:
    python3 build_report.py <combined.json> <out.html>

Input JSON schema (combined.json)
----------------------------------
{
  "meta": {
    "scope":   "All restaurants & takeaways in Diss",   # what was audited
    "date":    "2026-06-22",                              # ISO date of the run
    "sources": ["website", "menu", "article", "review"], # source kinds consulted
    "notes":   "optional free text shown under the header"
  },
  "restaurants": [
    {
      "name":    "Tom's Fish Bar",
      "area":    "Mulbarton, Norfolk",
      "website": "https://...",            # optional, linked if present
      "seed_oil": {
        "rung":       "none",              # one of: none | avoidable | uses | unknown
        "confidence": 100,                 # 0-100 (see the skill's confidence scale)
        "evidence": [                      # >=1 item required for any non-"unknown" rung
          { "text": "We fry exclusively in beef dripping.",
            "source": "website",           # website | menu | article | review
            "url": "https://..." }         # optional
        ]
      },
      "organic": {
        "rung":       "unknown",           # one of: only | some | none | unknown
        "confidence": 0,
        "evidence": []
      },
      "take": "Traditional dripping fryer; no organic sourcing mentioned."   # optional 1-2 lines
    }
  ]
}

A "unknown" rung means NO evidence was found - it is NEVER a guess and NEVER a stand-in
for a negative. Any rung other than "unknown" MUST carry at least one evidence item.

Ranking
-------
"unknown" is the LOWEST rung in each ladder, below a confirmed bad verdict: a place we
have verified something about (even something bad) ranks above one we know nothing about.
Outlets where BOTH dimensions are "unknown" are pulled out of the ranking entirely into a
compressed "no published evidence" table.

Each ladder scores a verified GOOD rung as base + CONF_WEIGHT*confidence, so a
higher-confidence weaker rung can beat a lower-confidence stronger rung (e.g. a
seed-oil-free OPTION stated at 80% outranks a fully-seed-oil-free claim at only 60%).
Seed oil is the dominant dimension (SEED_WEIGHT); organic breaks ties.
"""

import html
import json
import sys

CONF_WEIGHT = 0.06
SEED_WEIGHT = 3

SEED_OIL_RUNGS = {
    "none":      {"label": "No seed oil",          "tone": "great",   "kind": "good", "base": 2,
                  "blurb": "Cooks entirely in non-seed fats (dripping/tallow/lard/butter/olive/coconut)."},
    "avoidable": {"label": "Seed-oil-free option", "tone": "ok",      "kind": "good", "base": 1,
                  "blurb": "Seed oil is used, but a clear seed-oil-free choice exists."},
    "uses":      {"label": "Uses seed oil",        "tone": "bad",     "kind": "bad",  "base": -2,
                  "blurb": "Cooks in seed/vegetable oil with no seed-oil-free option."},
    "unknown":   {"label": "Unknown",              "tone": "unknown", "kind": "unknown", "base": -3,
                  "blurb": "No evidence found either way."},
}

ORGANIC_RUNGS = {
    "only":    {"label": "Only organic",    "tone": "great",   "kind": "good", "base": 2,
                "blurb": "Every ingredient/dish is organic."},
    "some":    {"label": "Organic options", "tone": "ok",      "kind": "good", "base": 1,
                "blurb": "Some organic dishes or ingredients are offered."},
    "none":    {"label": "Not organic",     "tone": "bad",     "kind": "bad",  "base": -1,
                "blurb": "No organic offering found."},
    "unknown": {"label": "Unknown",         "tone": "unknown", "kind": "unknown", "base": -2,
                "blurb": "No evidence found either way."},
}

TONE_COLOURS = {
    "great":   ("#1b5e20", "#d7f0d2"),
    "ok":      ("#8a5a00", "#fdeecb"),
    "bad":     ("#9a1c1c", "#f7d6d6"),
    "unknown": ("#555555", "#e7e7e7"),
}

SOURCE_LABELS = {
    "website": "website",
    "menu": "menu",
    "article": "article",
    "review": "reviews",
}


def esc(value):
    return html.escape(str(value), quote=True)


def rung_meta(table, rung):
    if rung not in table:
        raise ValueError(f"unknown rung {rung!r}; expected one of {sorted(table)}")
    return table[rung]


def dim_score(table, dim):
    meta = rung_meta(table, dim["rung"])
    if meta["kind"] == "good":
        return meta["base"] + CONF_WEIGHT * dim.get("confidence", 0)
    return meta["base"]


def both_unknown(r):
    return r["seed_oil"]["rung"] == "unknown" and r["organic"]["rung"] == "unknown"


def score(r):
    total = SEED_WEIGHT * dim_score(SEED_OIL_RUNGS, r["seed_oil"]) + dim_score(ORGANIC_RUNGS, r["organic"])
    conf = r["seed_oil"].get("confidence", 0) + r["organic"].get("confidence", 0)
    return (total, conf)


def badge(table, dim):
    meta = rung_meta(table, dim["rung"])
    fg, bg = TONE_COLOURS[meta["tone"]]
    conf = dim.get("confidence", 0)
    conf_html = "" if dim["rung"] == "unknown" and conf == 0 else (
        f'<span class="conf">{esc(conf)}%</span>')
    return (f'<span class="badge" style="color:{fg};background:{bg}" title="{esc(meta["blurb"])}">'
            f'{esc(meta["label"])}{conf_html}</span>')


def evidence_html(items):
    if not items:
        return '<div class="ev none">No evidence found.</div>'
    rows = []
    for it in items:
        src = SOURCE_LABELS.get(it.get("source", ""), it.get("source", "?"))
        url = it.get("url")
        tag = (f'<a class="src" href="{esc(url)}" target="_blank" rel="noopener">{esc(src)} &nearr;</a>'
               if url else f'<span class="src">{esc(src)}</span>')
        rows.append(f'<div class="ev">{tag}<span class="q">{esc(it.get("text", ""))}</span></div>')
    return "".join(rows)


def restaurant_card(rank, r):
    so = r["seed_oil"]
    org = r["organic"]
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
            <span class="dimlab">Seed oil</span>{badge(SEED_OIL_RUNGS, so)}
            {evidence_html(so.get("evidence", []))}
          </div>
          <div class="dim">
            <span class="dimlab">Organic</span>{badge(ORGANIC_RUNGS, org)}
            {evidence_html(org.get("evidence", []))}
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
    <p class="sub">Neither the cooking fat nor any organic sourcing is stated on these outlets' websites,
       menus, articles or reviews. Not ranked - "unknown" is the honest verdict, not a negative.</p>
    <div class="tablescroll">
      <table class="compact">
        <thead><tr><th>Outlet</th><th>Area</th><th></th></tr></thead>
        <tbody>{''.join(trs)}</tbody>
      </table>
    </div>
  </section>"""


def tally(restaurants):
    so_none = sum(1 for r in restaurants if r["seed_oil"]["rung"] == "none")
    so_avoid = sum(1 for r in restaurants if r["seed_oil"]["rung"] == "avoidable")
    org_any = sum(1 for r in restaurants if r["organic"]["rung"] in ("only", "some"))
    org_only = sum(1 for r in restaurants if r["organic"]["rung"] == "only")
    return so_none, so_avoid, org_any, org_only


def build(data):
    meta = data.get("meta", {})
    restaurants = data["restaurants"]
    ranked = sorted((r for r in restaurants if not both_unknown(r)), key=score, reverse=True)
    unknowns = [r for r in restaurants if both_unknown(r)]
    so_none, so_avoid, org_any, org_only = tally(restaurants)
    notes = f'<p class="notes">{esc(meta["notes"])}</p>' if meta.get("notes") else ""
    cards = "\n".join(restaurant_card(i + 1, r) for i, r in enumerate(ranked))
    legend = """
    <div class="legend">
      <strong>How to read this</strong>
      <ul>
        <li><b>Seed oil:</b> <i>No seed oil</i> &gt; <i>Seed-oil-free option</i> &gt; <i>Uses seed oil</i> &gt; <i>Unknown</i>.
            Seed/"vegetable" oils (rapeseed/canola, sunflower, soybean, corn, etc.) are the ones avoided; beef dripping, tallow, lard, butter, ghee, olive &amp; coconut are not seed oils.</li>
        <li><b>Organic:</b> <i>Only organic</i> &gt; <i>Organic options</i> &gt; <i>Not organic</i> &gt; <i>Unknown</i>.</li>
        <li><b>Confidence:</b> 100% = stated on the official website/menu; 80% = multiple reviewers/articles say so; lower = a single source.
            Confidence is weighted into the rank, so a higher-confidence weaker verdict can outrank a lower-confidence stronger one.</li>
        <li><b>Unknown</b> ranks below every verified verdict; outlets that are unknown on <i>both</i> dimensions are listed separately at the foot, not ranked.</li>
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
  .stat {{ background:#fff; border:1px solid var(--line); border-radius:12px; padding:12px 16px; min-width:118px; }}
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
  .badge .conf {{ font-weight:700; font-size:11.5px; opacity:.85; }}
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
    <div class="stat"><div class="n">{so_none}</div><div class="l">No seed oil</div></div>
    <div class="stat"><div class="n">{so_avoid}</div><div class="l">Seed-oil-free option</div></div>
    <div class="stat"><div class="n">{org_any}</div><div class="l">Organic options</div></div>
    <div class="stat"><div class="n">{org_only}</div><div class="l">Only organic</div></div>
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
