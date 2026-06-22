#!/usr/bin/env python3
"""Compute kick cron times from a dev's work pattern.

Places one usage-window anchor `lead` minutes before the first work start (so the
first reset lands partway into the first session), then tiles further anchors every
5h05m (just past the 5h window expiry) across the working day.

Usage:
  compute_schedule.py --first-start 10:00 --last-end 00:00 \
      --script /Users/sam/.claude/token-optimiser/kick-claude.sh \
      --log    /Users/sam/.claude/token-optimiser/kick-claude.log
"""
import argparse

WINDOW_MIN = 300
SLACK_MIN = 5
STEP_MIN = WINDOW_MIN + SLACK_MIN


def to_min(hhmm):
    h, m = hhmm.strip().split(":")
    return int(h) * 60 + int(m)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--first-start", required=True, help="earliest typical work start, HH:MM 24h")
    ap.add_argument("--last-end", required=True, help="latest typical work end, HH:MM 24h (after midnight is fine)")
    ap.add_argument("--lead", type=int, default=180, help="minutes before first start to open the first window (default 180)")
    ap.add_argument("--script", required=True)
    ap.add_argument("--log", required=True)
    a = ap.parse_args()

    start = to_min(a.first_start)
    end = to_min(a.last_end)
    if end <= start:
        end += 1440

    anchor = start - a.lead
    kicks = []
    t = anchor
    while t <= end:
        kicks.append(t)
        t += STEP_MIN

    print("# kick-claude schedule (each line = one daily window anchor)")
    for t in kicks:
        hh = (t // 60) % 24
        mm = t % 60
        print(f"{mm} {hh} * * * {a.script} >> {a.log} 2>&1")

    human = ", ".join(f"{(t//60)%24:02d}:{t%60:02d}" for t in kicks)
    print(f"# {len(kicks)} kicks/day at: {human}")


if __name__ == "__main__":
    main()
