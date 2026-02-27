# Contributing to java-tron

Thank you for considering contributing to java-tron! We welcome contributions from anyone and are grateful for even the smallest fixes.

java-tron is an open-source project that thrives on community support. This guide will help you contribute effectively. If you have suggestions for improving this guide, please let us know.

## Table of Contents

- [Reporting An Issue](#reporting-an-issue)
- [Working on java-tron](#working-on-java-tron)
  - [Contribution Types](#contribution-types)
  - [Key Branches](#key-branches)
  - [Submitting Code](#submitting-code)
- [Code Review Guidelines](#code-review-guidelines)
  - [Terminology](#terminology)
  - [The Process](#the-process)
  - [Code Style](#code-style)
  - [Commit Messages](#commit-messages)
  - [Branch Naming Conventions](#branch-naming-conventions)
  - [Pull Request Guidelines](#pull-request-guidelines)
  - [Special Situations](#special-situations-and-how-to-deal-with-them)
- [Code of Conduct](#code-of-conduct)

## Reporting An Issue

Before raising an issue, please follow these guidelines:

- **Search for existing issues** - Help us avoid duplicates by checking if someone has already reported your problem or requested your feature.

- **Use the Issue Report Template** - Provide clear information using this format:
    ```
    1. What did you do?

    2. What did you expect to see?

    3. What did you see instead?
    ```

## Working on java-tron

### Contribution Types

**For small fixes:**
- Submit a pull request (PR) directly with a detailed description
- Maintainers will review and merge into the main codebase

**For complex changes:**
- Submit a TIP (TRON Improvement Proposal) issue first
- Detail your motivation and implementation plan
- Refer to the [TIP Specification](https://github.com/tronprotocol/tips#to-submit-a-tip) for submission guidelines

### Key Branches

java-tron uses the following branch structure:

- **`develop` branch**  
  - Accepts merge requests from forked branches or `release_*` branches only
  - Direct pushes are not allowed
  - Source branch for creating new `release_*` branches

- **`master` branch**  
  - Receives merges from `release_*` and `hotfix/*` branches only
  - Updated only when a new build is released
  - Represents the production-ready state

- **`release_*` branch**  
  - Created from `develop` for each release
  - Merged into `master` after passing regression tests
  - Permanently kept in the repository as a release snapshot
  - Bug fixes can be applied directly to this branch
  - After regression tests, merged back into `develop`

- **`feature/*` branch**  
  - Created from `develop` for new feature development
  - Merged back to `develop` when code-complete
  - Maintained throughout the feature development lifecycle

- **`hotfix/*` branch**  
  - Created from `master` for urgent production bug fixes
  - Merged back into both `master` and `develop` branches
  - Used exclusively for fixing critical bugs found after release
  - Only accepts pull requests for bug fixes from forked repositories

### Submitting Code

Follow these steps to contribute code to java-tron:

**1. Fork the repository**
   - Fork tronprotocol/java-tron to your personal GitHub account

**2. Clone and configure your fork**
    ```bash
    git clone https://github.com/yourname/java-tron.git
    git remote add upstream https://github.com/tronprotocol/java-tron.git
    ```

**3. Synchronize with upstream**
   Before developing, sync your fork with the upstream repository:
    ```bash
    git fetch upstream
    git checkout develop
    git merge upstream/develop --no-ff  # Disable fast-forward merge
    ```

**4. Create a feature branch**
   Create a new branch from `develop` (see [Branch Naming Conventions](#branch-naming-conventions)):
    ```bash
    git checkout -b feature/branch_name develop
    ```

**5. Develop and commit**
   Write your code and commit changes (see [Commit Messages](#commit-messages)):
    ```bash
    git add .
    git commit -m 'commit message'
    ```

**6. Push to your fork**
    ```bash
    git push origin feature/branch_name
    ```

**7. Create a pull request**
   - Submit a PR from your fork to `tronprotocol/java-tron`
   - Select the appropriate base branch (usually `develop`)
   - Select your feature branch as the compare branch
   - ![PR Guide](https://raw.githubusercontent.com/tronprotocol/documentation-en/master/images/javatron_pr.png)

## Code Review Guidelines

All code changes must go through the pull request review process. This section outlines expectations for both PR authors and reviewers.

### Terminology

- **Author**: The entity who wrote the diff and submitted it to GitHub
- **Team**: People with commit rights on the java-tron repository
- **Reviewer**: The person assigned to review the diff (must be a team member)
- **Code owner**: The person responsible for the subsystem being modified

### The Process

**Initial Assessment:**
- Code owners evaluate PR inclusion based on adequate descriptions and reasonable diff sizes
- Anyone can request clarification when needed

**Review Responsibilities:**
- Reviewers verify code style and functionality
- Provide constructive feedback through GitHub's review system
- Maintain professional communication throughout the process
- Follow up until quality standards are met
- Approve when ready for merge

**Merging:**
- Code owners merge approved PRs

### Code Style

**Before Submitting:**
1. Use coding style checkers to review your code
2. Perform a self-review before submission
3. Run all standardized tests

**Automated Checks:**
- Sonar scanner and Travis CI run automatically on all PRs
- PRs must pass all checks before maintainer review

**Review Process:**
- Maintainers provide feedback and request modifications as needed
- Approved PRs are merged into the `develop` branch
- All contributions are welcome, including typo fixes

**If Your PR is Not Accepted:**
- Don't be discouraged - it may be an oversight
- Provide detailed explanations to help reviewers understand your changes
- Address feedback constructively

**Code Style Requirements:**
- Code must conform to [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Code must pass Sonar scanner tests
- Branches must be created from `develop`
- Commit messages must start with a lowercase verb
- Commit subject lines must be 50 characters or less

### Commit Messages

Follow this template for all commit messages:

```
<commit type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

**Header Format:**
A single line containing the commit type, optional scope, and subject.

**Commit Types:**
* feat     (new feature)
* fix      (bug fix)
* docs     (changes to documentation)
* style    (formatting, missing semi colons, etc. no code change)
* refactor (refactoring production code)
* test     (adding or refactoring tests. no production code change)
* chore    (updating grunt tasks etc. no production code change)

**Scope:**
Specifies the area of change (e.g., `protobuf`, `api`, `test`, `docs`, `build`, `db`, `net`). Use `*` if no specific scope applies.

**Subject Guidelines:**
1. Limit to 10 ~ 72 characters
2. Use imperative, present tense (e.g., "change" not "changed" or "changes")
3. Start with a lowercase letter
4. Do not end with a period
5. Avoid meaningless commits (use `git rebase` to clean up)

**Body:**
- Use imperative, present tense
- Explain the motivation for the change
- Contrast with previous behavior

**Footer:**
Reference related issues using the `Closes` keyword:
- Single issue: `Closes #1234`
- Multiple issues: `Closes #123, #245, #992`

**Example:**
```
feat(block): optimize the block-producing logic

1. increase the priority that block producing thread acquires synchronization lock
2. add the interruption exception handling in block-producing thread

Closes #1234
```

### Branch Naming Conventions

1. **Main branches:** Always use `master` and `develop`
2. **Release branches:** Use version numbers assigned by project lead (e.g., `Odyssey-v3.1.3`, `3.1.3`)
3. **Hotfix branches:** Use `hotfix/` prefix with brief description, words separated by underscores (e.g., `hotfix/typo`, `hotfix/null_point_exception`)
4. **Feature branches:** Use `feature/` prefix with brief description, words separated by underscores (e.g., `feature/new_resource_model`)

### Pull Request Guidelines

1. **One PR per issue** - Keep changes focused
2. **Avoid massive PRs** - Break large changes into smaller, reviewable pieces
3. **Clear title** - Summarize the purpose concisely
4. **Detailed description** - Help reviewers understand your changes
5. **Specify feedback needs** - Indicate what kind of review you need
6. **Title formatting:**
   - Start with a lowercase letter
   - Do not end with a period

### Special Situations And How To Deal With Them

**Author doesn't follow up:**
- Ping them after a few days
- If no response, close the PR or complete the work yourself

**Author includes refactoring with bug fix:**
- Small refactorings are acceptable
- For large refactorings, request a separate PR or independent commit
- Ask if the diff is difficult to review

**Author rejects feedback:**
- Reviewers have authority to reject changes for technical reasons
- Seek a second opinion from the team if unsure
- Close the PR if no consensus can be reached

## Code of Conduct

We are committed to providing a welcoming and inclusive environment for all contributors.

**Positive Behaviors:**
- Use welcoming and inclusive language
- Respect differing viewpoints and experiences
- Accept constructive criticism gracefully
- Focus on what's best for the community
- Show empathy towards other community members

**Unacceptable Behaviors:**
- Sexualized language, imagery, or unwelcome advances
- Trolling, insulting comments, or personal/political attacks
- Public or private harassment
- Publishing others' private information without permission
- Other conduct inappropriate in a professional setting
