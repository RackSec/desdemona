As an open source project, `desdemona` welcomes contributions of all
forms, including bug reports, code contributions.

We use a code review process to ensure code quality. Code review is
mandatory; a person with commit access has to sign off before
something can be merged, and that person can not be the same person as
the author.

We aspire to perfect test coverage. This is protected by a gating continuous
integration check: pull requests can not land if they reduce test
coverage. Practicality beats purity; e.g. in the case of obvious tooling
issues (e.g. cloverage's treatment of `for`), this rule shouldn't block the
PR. We also aim to enforce consistent, idiomatic use of our tools. Wherever
possible, this is also implemented by gating CI. This includes an
[EditorConfig][ec] file: please consider installing an EditorConfig plugin for
your editor.

Push changes to your own fork, even if you have push access to the
main repo.

Please note that this project is released with a Contributor Code of
Conduct. By participating in this project you agree to abide by its
terms. You can find the code of conduct in [`CONDUCT.md`][conduct].

[ec]: http://editorconfig.org/
[conduct]: https://github.com/RackSec/desdemona/blob/master/CONDUCT.md
