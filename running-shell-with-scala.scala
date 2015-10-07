
// Doesn't work in scala repl for some reason cos of weird implicit not working

def executeAndDiscardStdErr(s: String): String = s !! ProcessLogger(_ => ())
