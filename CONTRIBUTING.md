# Contributing to this repository

As an open source project, we welcome contributions from anybody who is
interested in improving our app.

If you are interested in submitting a PR, please make sure that you adhere to
these guidelines.

## Commits & commit messages

Every commit should be **atomic**, which means that it should be a
self-contained set of changes that can be easily describe. All commits should
also **pass the project's tests**.

This makes easier code reviews, taking a look at the project's history,
preparing changelogs and reverting single commits if things go wrong.

That is also the reason why **commit messages** are very important. Here are
some tips on how to write great messages:

```
area: Implement some service (up to 50 characters)

More detailed explanatory text. Wrap it to 72 characters. The blank
line separating the summary from the body is critical (unless you omit
the body entirely).

Write your commit message in the imperative: "Fix bug" and not "Fixed
bug" or "Fixes bug." This convention matches up with commit messages
generated by commands like git merge and git revert.

Further paragraphs come after blank lines.

- Bullet points are okay, too.
- Typically a hyphen or asterisk is used for the bullet, followed by a
  single space. Use a hanging indent.
```

- Start the subject line with a tag that indicates which part of the app is
  affected the most by that commit. You can take a look at the project's commit
  history, and here are some examples:
    - `tests`: for testing-related commits (unit tests, CI config...).
    - `views`: for changes that affect the app's views/layout.
    - `db`: for changes in the app's data storage system.
    - `project`: for "meta" changes, like the project's directory
      structure, `.gitignore`s...
    - `docs`: all things documentation.
    - Any other that you consider relevant!
- Use imperative mood in the subject line (_"Add feature"_ instead of _"Adds
  feature"_).
- Indicate in the commit's body if it closes any existing issue, so it is
  [closed
  automatically](https://docs.github.com/en/github/managing-your-work-on-github/linking-a-pull-request-to-an-issue#linking-a-pull-request-to-an-issue-using-a-keyword)
  once the commit is merged.
- Do not end the subject line with a period.
