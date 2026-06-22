# Claude Code Skills

Personal / shared [Claude Code](https://claude.com/claude-code) skills.

## Install

This repo is a Claude Code **plugin marketplace** (`.claude-plugin/marketplace.json` at the repo
root). Add it, then enable the plugins you want:

```jsonc
// ~/.claude/settings.json (or a project's .claude/settings.json)
{
  "extraKnownMarketplaces": {
    "samthebest": { "source": { "source": "git", "url": "git@github.com:samthebest/dump.git" } }
  },
  "enabledPlugins": {
    "setup-claude-token-optimiser@samthebest": true,
    "food-review-review@samthebest": true
  }
}
```

Or interactively: `/plugin marketplace add https://github.com/samthebest/dump` then
`/plugin install <name>@samthebest`. The skills' slash commands then work
(e.g. `/setup-claude-token-optimiser`).

**Without a marketplace**, just copy one skill into `~/.claude/skills/`:

```bash
cp -R claude-skills/setup-claude-token-optimiser/skills/setup-claude-token-optimiser ~/.claude/skills/
```

## Skills

### setup-claude-token-optimiser

`/setup-claude-token-optimiser` — interactively sets up a scheduled "kick" that opens Claude
usage windows on a fixed clock, so a developer who spreads work across the day can straddle the
5‑hour usage‑window resets and unlock extra token budget (up to ~2× on a heavy session). It
explains the mechanic and **when it helps** (≤5h days — half‑days/weekends — or 2+ spread sessions
with a long break) vs **when it doesn't** (one continuous block >5h, where you'd cross a reset
anyway). Sets up the kick script + crontab non‑interactively, and on macOS the state‑aware
`offsleep`/`onsleep` + a boot LaunchDaemon (handing you the sudo commands to run). macOS‑focused
(cron + `pmset`/`launchd`).

### food-review-review

`/food-review-review <restaurants | "<cuisine> in <place>">` — rank restaurants by genuine food
quality and authenticity from **every** Google + TripAdvisor review (harvested via Apify),
classifying each with parallel full‑strength agents (never sampling, never ML clustering).
Discounts non‑food and unsophisticated‑palate complaints; produces a ranked visual HTML report.
Requires the Apify MCP (setup walkthrough in the skill).

> ⚠️ **Heavy token usage.** Reads every review with a full‑strength agent → easily millions of
> tokens per run. Prefer a personal account / API key, or run it at the end of your weekly
> usage‑reset to burn tokens you'd otherwise lose.

### find-ultra-quality-food

`/find-ultra-quality-food <restaurants | "<category> in <place>">` — audit restaurants/takeaways
for a few specific **health‑quality facts** about how the food is made: (1) uses **no seed oil**,
(2) offers a **clear seed‑oil‑free option**, (3) offers **organic options**, (4) is **only
organic**. Each fact gets a verdict + confidence (100% = stated on the official site/menu, 80% =
multiple reviewers say so), gathered by full‑strength agents (one per outlet) that read websites,
**download and OCR menus** (PDF/image), articles, and a light pass of reviews. Output is a ranked,
easy‑to‑scan HTML matrix. Narrower and **much cheaper** than `food-review-review` — it extracts a
few binary facts rather than clustering every review. The review pass can use the same Apify MCP,
but it's optional (WebSearch/WebFetch carry most of it).
