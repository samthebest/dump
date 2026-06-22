---
name: find-ultra-quality-food
description: >-
  Audit a set of restaurants/takeaways for a few specific health-quality facts about HOW
  the food is made - (1) uses NO seed oil at all, (2) offers a clear seed-oil-free option,
  (3) offers organic options, (4) is ONLY organic - by reading their websites, menus
  (downloading and OCR'ing PDFs/images), articles and a light pass of reviews with parallel
  full-strength agents (one per outlet). Each fact gets a verdict + a confidence score
  (100% = stated on the official site/menu, 80% = multiple reviewers say so). Produces a
  ranked, easy-to-scan HTML matrix. Use when the user wants to find seed-oil-free and/or
  organic places among an explicit list OR a category + location (e.g. "Indian restaurants
  in Norwich", "all takeaways in Diss", "restaurants in Paris"). Narrower and cheaper than
  food-review-review - it extracts a few binary facts, it does not cluster every review.
argument-hint: '<explicit restaurant list> | "<category> in <place>"'
---

# find-ultra-quality-food

Find the places whose food is **ultra quality in the specific sense of how it is cooked and
sourced** — seed-oil-free and/or organic — for a health-conscious diner who avoids industrial
seed oils and prefers organic. This is **not** a taste/authenticity audit (that's
`food-review-review`); it extracts a handful of factual signals and presents them as a clean
matrix.

## The four facts (modelled as two ladders)

For each outlet we determine two things. Each is an ordered ladder, so the four binary facts the
user cares about fall out of one verdict per dimension and can never contradict each other.

**Seed-oil ladder** (best → worst):

| Rung | Meaning | Covers fact |
|---|---|---|
| `none` | Cooks in **zero** seed oil — only non-seed fats (beef dripping, tallow, lard, duck/goose fat, butter, ghee, olive, coconut). | **(1) uses no seed oil** |
| `avoidable` | Seed oil **is** used, but there is a **clear seed-oil-free option** the diner can choose (e.g. a dripping fryer alongside the vegetable-oil one). | **(2) clear seed-oil-free option** |
| `uses` | Cooks in seed / "vegetable" oil with **no** seed-oil-free option. | — |
| `unknown` | **No evidence found either way.** Not a guess, not a negative. | — |

**Organic ladder** (best → worst):

| Rung | Meaning | Covers fact |
|---|---|---|
| `only` | **Everything** is organic. | **(4) only organic** |
| `some` | **Some** organic dishes/ingredients are offered. | **(3) organic options** |
| `none` | No organic offering found. | — |
| `unknown` | **No evidence found either way.** | — |

### What counts as a seed oil

- **Seed oils — these are the ones being avoided:** rapeseed/canola, sunflower, safflower,
  soybean, corn, cottonseed, grapeseed, rice-bran, and generic **"vegetable oil"** / **"blended
  oil"** (in the UK these are almost always rapeseed or a rapeseed/soy blend — treat as seed oil).
- **NOT seed oils — a place using only these is `none`:** beef dripping, beef tallow, lard,
  duck/goose fat, butter, ghee/clarified butter, **olive oil**, **coconut oil**, **avocado oil**,
  palm oil (these are animal fats or fruit/drupe oils).
- **Borderline — peanut/groundnut oil:** a legume oil, not a true seed oil, but many seed-oil
  avoiders exclude it. If a place fries only in peanut oil, record `none` but **say so explicitly
  in the evidence** ("peanut oil — legume, not a seed oil") so the reader can judge.

## Confidence score (0–100)

Every non-`unknown` verdict carries a confidence and **at least one evidence item**:

- **100%** — explicitly stated on the **official website or menu** (or a documented chain-wide
  policy).
- **90%** — stated by the venue itself elsewhere (an official reply to a review, a verified
  social post, a menu PDF line item).
- **80%** — **multiple independent** reviewers/articles state it.
- **60%** — a **single** credible reviewer or article states it.
- **`unknown` / no number** — nothing was found. **This is the honest answer when there is no
  evidence — never round it down to "uses seed oil" or "not organic".**

Do **not** invent a confidence from generic priors ("chip shops often use dripping"). A regional
prior is not evidence; if you can't cite a source for *this* outlet, the rung is `unknown`.

## HARD RULES (do not violate)

1. **No evidence ⇒ `unknown`. Never a default negative.** Absence of evidence is its own valid
   verdict. Defaulting a not-found fact to "uses seed oil" / "not organic" is a silent failure and
   is banned. Likewise never upgrade a rung on a hunch.
2. **Every non-`unknown` rung MUST cite evidence** — at least one `{text, source, url?}` item with
   a real quote/paraphrase from a real page you fetched. No citation ⇒ downgrade to `unknown`.
3. **Read real sources with real agents.** Each outlet is researched by a full-strength agent that
   actually fetches its website, finds and **downloads + reads its menu** (including OCR of PDF/image
   menus), searches for articles, and does a light review pass. **Never** substitute a cheaper/smaller
   model and **never** fabricate facts from the name alone.
4. **One agent per outlet, in parallel** (see the workflow). This is far cheaper than
   `food-review-review` (no per-review fan-out) — but still warn (HARD RULE 5) on very large sets.
5. **Scale warning.** After resolving the outlet list, if it exceeds **60** outlets, print this and
   **continue without waiting** (this skill is non-interactive):
   ```
   ⚠️  LARGE SET: <N> outlets → <N> research agents, each doing several web fetches.
       This will take a while and use significant tokens. Ctrl-C now to abort.
   ```
6. **Non-interactive.** Don't stop to ask questions mid-run. Pick sensible defaults, state them,
   proceed. (Only exception: the one-time Apify setup, and only if you choose to use it.)

## Tools you'll use

- **WebSearch / WebFetch** (built-in) — the primary engine: find the official site, menu links,
  local news / "best beef-dripping chippy" listicles, blog posts, and review snippets.
- **Bash + Read** — to handle menus that are **PDFs or images**: `curl -sL "<url>" -o menu.pdf`
  then `Read` the PDF (or `pdftotext menu.pdf -` if available); for an image menu, `curl` it then
  `Read` the file so the vision model OCRs it. A menu's frying-oil / "all our beef is organic" note
  is often the single best 100% source.
- **Apify MCP (optional, for the review signal)** — Google hides reviews behind a consent wall, so
  to actually mine reviews for oil/organic mentions you may want Apify. It's the **same** server
  `food-review-review` uses. Only a *light* pass is needed here: pull reviews and grep them for
  oil/fat/organic mentions; you are not clustering them. If Apify isn't connected, you can skip it
  and rely on website/menu/articles + review snippets surfaced by WebSearch.
  - Setup, if you want it: `claude mcp add --transport http apify https://mcp.apify.com --header "Authorization: Bearer apify_api_YOUR_TOKEN"` (token from
    https://console.apify.com/settings/integrations), then **restart** (`claude --continue`).

## Procedure

### Step 0 — Resolve the outlet list (`$ARGUMENTS`)

- **Explicit list** ("Tom's Fish Bar Mulbarton and Mr Chips Diss") → use those names + localities.
- **Category + place** ("all takeaways in Diss", "Indian restaurants in Norwich") → **discover**
  the outlets first. Prefer the Apify Google Maps places scraper if connected — `call-actor` with
  `compass/crawler-google-places`, input
  `{ "searchStringsArray": ["restaurants Diss","takeaway Diss","fish and chips Diss"], "maxCrawledPlaces": 80, "language": "en" }` —
  then read the dataset (`get-dataset-items`, fields `title,categoryName,address,website,url,placeId`).
  If Apify isn't available, enumerate with **WebSearch** ("restaurants and takeaways in Diss",
  Google Maps listings, a local directory) and de-duplicate by name.

Record per outlet: clean **name**, **area/town**, and **website URL** if known. Print the resolved
list (name, area) and the HARD RULE 5 warning if > 60. Then proceed.

### Step 1 + 2 — Research & verdict per outlet (inside the fan-out agent)

For each outlet the agent:

1. **Website** — WebSearch "`<name> <town>`" → WebFetch the official site. Read the about /
   "our oil" / sustainability / allergen pages.
2. **Menu** — find the menu (often a separate PDF/image). **Download and read it** (PDF via `Read`
   / `pdftotext`; image via `Read` for OCR). Look for the frying medium and any "organic" sourcing
   lines.
3. **Articles** — WebSearch `"<name> <town>" (beef dripping OR "vegetable oil" OR "seed oil" OR organic)`
   and local-press / food-blog coverage.
4. **Reviews (light)** — Apify review pull *or* WebSearch review snippets; scan only for someone
   mentioning the oil/fat or organic sourcing. Multiple reviewers agreeing ⇒ 80%.

Then assign each ladder its highest **evidenced** rung + confidence, attaching the quote(s) +
source(s). Found nothing for a dimension ⇒ `unknown`, confidence 0, empty evidence.

### Step 3 — Fan out one agent per outlet (Workflow)

Use the **Workflow** tool. One agent per outlet, full strength (do **not** pass a cheaper `model`),
`agentType: 'general-purpose'` so it has WebSearch/WebFetch/Bash/Read (and MCP via ToolSearch).
Embed the outlet list as a literal array in the script (the `args` global has been unreliable).

```js
export const meta = {
  name: 'find-ultra-quality-food',
  description: 'Research seed-oil & organic facts for each outlet',
  phases: [{ title: 'Research' }],
};
const OUTLETS = [ { name: "Tom's Fish Bar", area: "Mulbarton, Norfolk" }, /* …embed all… */ ];
const RUBRIC = `…paste "The four facts" + "What counts as a seed oil" + "Confidence score" verbatim…`;
const VERDICT_SCHEMA = {
  type: 'object',
  required: ['name', 'seed_oil', 'organic'],
  properties: {
    name: { type: 'string' }, area: { type: 'string' }, website: { type: 'string' },
    seed_oil: { type: 'object', required: ['rung', 'confidence', 'evidence'], properties: {
      rung: { enum: ['none', 'avoidable', 'uses', 'unknown'] },
      confidence: { type: 'integer', minimum: 0, maximum: 100 },
      evidence: { type: 'array', items: { type: 'object', required: ['text', 'source'], properties: {
        text: { type: 'string' }, source: { enum: ['website', 'menu', 'article', 'review'] }, url: { type: 'string' } } } } } },
    organic: { type: 'object', required: ['rung', 'confidence', 'evidence'], properties: {
      rung: { enum: ['only', 'some', 'none', 'unknown'] },
      confidence: { type: 'integer', minimum: 0, maximum: 100 },
      evidence: { type: 'array', items: { type: 'object', required: ['text', 'source'], properties: {
        text: { type: 'string' }, source: { enum: ['website', 'menu', 'article', 'review'] }, url: { type: 'string' } } } } } },
    take: { type: 'string' },
  },
};
phase('Research');
const verdicts = await parallel(OUTLETS.map((o, i) => () =>
  agent(`${RUBRIC}\n\nResearch this single outlet and return its verdict.\nName: ${o.name}\nArea: ${o.area}\n` +
        `Fetch its website, find+download+read its menu (OCR PDFs/images), search articles, and do a light review pass. ` +
        `Cite a real quote+source for every non-"unknown" rung. No evidence for a dimension ⇒ rung "unknown", confidence 0, evidence []. Never guess.`,
    { label: `research:${o.name}`, phase: 'Research', agentType: 'general-purpose', effort: 'high', schema: VERDICT_SCHEMA })));
return { restaurants: verdicts.filter(Boolean) };
```

### Step 4 — Assemble `combined.json`

Take the workflow's `restaurants`, add a `meta` block, and write `combined.json` matching the schema
documented at the top of `scripts/build_report.py`:
`{ meta: {scope, date, sources, notes?}, restaurants: [ {name, area, website?, seed_oil, organic, take?} ] }`.
Use today's date for `meta.date`. Optionally write a one-line `take` per outlet from its evidence.

### Step 5 — Build the report & publish

```
python3 <this-skill-dir>/scripts/build_report.py <combined.json> <out.html>
```
(`<this-skill-dir>` = the directory containing this SKILL.md.) It produces a ranked matrix —
ranking rules (all handled by the generator):
- **`unknown` is the LOWEST rung** in each ladder, below a confirmed `uses`/`none`: a verified
  bad verdict outranks a place we know nothing about.
- **Outlets that are `unknown` on BOTH dimensions are not ranked** — they drop into a compressed
  "no published evidence" table at the foot, so the ranked cards stay scannable.
- **Confidence is weighted into the score** (seed oil dominates; good rungs score
  `base + 0.06×confidence`), so a higher-confidence weaker verdict can outrank a lower-confidence
  stronger one — e.g. a `avoidable`@80% ranks above a `none`@60%.

Publish with the
**Artifact** tool (favicon a food emoji, e.g. 🍟/🥗/🫒). Give the user the URL plus a tight summary:
which places are seed-oil-free, which have a seed-oil-free option, which are organic/only-organic,
and how many came back `unknown` (and why — e.g. no website/menu online).

## Notes

- **`unknown` is a feature, not a gap.** A report full of honest `unknown`s for places with no web
  presence is correct; a report that quietly marked them all "uses seed oil" would be a lie.
- Keep the ladder rungs and the seed-oil definitions **stable across runs** — they are the durable,
  reusable contract (shared with `build_report.py`).
- If Apify credit/permissions or a paywalled menu blocks a source, record what you *did* find and
  leave the rest `unknown`; never present an inferred verdict as if evidenced.
