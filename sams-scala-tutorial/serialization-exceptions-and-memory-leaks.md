## Introduction

Talk:

1. What causes serialization problems (and "memory leaks")
2. Quiz
3. Discussion of ways to prevent/stop these problems
4. The best way to prevent these problems
5. The ideal future way to prevent these problems

.

.

.

.

.

.

.

.

.

.

.

.

## What causes serialization problems (and "memory leaks")

Two things:

1. Spark jobs that *may* require transfer of *object* data between JVMs
    - Trying to broadcast non-serializable data
    - Trying to collect non-serializable data
    - Tryiny to *shuffle* non-serializable data
2. Spark jobs that transfer *meta* data between JVMs via (intentional or unintentional) **transative closure capture**

Both reasons can be very subtle.

.

.

.

.

.

.

.

.

.

.

## QUIZ!






