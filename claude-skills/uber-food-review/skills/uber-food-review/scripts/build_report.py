#!/usr/bin/env python3
"""Render an uber-food-review audit - three dimensions on one interactive page.

Usage:
    python3 build_report.py <combined.json> <out.html>

Combines the three sibling skills into one card per outlet:
  * taste        - food authenticity & taste     (from food-review-review)
  * food_quality - organic & seed-oil-free        (from find-ultra-quality-food)
  * ac           - air conditioning               (from find-restaurants-with-ac)

The page has a sort-by dropdown. The chosen dimension becomes the PRIMARY lexicographic key;
the other two follow in the default order taste > food_quality > ac. Default sort is taste.

Input JSON schema (combined.json)
----------------------------------
{
  "meta": { "scope": "...", "date": "2026-06-24", "sources": [...], "notes": "..." },
  "restaurants": [
    {
      "name": "...", "area": "...", "website": "https://...",          # website optional
      "taste": {                                                        # from food-review-review
        "score": 78,            # 0-100 authenticity/quality score, or null if no reviews read
        "stars": 4.5,           # avg star rating, or null            (optional)
        "reviews": 320,         # number of reviews considered         (optional)
        "summary": "Names regional dishes; authentic, a few service gripes."
      },
      "food_quality": {                                                 # from find-ultra-quality-food
        "seed_oil_free": { "verdict": "yes", "reliability": 80, "evidence": [ {text,source,date,url} ] },
        "organic":       { "verdict": "no",  "reliability": 60, "evidence": [ ... ] }
      },
      "ac": { "verdict": "unknown", "confidence": 0, "evidence": [ ... ] }   # from find-restaurants-with-ac
    }
  ]
}

verdicts are "yes"|"no"|"unknown"; "unknown" = no evidence (never a guessed negative). Any
non-"unknown" verdict needs >=1 evidence item. taste.score is null when no reviews were read.
"""

import html
import json
import sys

VERDICTS = {
    "yes":     {"tone": "great"},
    "no":      {"tone": "bad"},
    "unknown": {"tone": "unknown"},
}

SEED_LABELS = {"yes": "Seed-oil-free", "no": "Uses seed oil", "unknown": "Seed oil: unknown"}
ORG_LABELS = {"yes": "Organic options", "no": "Not organic", "unknown": "Organic: unknown"}
AC_LABELS = {"yes": "Has AC", "no": "No AC", "unknown": "AC: unknown"}

TONE_COLOURS = {
    "great":   ("#1b5e20", "#d7f0d2"),
    "bad":     ("#9a1c1c", "#f7d6d6"),
    "unknown": ("#555555", "#e7e7e7"),
}

SOURCE_LABELS = {"website": "website", "menu": "menu", "amenity": "listing",
                 "article": "article", "review": "review"}


def esc(value):
    return html.escape(str(value), quote=True)


def verdict_rank(verdict, conf):
    if verdict == "yes":
        return 2000 + conf
    if verdict == "no":
        return 1000 + (100 - conf)
    return 0


def taste_key(r):
    score = r.get("taste", {}).get("score")
    return float(score) if score is not None else -1.0


def fq_key(r):
    fq = r["food_quality"]
    seed = verdict_rank(fq["seed_oil_free"]["verdict"], fq["seed_oil_free"].get("reliability", 0))
    org = verdict_rank(fq["organic"]["verdict"], fq["organic"].get("reliability", 0))
    return seed + org / 10000.0


def ac_key(r):
    ac = r["ac"]
    return float(verdict_rank(ac["verdict"], ac.get("confidence", 0)))


def taste_tone(score):
    if score is None:
        return "unknown"
    if score >= 70:
        return "great"
    if score >= 40:
        return "ok"
    return "bad"


TASTE_TONE_COLOURS = dict(TONE_COLOURS, ok=("#8a5a00", "#fdeecb"))


def verdict_badge(label, verdict, conf):
    tone = VERDICTS[verdict]["tone"]
    fg, bg = TONE_COLOURS[tone]
    if verdict == "unknown":
        return f'<span class="badge" style="color:{fg};background:{bg}">{esc(label)}</span>'
    opacity = 0.45 + 0.55 * max(0, min(100, conf)) / 100
    return (f'<span class="badge" style="color:{fg};background:{bg};opacity:{opacity:.2f}">'
            f'{esc(label)}<span class="pct">{conf}%</span></span>')


def taste_badge(taste):
    score = taste.get("score")
    fg, bg = TASTE_TONE_COLOURS[taste_tone(score)]
    if score is None:
        return f'<span class="badge" style="color:{fg};background:{bg}">No reviews read</span>'
    return (f'<span class="badge" style="color:{fg};background:{bg}">Taste'
            f'<span class="pct">{int(round(score))}/100</span></span>')


def evidence_html(items, limit=2):
    items = items or []
    if not items:
        return '<div class="ev none">no evidence</div>'
    rows = []
    for it in items[:limit]:
        src = SOURCE_LABELS.get(it.get("source", ""), it.get("source", "?"))
        date = it.get("date")
        tag_text = f"{src} &middot; {esc(date)}" if date and date != "current" else src
        url = it.get("url")
        tag = (f'<a class="src" href="{esc(url)}" target="_blank" rel="noopener">{tag_text} &nearr;</a>'
               if url else f'<span class="src">{tag_text}</span>')
        rows.append(f'<div class="ev">{tag}<span class="q">{esc(it.get("text", ""))}</span></div>')
    return "".join(rows)


def card(rank, r):
    taste = r.get("taste", {})
    fq = r["food_quality"]
    ac = r["ac"]
    website = (f'<a class="site" href="{esc(r["website"])}" target="_blank" rel="noopener">website &nearr;</a>'
               if r.get("website") else "")
    stars = taste.get("stars")
    reviews = taste.get("reviews")
    meta_bits = []
    if stars is not None:
        meta_bits.append(f'{esc(stars)}&#9733;')
    if reviews is not None:
        meta_bits.append(f'{esc(reviews)} reviews')
    taste_meta = f'<span class="tmeta">{" &middot; ".join(meta_bits)}</span>' if meta_bits else ""
    summary = f'<p class="summary">{esc(taste["summary"])}</p>' if taste.get("summary") else ""
    return f"""
    <article class="card" data-taste="{taste_key(r):.4f}" data-fq="{fq_key(r):.4f}" data-ac="{ac_key(r):.4f}">
      <div class="rank">{rank}</div>
      <div class="body">
        <div class="head">
          <h2>{esc(r["name"])}</h2>
          <span class="area">{esc(r.get("area", ""))}</span>
          {website}
        </div>
        <div class="dims">
          <div class="dim">
            <span class="dimlab">Taste &amp; authenticity</span>{taste_badge(taste)} {taste_meta}
            {summary}
          </div>
          <div class="dim">
            <span class="dimlab">Food quality</span>
            <div class="fqbadges">
              {verdict_badge(SEED_LABELS[fq["seed_oil_free"]["verdict"]], fq["seed_oil_free"]["verdict"], fq["seed_oil_free"].get("reliability", 0))}
              {verdict_badge(ORG_LABELS[fq["organic"]["verdict"]], fq["organic"]["verdict"], fq["organic"].get("reliability", 0))}
            </div>
            {evidence_html(fq["seed_oil_free"].get("evidence", []), 1)}
          </div>
          <div class="dim">
            <span class="dimlab">Air conditioning</span>{verdict_badge(AC_LABELS[ac["verdict"]], ac["verdict"], ac.get("confidence", 0))}
            {evidence_html(ac.get("evidence", []), 1)}
          </div>
        </div>
      </div>
    </article>"""


STYLE = """<style>
  :root { --ink:#222; --paper:#faf7f2; --line:#e3ddd2; }
  * { box-sizing:border-box; }
  body { margin:0; background:var(--paper); color:var(--ink);
         font:16px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif; }
  .wrap { max-width:980px; margin:0 auto; padding:32px 20px 80px; }
  header h1 { font-size:30px; margin:0 0 4px; letter-spacing:-.01em; }
  header .scope { color:#6a6256; font-size:18px; margin:0; }
  header .meta { color:#8a8276; font-size:13px; margin:8px 0 0; }
  .controls { display:flex; align-items:center; gap:10px; margin:22px 0 10px; flex-wrap:wrap; }
  .controls label { font-size:13px; color:#6a6256; text-transform:uppercase; letter-spacing:.04em; }
  .controls select { font-size:15px; padding:7px 12px; border:1px solid var(--line); border-radius:10px; background:#fff; color:var(--ink); }
  .legend { color:#8a8276; font-size:12.5px; margin:0 0 18px; }
  .card { display:flex; gap:16px; background:#fff; border:1px solid var(--line);
          border-radius:14px; padding:16px 18px; margin:0 0 14px; }
  .rank { font-size:22px; font-weight:800; color:#b9b0a0; min-width:30px; text-align:right; }
  .body { flex:1; min-width:0; }
  .head { display:flex; align-items:baseline; flex-wrap:wrap; gap:8px 12px; }
  .head h2 { font-size:19px; margin:0; }
  .area { color:#8a8276; font-size:13px; }
  .site, .src { font-size:12px; }
  .site { color:#1565c0; text-decoration:none; }
  .dims { display:grid; grid-template-columns:1fr 1fr 1fr; gap:14px; margin:12px 0 0; }
  @media (max-width:760px) { .dims { grid-template-columns:1fr; } }
  .dimlab { display:block; font-size:11px; text-transform:uppercase; letter-spacing:.05em; color:#9a9182; margin-bottom:5px; }
  .badge { display:inline-flex; align-items:center; gap:6px; font-weight:600; font-size:13px;
           padding:3px 10px; border-radius:999px; margin:0 4px 4px 0; }
  .badge .pct { font-weight:700; font-size:11px; opacity:.85; }
  .fqbadges { display:flex; flex-wrap:wrap; }
  .tmeta { font-size:12px; color:#8a8276; }
  .summary { margin:8px 0 0; font-size:13px; color:#46443d; }
  .ev { font-size:12.5px; color:#4a463d; margin:8px 0 0; padding-left:10px; border-left:2px solid var(--line); }
  .ev.none { color:#9a9182; font-style:italic; border-left-color:transparent; padding-left:0; }
  .ev .src { display:inline-block; color:#7a7264; font-weight:600; margin-right:5px; }
  a.src { color:#1565c0; text-decoration:none; }
  .ev .q { color:#33302a; }
</style>"""

SCRIPT = """<script>
  (function () {
    var ORDER = { taste: ['taste','fq','ac'], fq: ['fq','taste','ac'], ac: ['ac','taste','fq'] };
    var sel = document.getElementById('sortby');
    var container = document.getElementById('cards');
    function resort() {
      var prio = ORDER[sel.value] || ORDER.taste;
      var cards = Array.prototype.slice.call(container.children);
      cards.sort(function (a, b) {
        for (var i = 0; i < prio.length; i++) {
          var k = prio[i];
          var d = parseFloat(b.dataset[k]) - parseFloat(a.dataset[k]);
          if (d) return d;
        }
        return 0;
      });
      cards.forEach(function (c, i) {
        c.querySelector('.rank').textContent = i + 1;
        container.appendChild(c);
      });
    }
    sel.addEventListener('change', resort);
    resort();
  })();
</script>"""


def build(data):
    meta = data.get("meta", {})
    restaurants = sorted(data["restaurants"],
                         key=lambda r: (taste_key(r), fq_key(r), ac_key(r)), reverse=True)
    notes = f'<p class="legend">{esc(meta["notes"])}</p>' if meta.get("notes") else ""
    cards = "\n".join(card(i + 1, r) for i, r in enumerate(restaurants))
    src_line = ""
    if meta.get("sources"):
        src_line = " &middot; sources: " + esc(", ".join(SOURCE_LABELS.get(s, s) for s in meta["sources"]))
    return f"""<title>Uber food review &middot; {esc(meta.get("scope", "audit"))}</title>
{STYLE}
<div class="wrap">
  <header>
    <h1>Uber food review</h1>
    <p class="scope">{esc(meta.get("scope", ""))}</p>
    <p class="meta">{esc(meta.get("date", ""))} &middot; {len(restaurants)} places{src_line}</p>
  </header>
  <div class="controls">
    <label for="sortby">Sort by</label>
    <select id="sortby">
      <option value="taste" selected>Taste &amp; authenticity</option>
      <option value="fq">Food quality (organic &amp; seed-oil-free)</option>
      <option value="ac">Air conditioning</option>
    </select>
  </div>
  <p class="legend">The chosen option is the primary sort key; the other two follow in the default
    order taste &rarr; food quality &rarr; AC. Faded badges = weakly-evidenced verdicts; "unknown" = no
    evidence found (never a guessed negative).</p>
  {notes}
  <div id="cards">
  {cards}
  </div>
</div>
{SCRIPT}"""


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
