
### Definition - Performance Test

Is any test for resource consumption: that is memory, disk or time.

### Definition - End to End Test

Must (either directly or via the harness) invoke the main method, and interact with the system only through the public interface of the system without mocking, injection, or any other magic. E.g. it calls the application as a CLI or a HTTP API.

### Definition - Acceptance Test (AKA Functional Test)

A test is an acceptance test (aka functional test) if and only if it tests something that is specific to the domain or application.

Or via contraposition:

A test is not an acceptance test if and only if it tests something that is not specific to the domain (it could be used in any application).

In practice this delineation is usually achieved by ensuring domain agnostic code is kept in packages named "utils" or similar, while all other code is assumed to be specific to the application.  Therefore any tests for utils code are not acceptance tests, and any tests for code not in utils are acceptance tests. It may therefore be useful to refer to NOT acceptance tests as Utils Tests.

### Definition - Integrations Test - IT Suffix

For class/object X, XIT should include and only include tests that

 - Test the interactions of X with other classes/objects, i.e. how it wires together with other components
 - Does NOT inject any side effecting dependencies (e.g. hits a filesystem, or a DB)
 - Requires the existence of some other process to be running (e.g. a DB)

Or, put simply it tests the integration of a component with other internal or external components.
Definition - Unit Test - Spec Suffix

Definition - Unit Test - Spec Suffix

For class/object X, XSpec should only test the code in class/object X (that may call native or standard libraries).  If X depends on component Y, then Y should be mocked or injected.

Set Theoretic Relationships

The above definitions are by no means forms a partition.

E2E subset of IT
PT subset of IT (technically any resource is a side effect, so PTs should go in IT)
IT and UT form a partition (i.e. are disjoint and cover the universe of tests)
AT is the compliment of Utils Tests

No further restrictions apply, for example is is perfectly possible that a test is an AT an E2E and an IT.  It's also possible that a utils test is an E2E test, or a PT or an IT.
I have a preference towards keeping having both E2E tests and PT tests, rather than subsuming E2E tests inside PT tests (even thought if an E2E test fails it will inevitably mean some PT fails). Thought this is just a nice to have (my subjective opinion).



See also http://blog.jonasbandi.net/2010/09/acceptance-vs-integration-tests.html




With respect the atoms above:

PT: Can include any of 1 - 7, but any test of type 6 and 7 is always a PT (since volume implies you wish to test resource usage)
E2E: Is exactly 5, 6, 7.
AT: Does NOT include 1, 4, but can include any of the others.
IT: Is exactly 3 - 7
UT / Spec: Is exactly 1, 2



I'm not sure I agree with your "prevention over cure", as IME prevention seems to cause more complexity than it removes. It's a case of certainty vs uncertainty. If you cure problems as they arise you have certainty that your incremental improvement IS an improvement, whereas prevention is uncertain - there is a non-zero probability that your "prevention" creates complexity without improvement. Therefore there is a statistical advantage to only ever focusing on issues that are certain. This is one of the main principles of KISS & YAGNI in Extreme Programming, you take "KISS & YANGI to the extreme".
