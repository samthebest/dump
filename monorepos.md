https://danluu.com/monorepo/

http://blog.xebia.com/monorepos-for-true-ci/

http://www.drmaciver.com/2016/10/why-you-should-use-a-single-repository-for-all-your-companys-projects/

http://blog.shippable.com/our-journey-to-microservices-and-a-mono-repository

https://cacm.acm.org/magazines/2016/7/204032-why-google-stores-billions-of-lines-of-code-in-a-single-repository/fulltext

https://medium.com/@Jakeherringbone/you-too-can-love-the-monorepo-d95d1d6fcebe

https://www.pantsbuild.org/index.html

https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git/

# Other Benefits Not Mentioned Above

 - Easy single view of all open PRs
 - Every project will use same workflow (can't having diverging branching models across projects)
 - Have to setup IDEs less often (no need to setup Intellij 10x for 10 different repos for example)
 - Code sharing/reuse is discouraged in multi-repo due to the overhead.  Suppose module A and B would naturally both depend on C, but instead they each have their own implementation, say C_A and C_B.  Now if a fix/feature is applied to one, say C_A, it won't get propogated to C_B, or we have to fix the bug twice.  In large projects this gets much worse.
 - Multirepo is an almost irreversible decision, it's hard to go from a multi to a mono, but it's easy to go from a mono to a multi.

# Forcing Checks

All projects can be forced to conformed to same checks, like security checks, file size checks, etc.
