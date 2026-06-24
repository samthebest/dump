---
name: uber-food-review
description: >-
  The combined restaurant audit: runs all THREE sibling skills on one outlet list and renders a
  single interactive page scoring each place on (1) food authenticity & taste (from
  food-review-review), (2) food quality - organic & seed-oil-free (from find-ultra-quality-food),
  and (3) air conditioning (from find-restaurants-with-ac). The page has a sort-by dropdown: the
  chosen dimension becomes the primary lexicographic sort key, the other two following in the
  default order taste > food quality > AC. Use when the user wants the all-in-one view of the best
  places among an explicit list OR a category + location (e.g. "Indian restaurants in Norwich",
  "all restaurants in Diss"). Token-intensive (it includes the full food-review-review pass).
argument-hint: '<explicit restaurant list> | "<category> in <place>"'
---

# uber-food-review

The **all-in-one** restaurant audit. It does not reimplement anything — it **orchestrates the three
sibling skills** over a single resolved outlet list and merges their results onto one interactive
page with a sort-by dropdown.

The three dimensions (this is also the **default lexicographic sort priority**):

1. **Taste & authenticity** — from **`food-review-review`**: a 0–100 score from clustering *every*
   review (authentic praise up, credible food faults down, palate/non-food gripes discounted).
2. **Food quality** — from **`find-ultra-quality-food`**: two binaries, `seed_oil_free` and
   `organic`, each `yes`/`no`/`unknown` + reliability.
3. **Air conditioning** — from **`find-restaurants-with-ac`**: one binary `ac`, `yes`/`no`/`unknown`
   + confidence.

The page's **sort-by dropdown** makes the chosen dimension the primary sort key; the other two
follow in the default order above (e.g. choosing *Food quality* sorts by food quality → taste → AC).
Default is *Taste & authenticity*.

## Use the sibling skills — don't reinvent them

The authoritative rubrics live in the three sibling skills' own `SKILL.md` files (installed
alongside this one in the same marketplace). **Read them and follow them verbatim** for:

- `food-review-review` — the review taxonomy, the "read every review with a real agent" rule, the
  per-outlet cluster counts.
- `find-ultra-quality-food` — what counts as a seed oil, the reliability scale, the binary verdicts.
- `find-restaurants-with-ac` — the AC verdict + confidence scale (reviews cut both ways).

All HARD RULES of the siblings apply here unchanged — most importantly: **no evidence ⇒ `unknown`,
never a defaulted negative; every non-`unknown` verdict cites dated evidence; classify with
full-strength agents, never a cheaper model or ML shortcut.**

## ⚠️ Token cost

This is the **heaviest** skill — it contains the full `food-review-review` pass (a classifier agent
per ~12 reviews) **plus** a research agent per outlet. After discovery, print a banner and continue
(non-interactive):

```
⚠️  UBER RUN: <N> outlets, ~<R> reviews to classify. This runs food-review-review's full
    per-review classification PLUS a per-outlet research pass - easily millions of tokens.
    Ctrl-C now to abort.
```

## Procedure

### Step 0 — Resolve the outlet list ONCE (`$ARGUMENTS`)

Resolve the outlet list exactly as the siblings do (Step 0 of any of them): explicit list, or
discover a category+place via the Apify Google Maps places scraper (`compass/crawler-google-places`),
keeping genuine food outlets in the locality, dropping non-food and permanently-closed. Project
`title,categoryName,address,postalCode,city,website,url,placeId,additionalInfo.Amenities.Air conditioning`
so the AC amenity flag is available. **Use this one resolved list for all three dimensions.**
Print it and the token banner.

### Step 1 — Food quality + AC, in ONE fan-out (one agent per outlet)

`find-ultra-quality-food` and `find-restaurants-with-ac` read the **same sources** (website, menu,
reviews), so combine them into a single research agent per outlet to avoid duplicate fetching. Give
the agent **both** rubrics (paste them verbatim from the two sibling SKILL.md files) and this schema:

```js
const FACT = (vs) => ({ type:'object', required:['verdict','reliability','evidence'], properties:{
  verdict:{enum:vs}, reliability:{type:'integer',minimum:0,maximum:100}, evidence:EV } });
const SCHEMA = { type:'object', required:['name','seed_oil_free','organic','ac'], properties:{
  name:{type:'string'}, area:{type:'string'}, website:{type:'string'},
  seed_oil_free: FACT(['yes','no','unknown']),
  organic:       FACT(['yes','no','unknown']),
  ac: { type:'object', required:['verdict','confidence','evidence'], properties:{
    verdict:{enum:['yes','no','unknown']}, confidence:{type:'integer',minimum:0,maximum:100}, evidence:EV } } } };
```
(`EV` is the dated-evidence array used by the siblings: `{text, source, date, url?}`.) One agent per
outlet, `agentType:'general-purpose'`, `effort:'high'`, embed the list literally, pass `TODAY` and
each outlet's Google AC amenity flag.

### Step 2 — Taste, via the full `food-review-review` pipeline

Run `food-review-review`'s pipeline on the same outlets: harvest **all** Google + TripAdvisor
reviews (Apify), then classify **every** review with parallel full-strength agents into its taxonomy
(this is the token-heavy part — do **not** sample or substitute a cheaper model). Aggregate per
outlet to cluster counts, then compute a **0–100 `taste.score`** (deterministic, in the merge step):

```
pos = 2*authentic_praise + 1*generic_praise
neg = 2*credible_food_complaint + 0.5*unsophisticated_complaint
taste.score = round(100 * (pos + 2) / (pos + neg + 4))      # Laplace-smoothed; ~100 all-praise, 50 balanced
```
Carry `stars` (avg rating) and `reviews` (count classified) for display, and a 1-line `summary`. An
outlet with **zero** reviews read ⇒ `taste.score = null` (shown as "No reviews read", sorts last on
taste).

### Step 3 — Merge into `combined.json`

Join Step 1 and Step 2 by outlet name into the schema at the top of `scripts/build_report.py`:
```
{ meta:{scope,date,sources,notes?},
  restaurants:[ { name, area, website?,
                  taste:{score,stars?,reviews?,summary},
                  food_quality:{ seed_oil_free, organic },
                  ac:{ verdict, confidence, evidence } } ] }
```
Use today's date for `meta.date`. An outlet missing from a dimension keeps that dimension `unknown`
(or `taste.score=null`) — never fabricate.

### Step 4 — Build the interactive page & publish

```
python3 <this-skill-dir>/scripts/build_report.py <combined.json> <out.html>
```
(`<this-skill-dir>` = the directory containing this SKILL.md.) It renders one card per outlet with
all three dimensions, pre-sorted by the default priority and re-sortable live via the **Sort by**
dropdown (the chosen dimension becomes the primary lexicographic key; the JS is inline/self-contained
for the Artifact CSP). Publish with the **Artifact** tool (favicon 🍽️). Give the user the URL plus a
one-line summary of the top picks under the default (taste) sort, and note they can re-sort by food
quality or AC.

## Notes

- **Run order:** Step 1 (fq+ac) and Step 2 (taste) are independent — run them as two workflows; the
  merge waits for both. Resolve the outlet list (Step 0) only once and feed both.
- **`unknown` is a feature.** Most outlets will be `unknown` on food quality / AC and may have few
  reviews; the page shows that honestly (faded badges, "No reviews read") rather than guessing.
- Keep the dimension order and the sibling contracts **stable** — the page, the sort keys and the
  three schemas are the durable, reusable interface.
