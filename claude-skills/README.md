# Claude Code Skills

Personal / shared [Claude Code](https://claude.com/claude-code) skills.

## Install

Copy (or symlink) a skill directory into `~/.claude/skills/`:

```bash
cp -R claude-skills/setup-claude-token-optimiser ~/.claude/skills/
```

Then invoke it in Claude Code by its slash command.

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
