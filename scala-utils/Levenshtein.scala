implicit class PimpedString(s: String) {
    def levenshtein(other: String): Int = {
      val dist = Array.tabulate(other.length + 1, s.length + 1) {
        case (0, i) => i
        case (j, 0) => j
        case _ => 0
      }

      for {
        j <- 1 to other.length
        i <- 1 to s.length
      }
        dist(j)(i) =
          if (other(j - 1) == s(i - 1)) dist(j - 1)(i - 1)
          else List(dist(j - 1)(i) + 1, dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1).min

      dist(other.length)(s.length)
    }
  }
  
  // TESTS:
  
  "PimpedString.levenshtein" should {
    import Utils.PimpedString
    "Return a distance of 0 for equal strings 'hello world'" in {
      "hello world".levenshtein("hello world") must_=== 0
    }

    "Return a distance of 0 for equal strings 'foo bar'" in {
      "foo bar".levenshtein("foo bar") must_=== 0
    }

    "insertions:" in {
      "Return a distance of 1 for equal strings off by one insertion" in {
        "foo barr".levenshtein("foo bar") must_=== 1
      }

      "Return a distance of 2 for equal strings off by two insertion" in {
        "fooo barr".levenshtein("foo bar") must_=== 2
      }

      "Return a distance of 3 for equal strings off by three insertion" in {
        "fooo bbarr".levenshtein("foo bar") must_=== 3
      }

      "Return a distance of 4 for equal strings off by four insertion" in {
        "ffooo bbarr".levenshtein("foo bar") must_=== 4
      }
    }

    "substitutions:" in {
      "Return a distance of 1 for equal strings off by one sub" in {
        "foo bar".levenshtein("fof bar") must_=== 1
      }

      "Return a distance of 2 for equal strings off by two sub" in {
        "foo bar".levenshtein("fff bar") must_=== 2
      }

      "Return a distance of 3 for equal strings off by three sub" in {
        "foo bar".levenshtein("aod bfr") must_=== 3
      }

      "Return a distance of 4 for equal strings off by four sub" in {
        "foo bar".levenshtein("aad dar") must_=== 4
      }
    }

    "deletions:" in {
      "Return a distance of 1 for equal strings off by one deletion" in {
        "foo bar".levenshtein("foobar") must_=== 1
      }

      "Return a distance of 2 for equal strings off by two deletion" in {
        "foo bar".levenshtein("fo br") must_=== 2
      }

      "Return a distance of 3 for equal strings off by three deletion" in {
        "foo bar".levenshtein("oo b") must_=== 3
      }

      "Return a distance of 4 for equal strings off by four deletion" in {
        "foo bar".levenshtein("for") must_=== 4
      }
    }

    "Return a distance of 10 for a complex mixed example" in {
      "hello world hello world hello".levenshtein("helo  w33fld hhello werld hDllofs") must_=== 10
    }
  }
