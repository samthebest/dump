---
name: food-review-review
description: >-
  Audit and RANK a set of restaurants by genuine food quality and authenticity by reading
  EVERY Google and TripAdvisor review (harvested via Apify) and clustering each one with
  parallel full-strength agents — never sampling, never an ML/embedding shortcut. Filters out
  non-food gripes (delivery, wait, price) and unsophisticated-palate complaints to surface the
  credible food signal, then produces a ranked, visual HTML report. Use when the user wants to
  find the best authentic food outlets among an explicit list of restaurants OR a category +
  location (e.g. "Indian restaurants in Norwich", "ramen in Brighton"). Token-intensive by design.
argument-hint: '<explicit restaurant list> | "<cuisine> restaurants in <place>"'
---

# food-review-review

Find the **best places to actually eat** in a set, judged on **real food quality and
authenticity** — not on price, speed, or the opinions of people who don't enjoy authentic
world food. The output is a ranked, visual report.

## Who this is for / what "good" means

The audience is **well-travelled, culinarily diverse diners** who know authentic world food and
are happy to travel or pay for it. Therefore:

- **Cost, value-for-money, and delivery speed are NOT quality signals.** A place can be pricey
  and slow and still be the best food. Discount these complaints entirely.
- **The goal is to surface genuine quality and authenticity.** Praise from someone who clearly
  knows the cuisine (orders regional/traditional/off-menu dishes and judges them on their own
  terms) is worth far more than a generic "lovely, best in town".
- **Calibrate for palate, not for niceness.** A common, *legitimately discountable* class of
  negative review comes from diners with an **unsophisticated palate who aren't accustomed to
  authentic world food** — e.g. someone who orders chips and gravy from an Indian, or chicken
  balls from a Chinese, judges a korma as "not spicy", or calls a properly balanced dish "bland"
  because it isn't drowned in sweet sauce. Their complaints reflect their expectations, not the
  kitchen. Treat these as noise. Keep the language neutral and descriptive — never insulting.
- Conversely, **credible food faults are gold and must be kept**: undercooking, illness, foreign
  objects, hygiene, or a discerning diner explaining a genuine recipe/technique failure.

## The taxonomy (six judgements + one bucket)

Every review with text is placed in exactly ONE cluster. These are cuisine-agnostic; the
examples just illustrate.

| Cluster | Meaning | Verdict |
|---|---|---|
| `non_food_complaint` | Substance is ONLY non-food: delivery time, lateness, waiting, price/value, wrong or missing order, packaging, parking, staff rudeness/service — no judgement of how the food tasted. | **Discount** |
| `unsophisticated_complaint` | Negative about the FOOD but from a palate unaccustomed to authentic world cuisine: judges by anglicised/Westernised staples (chips, chicken balls, "curry sauce"), calls properly cooked food "bland/no flavour/not spicy enough", expects MSG or heavy sweet sauce, objects to authentic texture/spicing, vague "greasy" with no specifics. | **Discount** |
| `mixed_or_other` | Genuinely mixed (good food + a non-food gripe) or impossible to place. | Set aside |
| `credible_food_complaint` | Negative about the food citing a concrete, legitimate defect OR from a clearly discerning diner: raw/undercooked, made-ill, foreign object, hygiene rating, "the [regional dish] should be made with X not Y", "every dish tastes the same", a proper assessment of a technique. | **Keep — real signal** |
| `generic_praise` | Positive but no authenticity signal: "lovely", "best in town", "hot and tasty", great portions/value/service, praises anglicised staples. | Positive, low weight |
| `authentic_praise` | Positive AND food-literate: names traditional/regional/off-menu dishes and judges them on their own terms; recognises proper recipes vs cheap shortcuts; orders the things a knowledgeable diner of that cuisine would. | **Trust — strong signal** |
| `rating_only` | A star rating with NO written text. No content to judge. | Separate bucket (counted for volume only) |

The ranking score rewards `authentic_praise` (×2) and `generic_praise` (×1), penalises
`credible_food_complaint` (×2) and lightly `unsophisticated_complaint` (×0.5); `non_food`,
`rating_only`, and `mixed` are excluded. (The bundled report computes and displays this.)

## HARD RULES (do not violate)

1. **Read every review with a real agent.** Each review with text MUST be classified by a
   full-strength model agent that has actually read its text. **NEVER** sample, **NEVER**
   substitute a cheaper/smaller model for the classification, and **NEVER** replace the agents
   with an embedding/keyword/ML clustering algorithm. The whole point is human-grade judgement at
   scale. (Use many agents in parallel — see the workflow below.) This is expensive in tokens by
   design, and the user accepts that.
2. **Token-cost warning.** After counting the total reviews to be classified, if it exceeds
   **500**, print this banner prominently and then **continue without waiting** (this skill is
   non-interactive — the user can Ctrl-C to abort):
   ```
   ⚠️  THIS WORKFLOW WILL BE VERY TOKEN EXPENSIVE DUE TO LARGE NUMBER OF REVIEWS
       (<N> reviews → roughly <N/12> classifier agents). Cancel now if you don't want this.
   ```
3. **Non-interactive.** Do not stop to ask the user questions mid-run. Make sensible defaults,
   state them, and proceed. (Only exception: the one-time Apify setup, if it isn't connected.)
4. **Both platforms, in full.** Harvest *all* Google and *all* TripAdvisor reviews. Don't stop at
   page 1; don't rely on aggregator summaries.

## Prerequisite: Apify (one-time setup)

Reviews are harvested with [Apify](https://apify.com) scrapers via its MCP server. Google Maps
hides reviews behind a cookie-consent wall that defeats plain fetching, and TripAdvisor paginates
awkwardly — Apify solves both and returns clean structured data with real timestamps.

**Check if it's already connected:** run `claude mcp list` (look for `apify ... ✔ Connected`) or
search for the tools (`mcp__apify__call-actor`, `mcp__apify__get-dataset-items`). If connected,
skip ahead.

**If not connected, walk the user through this (it's quick and cheap — free tier covers normal
use; the Google reviews scraper is ~$0.0006/review):**

1. Create a free account at **https://apify.com**.
2. Copy the API token from **https://console.apify.com/settings/integrations** (`apify_api_…`).
3. Add the MCP server (token in the header avoids the OAuth dialog):
   ```
   claude mcp add --transport http apify https://mcp.apify.com --header "Authorization: Bearer apify_api_YOUR_TOKEN"
   ```
   (Alternatively `claude mcp add --transport http apify https://mcp.apify.com` then `/mcp` →
   select **apify** → complete the browser OAuth.)
4. **Restart Claude Code, resuming the session:** `claude --continue`. A *running* session does
   **not** hot-load a newly added MCP server — this restart is required, or the tools won't appear.
5. Verify with `claude mcp list` → `apify … ✔ Connected`, then re-invoke this skill.

## Procedure

### Step 0 — Interpret the argument (`$ARGUMENTS`)

- **Explicit list** ("Golden Dragon, Happy Palace and Imperial Wok in Diss") → use those names +
  their locality.
- **Category + place** ("Indian restaurants in Norwich") → first *discover* the outlets. Use the
  Apify Google Maps places scraper to enumerate candidates:
  `mcp__apify__call-actor` with actor `compass/crawler-google-places`, input
  `{ "searchStringsArray": ["Indian restaurant Norwich"], "maxCrawledPlaces": 30, "language": "en" }`,
  then read the dataset (`mcp__apify__get-dataset-items`) and keep the genuine matches of that
  cuisine (use `title`, `categoryName`, `reviewsCount`, `url`/`placeId`, `totalScore`). Present
  the resolved list (name, area, Google review count) and proceed.

Record for each outlet: a clean **name**, its **Google Maps place URL or placeId**, and (if
found) its **TripAdvisor review-page URL**.

### Step 1 — Harvest ALL reviews (Apify)

Always `mcp__apify__fetch-actor-details` (output `{inputSchema:true,pricing:true}`) before running
an actor, then `call-actor` with `waitSecs:0` (fire-and-forget) and poll with
`get-actor-run` (`waitSecs:45`) until `SUCCEEDED`.

**Google** — actor `compass/google-maps-reviews-scraper` (verify via `search-actors "Google Maps reviews"`):
```json
{ "startUrls": [{"url":"https://www.google.com/maps/search/<Name+Full+Address>"}, ...],
  "maxReviews": 1000, "reviewsSort": "newest", "language": "en",
  "reviewsOrigin": "google", "personalData": true }
```
`reviewsOrigin:"google"` keeps Google-native reviews only (so they don't duplicate TripAdvisor).
Pass one `startUrl` per outlet; a `/maps/search/` URL with the **full name + street + town +
postcode** resolves to the right place. After it finishes, **verify** each place via a compact
`get-dataset-items` projection (`title,address,city,postalCode,reviewsCount,totalScore`) — confirm
the address/town matches and that scraped count ≈ `reviewsCount`.

**TripAdvisor** — find the actor with `search-actors "Tripadvisor reviews"` (e.g.
`maxcopell/tripadvisor-reviews`), read its input schema, and run it with each outlet's TripAdvisor
URL (or let it search by name+location), pulling **all** reviews. If no TripAdvisor actor is
available, fall back to `WebFetch` on the TripAdvisor review pages, paginating with the `-orN-`
offset in the URL (e.g. `…-Reviews-or10-…`, `-or20-`, …) and de-duplicating.

**Get the data onto disk.** `get-dataset-items` results that exceed the token cap are written to a
file automatically (the tool tells you the path) — that's the easiest route. Otherwise project
fields and page through. You need at least: review **text**, **stars/rating**, **publish date**
(for the year), reviewer **name**, and which **outlet** + **source** it belongs to.

### Step 2 — Normalise, count, batch (Python)

Write a small Python script (in the scratchpad) that loads the harvested files and produces, for
every review, a record: `{id, restaurant, reviewer, rating(1-5), year(int), title, text, source}`.
Map each Google place `title`/`placeId` to its outlet name; derive `year` from the publish
timestamp (convert any relative dates against today). De-duplicate by review id / (name+text).

- Split reviews **with text** into batch files of ~12 (`batch_000.json` …), each an array of
  `{id, restaurant, rating, title, text}`.
- Reviews **without text** → a `rating_only.json` list (classified deterministically, not by an
  agent).

**Print the total and the Step-1 §HARD RULE 2 warning if > 500.**

### Step 3 — Classify EVERY text review with parallel agents (Workflow)

Use the **Workflow** tool. One agent per batch file: it `Read`s the file and classifies each
review against the rubric, returning structured output. Use the **session model at high effort**
— do **not** pass a cheaper `model`. (Implementation note: pass the batch-file paths by embedding
them as a literal array in the workflow script; the `args` global has been unreliable.)

Give each classifier agent the **exact rubric** from "The taxonomy" above, and this schema per
review: `{id, cluster (the 6 content clusters), sentiment (positive|negative|mixed),
authenticity_signal (literate|anglicised|neutral), dishes_mentioned (string[]), rationale}`.

Sketch:
```js
const paths = [ "/abs/batch_000.json", /* …embed every batch path here… */ ];
const RUBRIC = `…the taxonomy text, verbatim…`;
const CLASSIFY_SCHEMA = { /* object with classifications: [{id,cluster,sentiment,authenticity_signal,dishes_mentioned,rationale}] */ };
phase('Classify');
const results = await parallel(paths.map((p,i) => () =>
  agent(`${RUBRIC}\nRead the JSON file at:\n${p}\nIt is an array of reviews (id, restaurant, rating, title, text). Classify EVERY one, keyed by id.`,
    { label:`classify:${i}`, phase:'Classify', agentType:'general-purpose', effort:'high', schema:CLASSIFY_SCHEMA })
    .then(r => (r && r.classifications) || []) ));
return { classifications: results.filter(Boolean).flat() };
```
For categories with many outlets you may have a separate harvest workflow too, but classification
is the part that MUST fan out one-agent-reads-each.

### Step 4 — Merge & aggregate (Python)

Join the classifications back to the review records by `id`. Add the `rating_only` reviews
(cluster `rating_only`; sentiment from stars: ≥4 positive, 3 mixed, ≤2 negative). Then compute:

- `counts.byCluster`, `counts.byRestaurant` (name → cluster → n), `counts.bySource`
- `summary`: `dismissible` (= non_food + unsophisticated), `credible_negative`, `positive`
  (= authentic + generic), `rating_only`, `mixed`
- `histogram`: `years` (sorted), `overall` (year → cluster → n), `byRestaurant` (name → year → cluster → n)
- `restaurants[]`: `{name, score_label (e.g. "Google 4.5★"), total, badge, take}` — **you write**
  the one-line `badge` and 1–2-sentence `take` per outlet from its cluster mix + notable quotes.
- `quotes[]`: ~6–8 hand-picked verbatim examples per pile (authentic_praise, credible_food_complaint,
  and the discountable non_food/unsophisticated), each `{reviewer, restaurant, year, rating, text,
  cluster, source}`. Truncate text to ~320 chars.
- `meta`: `{scope (e.g. "Indian restaurants in Norwich"), total, date, sources (labels)}`

Write it all to `combined.json` in the schema documented at the top of `scripts/build_report.py`.

### Step 5 — Build the report & publish

Run the bundled generator (vertical layout + ranked leaderboard, scales to any number of
outlets). It ships alongside this skill at `scripts/build_report.py` — resolve that path relative
to this SKILL.md's own directory (the skill's plugin install location):
```
python3 <this-skill-dir>/scripts/build_report.py <combined.json> <out.html>
```
Then publish with the **Artifact** tool (favicon a food emoji, e.g. 🍜/🥡/🍛). Give the user the
URL plus a tight prose summary: the ranked top picks, the totals (dismissible / credible / positive
/ star-only), and any surprises (e.g. an outlet that looks great on stars but hides a food-safety
pattern). Always restate the caveats: classification is automated ("~this many"), and star-only
ratings are bucketed separately.

## Notes

- The report's design is a warm "world-cuisine menu" identity (lacquer red, gold, ink, jade).
  It is intentionally self-contained (inline CSS/JS) so the Artifact CSP is satisfied.
- Keep the cluster names and definitions stable across runs — they're the durable, reusable part.
- If Apify credit/permissions block a scrape, report exactly what failed and what was still
  obtained; never silently fall back to a thin aggregator sample and present it as complete.
