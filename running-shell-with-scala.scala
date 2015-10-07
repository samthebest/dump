
// Doesn't work in scala repl for some reason cos of weird implicit not working

def executeAndDiscardStdErr(s: String): String = s !! ProcessLogger(_ => ())

def executeString(s: String): (String, String, Int) = {
    var stderr = ""
    var stdout = ""
    val exitCode = s ! ProcessLogger(stdout += _, stderr += _)
    (stdout, stderr, exitCode)
  }
