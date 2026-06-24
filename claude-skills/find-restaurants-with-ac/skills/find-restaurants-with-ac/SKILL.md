---
name: find-restaurants-with-ac
description: >-
  Find which restaurants/takeaways have air conditioning. For each outlet, answer ONE binary
  fact - does it have AC? - with a confidence score (how much to trust the answer, from the
  number, recency and type of sources), by reading the outlet's website verbatim, its Google/
  TripAdvisor amenity listing, articles and (especially) reviews, with parallel full-strength
  agents (one per outlet). Produces a super-simple ranked table: "Has AC" + confidence.
  Use when the user wants air-conditioned places among an explicit list OR a category +
  location (e.g. "restaurants in Diss", "cafes in Norwich", "restaurants in Seville").
argument-hint: '<explicit restaurant list> | "<category> in <place>"'
---

# find-restaurants-with-ac

Find the places that have **air conditioning**, for a diner who wants to stay cool. One fact per
outlet — **Has AC?** — `yes` / `no` / `unknown`, each with a **confidence** score. Output is a
super-simple ranked table. (Sibling of `find-ultra-quality-food` / `food-review-review`, same
machinery, one fact.)

## The one fact

**`ac` — does this outlet have air conditioning?**
- `yes` — there is AC (cooling), per a credible source.
- `no` — confirmed to have NO AC (e.g. reviews complaining it was sweltering / "no aircon").
- `unknown` — **no evidence either way.** The honest verdict; never a guess and never a stand-in
  for `no`.

## Confidence score (0–100)

Confidence is **how much to trust the verdict**, from the **number** of sources, their **recency**
(old sources discounted — AC can be installed or removed), and their **type**, dropped when sources
**conflict**:

- **90–100** — explicitly on the **official website** OR a verified **amenity listing** (Google
  Maps / TripAdvisor "Air-conditioned"), ideally corroborated by a recent review.
- **75–90** — **multiple independent, recent** (≤~2 yrs) reviews consistently mention AC (or its
  absence).
- **50–65** — a **single** recent credible source.
- **10–25** — only **old** sources (>~3 yrs), e.g. a lone review from years ago.
- **≤40, and pick the most recent/authoritative verdict** — when sources **conflict** (an old
  review says AC, a recent one says "no aircon, boiling" → follow the recent one, keep confidence
  low).
- **0 / `unknown`** — nothing found.

Reviews are the richest source for AC and cut **both** ways: "lovely and cool with the air con" is
evidence for `yes`; "no air conditioning, absolutely sweltering" is evidence for `no`. Do **not**
infer AC from generic priors ("modern chains usually have it"); with no source for *this* outlet the
verdict is `unknown`.

## HARD RULES (do not violate)

1. **No evidence ⇒ `unknown`. Never a default `no`.** Defaulting a not-found fact to `no` is a
   silent failure and is banned. Never flip a verdict on a hunch.
2. **Every non-`unknown` verdict MUST cite evidence** — ≥1 `{text, source, date, url?}` item with a
   real quote/paraphrase, each tagged with its **date** (so recency can be weighed). No citation ⇒
   `unknown`.
3. **Use the website verbatim + reviews.** Quote the outlet's own words where it states AC; mine
   reviews (with dates) for cool/sweltering mentions. A recent source overrides a stale one.
4. **Read real sources with real agents.** Each outlet is researched by a full-strength agent that
   fetches the website, checks the Google/TripAdvisor amenity listing, searches articles, and does a
   review pass. **Never** substitute a cheaper model and **never** fabricate from the name alone.
5. **One agent per outlet, in parallel** (see the workflow).
6. **Scale warning.** After resolving the outlet list, if it exceeds **60** outlets, print this and
   **continue without waiting** (this skill is non-interactive):
   ```
   ⚠️  LARGE SET: <N> outlets → <N> research agents, each doing several web fetches.
       This will take a while and use significant tokens. Ctrl-C now to abort.
   ```
7. **Non-interactive.** Don't stop to ask questions mid-run. Pick sensible defaults, state them,
   proceed. (Only exception: the one-time Apify setup, if you choose to use it.)

## Tools you'll use

- **WebSearch / WebFetch** (built-in) — the primary engine: official site, the Google Maps /
  TripAdvisor listing, local articles, and review snippets (`"<name> <town>" (air conditioning OR
  aircon OR "air con" OR sweltering OR "too hot")`).
- **Apify MCP (recommended here, for reviews + the amenity attribute)** — Google hides reviews
  behind a consent wall. The **same** server `find-ultra-quality-food` uses. Two uses:
  1. **Discovery + amenity signal:** the Google Maps places scraper (`compass/crawler-google-places`)
     returns each place's `additionalInfo.Amenities."Air conditioning"` (and `…Highlights`) flag when
     present — a citable `amenity` source. Project it in Step 0 and pass it to the agent.
  2. **Reviews:** pull recent reviews and scan for AC/heat mentions **with their dates**.
  If Apify isn't connected, fall back to website + review snippets via WebSearch.
  - Setup: `claude mcp add --transport http apify https://mcp.apify.com --header "Authorization: Bearer apify_api_YOUR_TOKEN"` (token from
    https://console.apify.com/settings/integrations), then **restart** (`claude --continue`).

## Procedure

### Step 0 — Resolve the outlet list (`$ARGUMENTS`)

- **Explicit list** → use those names + localities.
- **Category + place** ("restaurants in Diss", "cafes in Norwich") → **discover** the outlets first.
  Prefer the Apify Google Maps places scraper — `call-actor` with `compass/crawler-google-places`,
  input `{ "searchStringsArray": ["restaurants <place>","cafe <place>","takeaway <place>", …], "maxCrawledPlaces": 100, "language": "en", "countryCode": "gb" }`,
  then `get-dataset-items` projecting
  `title,categoryName,address,postalCode,city,website,url,placeId,additionalInfo.Amenities.Air conditioning`.
  Keep genuine food outlets in the target locality (filter by `city`/postcode), drop non-food and
  permanently-closed. If Apify isn't available, enumerate via WebSearch + a local directory.

Record per outlet: **name**, **area/town**, **website** if known, and the **Google AC amenity flag**
if the listing has it. Print the resolved list and the HARD RULE 6 warning if > 60.

### Step 1 + 2 — Research & verdict per outlet (inside the fan-out agent)

For each outlet the agent:

1. **Website** — WebSearch "`<name> <town>`" → WebFetch the official site; look for any AC / "air
   conditioned" / "climate controlled" mention (quote it verbatim).
2. **Amenity listing** — the Google AC amenity flag (passed in) and/or the TripAdvisor amenities
   section. A set "Air-conditioned" flag is a citable `amenity` source.
3. **Articles** — WebSearch local coverage mentioning the interior/comfort.
4. **Reviews** — Apify review pull *or* WebSearch review snippets; scan for AC vs heat mentions,
   **noting each mention's date** (recent reviews weigh most; they cut both ways).

Then set the `ac` verdict (`yes`/`no`/`unknown`) from the most recent, authoritative evidence, plus
a `confidence` per the scale, attaching dated evidence items. Nothing found ⇒ `unknown`,
confidence 0, empty evidence.

### Step 3 — Fan out one agent per outlet (Workflow)

Use the **Workflow** tool. One agent per outlet, full strength (do **not** pass a cheaper `model`),
`agentType: 'general-purpose'`. Embed the outlet list as a literal array (the `args` global has been
unreliable), pass each outlet's Google AC amenity flag, and tell the agents **today's date** for
recency.

```js
const OUTLETS = [ { name: "...", area: "...", website: "...", google_ac: true|false }, /* …embed all… */ ];
const TODAY = "2026-06-24";  // pass the real current date
const RUBRIC = `…paste "The one fact" + "Confidence score" verbatim…`;
const EV = { type:'array', items:{ type:'object', required:['text','source','date'], properties:{
  text:{type:'string'}, source:{enum:['website','amenity','article','review']},
  date:{type:'string'}, url:{type:'string'} } } };
const VERDICT_SCHEMA = { type:'object', required:['name','ac'], properties:{
  name:{type:'string'}, area:{type:'string'}, website:{type:'string'},
  ac:{ type:'object', required:['verdict','confidence','evidence'], properties:{
    verdict:{enum:['yes','no','unknown']}, confidence:{type:'integer',minimum:0,maximum:100}, evidence:EV } },
  take:{type:'string'} } };
phase('Research');
const verdicts = await parallel(OUTLETS.map((o) => () =>
  agent(`${RUBRIC}\n\nToday is ${TODAY}. Research this ONE outlet: does it have air conditioning?\n` +
        `Name: ${o.name}\nArea: ${o.area}\nKnown website: ${o.website || '(find it)'}\n` +
        `Google "Air conditioning" amenity flag: ${o.google_ac ? 'SET (a citable amenity source - corroborate it)' : 'not set'}\n` +
        `Fetch the website (quote any AC mention verbatim), check the amenity listing, search articles, do a dated review pass (AC vs heat mentions cut both ways). ` +
        `Set verdict from the most recent/authoritative evidence; set confidence from number+recency+type, dropping it on conflict. ` +
        `Cite a dated quote+source for any non-"unknown" verdict. No evidence ⇒ verdict "unknown", confidence 0, evidence []. Never guess.`,
    { label: `ac:${o.name}`.slice(0,60), phase:'Research', agentType:'general-purpose', effort:'high', schema:VERDICT_SCHEMA })));
return { restaurants: verdicts.filter(Boolean) };
```

### Step 4 — Assemble `combined.json`

Write `combined.json` matching the schema at the top of `scripts/build_report.py`:
`{ meta: {scope, date, sources, notes?}, restaurants: [ {name, area, website?, ac, take?} ] }`.
Use today's date for `meta.date`.

### Step 5 — Build the report & publish

```
python3 <this-skill-dir>/scripts/build_report.py <combined.json> <out.html>
```
(`<this-skill-dir>` = the directory containing this SKILL.md.) It produces a super-simple table:
**Has AC** (by confidence) → **No AC**, with a faded pill for low-confidence verdicts; outlets with
no AC mention at all drop into a compact "no AC info found" table at the foot. Publish with the
**Artifact** tool (favicon ❄️ / 🧊). Give the user the URL plus a one-line summary: how many have AC
(and how confidently), how many confirmed none, and how many came back `unknown`.

## Notes

- **`unknown` is a feature, not a gap.** Most small-town outlets won't mention AC anywhere; an honest
  pile of `unknown`s is correct — quietly marking them `no` would be a lie.
- Keep the verdict set, the confidence scale, and the source types **stable across runs** — they are
  the durable contract (shared with `build_report.py`).
- Reviews are the strongest AC signal; weigh the **most recent** ones, since AC gets installed and
  units break.
