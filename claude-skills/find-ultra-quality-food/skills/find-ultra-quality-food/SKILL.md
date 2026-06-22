---
name: find-ultra-quality-food
description: >-
  Audit a set of restaurants/takeaways for two specific health-quality facts about HOW the
  food is made - (1) are there seed-oil-free options? and (2) are there organic options? -
  by reading their websites, menus (downloading and OCR'ing PDFs/images), articles and a
  light pass of reviews with parallel full-strength agents (one per outlet). Each fact gets
  a yes/no/unknown verdict plus a reliability score (how much to trust it, from the number,
  recency and type of sources). Produces a ranked, easy-to-scan HTML report. Use when the
  user wants to find seed-oil-free and/or organic places among an explicit list OR a category
  + location (e.g. "Indian restaurants in Norwich", "all takeaways in Diss", "restaurants in
  Paris"). Narrower and cheaper than food-review-review - it extracts two binary facts, it
  does not cluster every review.
argument-hint: '<explicit restaurant list> | "<category> in <place>"'
---

# find-ultra-quality-food

Find the places whose food is **ultra quality in the specific sense of how it is cooked and
sourced** — seed-oil-free and/or organic — for a health-conscious diner who avoids industrial
seed oils and prefers organic. This is **not** a taste/authenticity audit (that's
`food-review-review`); it extracts two factual signals and presents them as a clean, ranked report.

## The two facts (each a binary verdict + a reliability score)

For each outlet, answer two independent questions. Each gets a **verdict** of `yes` / `no` /
`unknown` and a **reliability** score (0–100) — how much to trust that verdict.

1. **`seed_oil_free` — is there a way to eat here WITHOUT seed oil?** `yes` if it fries/cooks
   entirely in non-seed fats **OR** offers a clear seed-oil-free option a diner can choose (e.g. a
   beef-dripping fryer alongside the vegetable-oil one — *both* count as `yes`, the distinction is
   deliberately collapsed). `no` if it cooks in seed/"vegetable" oil with no seed-oil-free option.
2. **`organic` — are there organic options?** `yes` if any organic dishes/ingredients are offered,
   `no` if not.

`unknown` means **no evidence was found** — it is the honest verdict, never a guess and never a
stand-in for `no`.

### What counts as a seed oil

- **Seed oils — the ones being avoided:** rapeseed/canola, sunflower, safflower, soybean, corn,
  cottonseed, grapeseed, rice-bran, and generic **"vegetable oil"** / **"blended oil"** (in the UK
  almost always rapeseed or a rapeseed/soy blend — treat as seed oil).
- **NOT seed oils — frying only in these ⇒ `seed_oil_free: yes`:** beef dripping, beef tallow,
  lard, duck/goose fat, butter, ghee/clarified butter, **olive oil**, **coconut oil**, **avocado
  oil**, palm oil (animal fats or fruit/drupe oils).
- **Borderline — peanut/groundnut oil:** a legume oil, not a true seed oil, but many avoiders
  exclude it. Frying only in peanut oil ⇒ `yes`, but **say so in the evidence** ("peanut oil —
  legume, not a seed oil").

## Reliability score (0–100)

Reliability is **how much to trust the verdict**, NOT how good the outlet is. Set it from the
**number** of sources, their **recency** (old sources are heavily discounted), and their **type**
(official site/menu > article > review), and **drop it when sources disagree**:

- **90–100** — explicitly stated on the **official website/menu** (or documented chain policy),
  current; ideally corroborated.
- **75–90** — **multiple independent, recent** (≤~2 yrs) reviewers/articles agree.
- **50–65** — a **single** recent credible source.
- **~10–25** — only **old** sources (>~3 yrs), e.g. a single review from years ago. (A lone 2018
  "fries in dripping" review on a 2026 run ⇒ ~10%.)
- **≤40, and pick the most recent/authoritative verdict** — when sources **conflict** (e.g. an old
  review says dripping but a recent one says vegetable oil). Recent, authoritative evidence wins
  the verdict; the disagreement keeps reliability low.
- **0 / `unknown`** — nothing found.

Do **not** invent reliability from generic priors ("chip shops often use dripping"). A regional
prior is not evidence; with no source for *this* outlet the verdict is `unknown`.

## HARD RULES (do not violate)

1. **No evidence ⇒ `unknown`. Never a default negative.** Defaulting a not-found fact to "uses seed
   oil" / "no organic" is a silent failure and is banned. Never flip a verdict on a hunch.
2. **Every non-`unknown` verdict MUST cite evidence** — ≥1 `{text, source, date, url?}` item with a
   real quote/paraphrase from a page you fetched, each tagged with its **date** (so recency can be
   weighed). No citation ⇒ `unknown`.
3. **Weigh recency and conflicts.** Old sources are weak; a recent source overrides a stale one.
   Reflect this in BOTH the verdict and the reliability (see the scale above).
4. **Read real sources with real agents.** Each outlet is researched by a full-strength agent that
   actually fetches the website, finds and **downloads + reads the menu** (OCR'ing PDF/image menus),
   searches articles, and does a light review pass. **Never** substitute a cheaper model and
   **never** fabricate facts from the name alone.
5. **One agent per outlet, in parallel** (see the workflow). Far cheaper than `food-review-review`.
6. **Scale warning.** After resolving the outlet list, if it exceeds **60** outlets, print this and
   **continue without waiting** (this skill is non-interactive):
   ```
   ⚠️  LARGE SET: <N> outlets → <N> research agents, each doing several web fetches.
       This will take a while and use significant tokens. Ctrl-C now to abort.
   ```
7. **Non-interactive.** Don't stop to ask questions mid-run. Pick sensible defaults, state them,
   proceed. (Only exception: the one-time Apify setup, and only if you choose to use it.)

## Tools you'll use

- **WebSearch / WebFetch** (built-in) — the primary engine: official site, menu links, local news /
  "best beef-dripping chippy" listicles, blog posts, and review snippets.
- **Bash + Read** — for menus that are **PDFs or images**: `curl -sL "<url>" -o menu.pdf` then
  `Read` the PDF (or `pdftotext menu.pdf -`); for an image menu, `curl` it then `Read` for OCR. A
  menu's frying-oil / "all our beef is organic" line is often the single best 100% source.
- **Apify MCP (optional, for the review signal)** — Google hides reviews behind a consent wall, so to
  mine reviews for oil/organic mentions you may want Apify (the **same** server `food-review-review`
  uses). Only a *light* pass is needed: pull reviews and scan for oil/fat/organic mentions **with
  their dates** (recency matters). If Apify isn't connected, rely on website/menu/articles + review
  snippets from WebSearch.
  - Setup: `claude mcp add --transport http apify https://mcp.apify.com --header "Authorization: Bearer apify_api_YOUR_TOKEN"` (token from
    https://console.apify.com/settings/integrations), then **restart** (`claude --continue`).

## Procedure

### Step 0 — Resolve the outlet list (`$ARGUMENTS`)

- **Explicit list** ("Tom's Fish Bar Mulbarton and Mr Chips Diss") → use those names + localities.
- **Category + place** ("all takeaways in Diss", "Indian restaurants in Norwich") → **discover** the
  outlets first. Prefer the Apify Google Maps places scraper if connected — `call-actor` with
  `compass/crawler-google-places`, input
  `{ "searchStringsArray": ["restaurants <place>","takeaway <place>","fish and chips <place>", …per-cuisine…], "maxCrawledPlaces": 100, "language": "en", "countryCode": "gb" }` —
  then read the dataset (`get-dataset-items`, fields
  `title,categoryName,address,postalCode,city,website,menu,url,placeId`). Keep the genuine food
  outlets in the target locality (filter by `city`/postcode), drop non-food (barbers, shops) and
  permanently-closed. If Apify isn't available, enumerate via **WebSearch** + a local directory and
  de-duplicate by name.

Record per outlet: clean **name**, **area/town**, and **website URL** if known. Print the resolved
list and the HARD RULE 6 warning if > 60. Then proceed.

### Step 1 + 2 — Research & verdict per outlet (inside the fan-out agent)

For each outlet the agent:

1. **Website** — WebSearch "`<name> <town>`" → WebFetch the official site (about / "our oil" /
   sustainability / allergen pages).
2. **Menu** — find it (often a separate PDF/image), **download and read it** (PDF via `Read` /
   `pdftotext`; image via `Read` for OCR). Look for the frying medium and "organic" sourcing lines.
3. **Articles** — WebSearch `"<name> <town>" (beef dripping OR "vegetable oil" OR "seed oil" OR organic)`
   and local-press / food-blog coverage.
4. **Reviews (light)** — Apify review pull *or* WebSearch review snippets; scan for the oil/fat or
   organic sourcing, **noting each mention's date**.

Then set each fact's **verdict** (`yes`/`no`/`unknown`) from the most recent, authoritative
evidence, plus a **reliability** per the scale, attaching dated evidence items. Nothing found for a
fact ⇒ `unknown`, reliability 0, empty evidence.

### Step 3 — Fan out one agent per outlet (Workflow)

Use the **Workflow** tool. One agent per outlet, full strength (do **not** pass a cheaper `model`),
`agentType: 'general-purpose'` so it has WebSearch/WebFetch/Bash/Read (and MCP via ToolSearch).
Embed the outlet list as a literal array in the script (the `args` global has been unreliable), and
tell the agents **today's date** so they can weigh recency.

```js
const OUTLETS = [ { name: "Mr Chips Diss", area: "Diss (IP22 4AB)", website: "…" }, /* …embed all… */ ];
const TODAY = "2026-06-22";  // pass the real current date
const RUBRIC = `…paste "The two facts" + "What counts as a seed oil" + "Reliability score" verbatim…`;
const EV = { type:'array', items:{ type:'object', required:['text','source','date'], properties:{
  text:{type:'string'}, source:{enum:['website','menu','article','review']},
  date:{type:'string'}, url:{type:'string'} } } };
const FACT = (verdicts) => ({ type:'object', required:['verdict','reliability','evidence'], properties:{
  verdict:{enum:verdicts}, reliability:{type:'integer',minimum:0,maximum:100}, evidence:EV } });
const VERDICT_SCHEMA = { type:'object', required:['name','seed_oil_free','organic'], properties:{
  name:{type:'string'}, area:{type:'string'}, website:{type:'string'},
  seed_oil_free: FACT(['yes','no','unknown']),
  organic:       FACT(['yes','no','unknown']),
  take:{type:'string'} } };
phase('Research');
const verdicts = await parallel(OUTLETS.map((o) => () =>
  agent(`${RUBRIC}\n\nToday is ${TODAY}. Research this single outlet and return its verdict.\n` +
        `Name: ${o.name}\nArea: ${o.area}\nKnown website: ${o.website || '(find it)'}\n` +
        `Fetch the website, find+download+read the menu (OCR PDFs/images), search articles, light review pass. ` +
        `Set each fact's verdict from the most recent/authoritative evidence; set reliability from number+recency+type of sources, dropping it on conflict. ` +
        `Cite a dated quote+source for every non-"unknown" verdict. No evidence ⇒ verdict "unknown", reliability 0, evidence []. Never guess.`,
    { label: `research:${o.name}`.slice(0,60), phase:'Research', agentType:'general-purpose', effort:'high', schema:VERDICT_SCHEMA })));
return { restaurants: verdicts.filter(Boolean) };
```

### Step 4 — Assemble `combined.json`

Take the workflow's `restaurants`, add a `meta` block, and write `combined.json` matching the schema
documented at the top of `scripts/build_report.py`:
`{ meta: {scope, date, sources, notes?}, restaurants: [ {name, area, website?, seed_oil_free, organic, take?} ] }`.
Use today's date for `meta.date`. Optionally write a one-line factual `take` per outlet.

### Step 5 — Build the report & publish

```
python3 <this-skill-dir>/scripts/build_report.py <combined.json> <out.html>
```
(`<this-skill-dir>` = the directory containing this SKILL.md.) Ranking rules (all in the generator):
- **`seed_oil_free` is the primary key; `organic` breaks ties.** Within each fact the order is
  `yes` → `no` → `unknown`, and within `yes` the **most reliable** outlet ranks first (so a
  strongly-evidenced seed-oil-free option beats a single old review).
- **A faded badge = low reliability** (badge opacity scales with the score), so a weak `yes`
  (e.g. one stale review) is visually obviously weak even though it's still a `yes`.
- **Outlets `unknown` on BOTH facts are not ranked** — they drop into a compressed "no published
  evidence" table at the foot, keeping the ranked cards scannable.

Publish with the **Artifact** tool (favicon a food emoji, e.g. 🍟/🥗/🫒). Give the user the URL plus
a tight summary: which places are seed-oil-free (and how reliably), which are confirmed seed-oil,
which have organic options, and how many came back `unknown` (and why — e.g. no website/menu online).

## Notes

- **`unknown` is a feature, not a gap.** A report full of honest `unknown`s for places with no web
  presence is correct; quietly marking them "uses seed oil" would be a lie.
- Keep the two facts, the seed-oil definitions, and the reliability scale **stable across runs** —
  they are the durable, reusable contract (shared with `build_report.py`).
- If Apify credit/permissions or a paywalled menu blocks a source, record what you *did* find and
  leave the rest `unknown`; never present an inferred verdict as if evidenced.
