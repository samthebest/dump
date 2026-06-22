---
name: setup-claude-token-optimiser
description: >-
  Invoked via /setup-claude-token-optimiser to interactively set up a "kick" heartbeat that
  optimises Claude usage-window tokens for a developer who spreads work across the day. It
  explains the 5-hour-window trick and when it does/doesn't help, asks the user about their work
  pattern and sleep preference (with yes/no options), computes an optimal cron schedule, installs
  the kick script + crontab (non-interactively), and — on macOS — sets up state-aware sleep
  disabling (offsleep/onsleep + a boot LaunchDaemon), handing the user the sudo commands to run.
disable-model-invocation: true
---

# /setup-claude-token-optimiser

Sets up a scheduled "kick" that opens Claude usage windows on a clock, so a developer who spreads
their work out across the day can straddle window resets and access extra token budget. This is an
**interactive** setup: explain the mechanic, ask questions, then install.

macOS-focused (cron + `pmset`/`launchd`). The kick+cron logic is portable to Linux, but the sleep
section is macOS-only — on Linux, note that and skip it (or suggest a `systemd` timer with
`Persistent=true`).

---

## STEP 1 — Explain the mechanic to the user (show this, in your own words but keep it accurate)

> **How it works.** Claude meters usage in a **rolling 5-hour window**: the window opens on your
> first message and grants a fixed token budget **B**; it closes exactly 5h later regardless of
> use, and the next message opens a fresh window with a fresh **B**.
>
> A tiny scheduled "kick" (a one-word `"Hi"` to headless Claude, costing ~nothing) **opens a window
> on a fixed clock, before you start work.** So when you begin, you're already partway through a
> window — and it **resets in the middle of your session**, opening a second window. One work
> session then spans **two** windows → up to **2× the budget** (`2B`).
>
> **The sweet spot** is the reset landing roughly mid-session (so you can actually burn budget on
> both sides). The kicks are spaced ~5h05m apart to tile your day with back-to-back windows.

Then state, clearly, **when it helps and when it doesn't**:

- ✅ **Short days (≤5h): half-days, weekend dabbling.** A single ≤5h session would otherwise sit in
  one self-anchored window; the kick shifts a reset into it → it spans two → **2×**.
- ✅ **Spread work: 2+ sessions/day with a real break (a few hours).** The clock-anchored windows
  put a reset inside each session.
- ❌ **One long continuous block >5h with only a short break.** You'd cross a window boundary anyway
  (any >5h stretch already spans 2 windows), so the kick adds **nothing**. Tell the user plainly
  that for this pattern the trick gives no benefit.
- ❌ **You never get near the cap.** It only raises the *ceiling* of a heavy session; if you don't
  hit `B`, it changes nothing (just costs the trivial kick).

It is most valuable for someone who takes **long breaks and spreads their hours** — that is the
explicit assumption.

---

## STEP 2 — Ask the user (use `AskUserQuestion`)

Ask these (combine into one `AskUserQuestion` call where sensible):

1. **Work pattern** — "Which best fits how you usually work?"
   - *Two or more sessions a day with a long (≈3h+) break* → ideal.
   - *Mostly short days (≤5h): half-days / weekends* → works well.
   - *One long continuous block, >5h, only short breaks* → **warn the trick won't help**; offer to
     stop, or set up only the kick at low cadence.
   - *It varies* → proceed, tuned to their times.
2. **Disable sleep?** — explain first: *"On macOS, `cron` does NOT run jobs while the Mac is asleep
   and never catches up. To guarantee the kicks fire, the Mac must stay awake. I can set up
   state-aware sleep disabling (an `offsleep`/`onsleep` pair + a boot daemon that restores your
   choice). This keeps the laptop awake 24/7 — best on AC power."* Options: **Yes, set up sleep
   control** / **No, cron only (accept missed kicks while asleep)**.

Then ask, in plain prose (free-form answers are fine):

3. **Your typical earliest work start and latest finish** (e.g. "10:00 to ~00:00"), and roughly
   **when/how long your break is** between sessions. Use these to compute the schedule.

If the user picked the ">5h continuous block" pattern, do not pretend it helps — confirm whether
they still want a minimal kick (e.g. one or two/day just to keep a window primed) or to abort.

---

## STEP 3 — Ensure the OAuth token

The kick runs headless, so it needs a Claude Code OAuth token at `~/.ssh/claude-token`.

- If `~/.ssh/claude-token` exists and is non-empty, use it.
- Otherwise tell the user to generate one: run `claude setup-token`, then save the printed token:
  `printf '%s' '<token>' > ~/.ssh/claude-token && chmod 600 ~/.ssh/claude-token`. This is the
  user's action (it prints a secret) — give them the commands; don't capture the token yourself.

---

## STEP 4 — Install the kick script

Resolve the Claude binary path: `command -v claude` (cron has a minimal PATH, so the script MUST
use the absolute path). Create `~/.claude/token-optimiser/kick-claude.sh` with this content,
substituting `__CLAUDE_BIN__` with the resolved path:

```bash
#!/bin/bash
ts() { date '+%Y-%m-%dT%H:%M:%S%z'; }
token=$(cat ~/.ssh/claude-token 2>/dev/null) || {
  echo "=== $(ts) kick FAILED: cannot read ~/.ssh/claude-token ==="
  exit 1
}
export CLAUDE_CODE_OAUTH_TOKEN="$token"
echo "=== $(ts) kick start ==="
echo "Hi" | __CLAUDE_BIN__ -p --model claude-haiku-4-5 --disallowedTools "*"
status=$?
echo "=== $(ts) kick end (exit $status) ==="
exit $status
```

`mkdir -p ~/.claude/token-optimiser && chmod +x` the script. `bash -n` it.

---

## STEP 5 — Compute schedule & update crontab (non-interactive — do this yourself)

Compute the kick times with the bundled helper (it places the first window `--lead` minutes — 180
by default — before the first start so the first reset lands ~2h in, then tiles every 5h05m):

```
python3 <this-skill-dir>/scripts/compute_schedule.py \
  --first-start <HH:MM> --last-end <HH:MM> \
  --script "$HOME/.claude/token-optimiser/kick-claude.sh" \
  --log    "$HOME/.claude/token-optimiser/kick-claude.log"
```

Use the **absolute** `$HOME`-expanded paths (cron runs via `/bin/sh`, which expands `$HOME`, but
absolute is safest — substitute the real home). Show the user the computed times and confirm.

Then update the crontab **non-interactively** (no editor, no sudo) — strip any prior kick lines
first so re-runs don't duplicate:

```
( crontab -l 2>/dev/null | grep -v 'token-optimiser/kick-claude.sh' ; \
  <the cron lines, without the comment lines> ) | crontab -
```

Verify with `crontab -l | grep kick-claude`.

---

## STEP 6 — Sleep control (only if the user said Yes; macOS)

Create three things. Substitute `__HOME__` with the absolute home path (the daemon runs as **root**,
so `$HOME` would be root's home — paths MUST be absolute).

**a) State-aware functions** in the user's shell profile (`~/.zshrc` if `$SHELL` is zsh, else
`~/.bash_profile`). Add only if not already present:

```bash
CLAUDE_SLEEP_STATE="$HOME/.claude/token-optimiser/sleep-disabled"
offsleep() { sudo pmset -a disablesleep 1 || return 1; mkdir -p "$(dirname "$CLAUDE_SLEEP_STATE")"; echo 1 > "$CLAUDE_SLEEP_STATE"; }
onsleep()  { sudo pmset -a disablesleep 0 || return 1; mkdir -p "$(dirname "$CLAUDE_SLEEP_STATE")"; echo 0 > "$CLAUDE_SLEEP_STATE"; }
```

**b) Boot apply-script** at `~/.claude/token-optimiser/apply-sleep-state.sh` (`chmod +x`, `bash -n`):

```bash
#!/bin/bash
ts() { date '+%Y-%m-%dT%H:%M:%S%z'; }
state_file="__HOME__/.claude/token-optimiser/sleep-disabled"
want=0
[ -r "$state_file" ] && want="$(cat "$state_file" 2>/dev/null)"
if [ "$want" = "1" ]; then
  /usr/bin/pmset -a disablesleep 1 && echo "=== $(ts) boot: re-applied disablesleep 1 ==="
else
  /usr/bin/pmset -a disablesleep 0 && echo "=== $(ts) boot: disablesleep 0 ==="
fi
```

**c) LaunchDaemon plist** at `~/.claude/token-optimiser/local.claude-token-optimiser.sleep.plist`
(validate with `plutil -lint`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>local.claude-token-optimiser.sleep</string>
    <key>ProgramArguments</key>
    <array>
        <string>/bin/bash</string>
        <string>__HOME__/.claude/token-optimiser/apply-sleep-state.sh</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>StandardOutPath</key>
    <string>__HOME__/.claude/token-optimiser/apply-sleep-state.log</string>
    <key>StandardErrorPath</key>
    <string>__HOME__/.claude/token-optimiser/apply-sleep-state.log</string>
</dict>
</plist>
```

Seed the state to match the user's intent (they're opting in, so `1`):
`mkdir -p ~/.claude/token-optimiser && echo 1 > ~/.claude/token-optimiser/sleep-disabled`.

Then **hand the user the sudo install block to run themselves** (this needs root — do NOT try to
run sudo yourself; present it and let them paste it):

```bash
sudo cp __HOME__/.claude/token-optimiser/local.claude-token-optimiser.sleep.plist /Library/LaunchDaemons/ \
 && sudo chown root:wheel /Library/LaunchDaemons/local.claude-token-optimiser.sleep.plist \
 && sudo chmod 644 /Library/LaunchDaemons/local.claude-token-optimiser.sleep.plist \
 && sudo launchctl bootstrap system /Library/LaunchDaemons/local.claude-token-optimiser.sleep.plist
```

Tell them: `bootstrap` loads it now and `RunAtLoad` fires it immediately; if they ever re-run the
block and see `Bootstrap failed: 5: Input/output error`, that just means it's already loaded
(harmless). Then `source` their profile so `offsleep`/`onsleep` are live, and run **`offsleep`**
once to actually disable sleep now.

---

## STEP 7 — Verify & summarise (read-only)

- `crontab -l | grep kick-claude` → the new lines.
- After they run the kick once (or the next slot fires): `tail ~/.claude/token-optimiser/kick-claude.log`
  → a `kick end (exit 0)` line.
- If sleep was set up: `cat ~/.claude/token-optimiser/apply-sleep-state.log` (boot line) and
  `pmset -g | grep SleepDisabled` (→ `1` after `offsleep`).

Summarise what was installed, the chosen kick times, and restate the caveat: this pays off only
when they spread work / take long breaks / do ≤5h days, and never for one continuous >5h block.

To uninstall later: remove the kick lines from `crontab -e`, and
`sudo launchctl bootout system/local.claude-token-optimiser.sleep && sudo rm /Library/LaunchDaemons/local.claude-token-optimiser.sleep.plist`.

---

## Hard rules

- **Interactive, but decisive.** Ask the questions in Step 2 and present the sudo block; otherwise
  proceed and make the non-sudo changes (script, crontab) yourself. Don't stall.
- **Never run `sudo` yourself** — hand sudo commands to the user.
- **Never read or echo the OAuth token** — the user creates the token file.
- **Be honest about applicability** — if their pattern is the ">5h continuous block", say it won't
  help rather than installing something pointless.
- **Idempotent** — strip prior kick lines before re-adding; add shell functions only if absent.
