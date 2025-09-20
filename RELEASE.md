# 📦 Release Process

This project uses **GitHub Flow** (feature → main) and a **two-part semantic versioning scheme**:

```
MAJOR.MINOR
```

- **MAJOR** → Incremented for breaking changes.
- **MINOR** → Incremented for every feature merge into `main`.
- ❌ No patch numbers (X.Y.Z) are used.

---

## 🔢 Version Source

- The version is stored in **`gradle.properties`**:

```properties
group=com.meln
version=1.12
```

- Gradle tasks read and update this file.
- Tags are created in Git in the form: `vX.Y`

---

## ⚙️ Gradle Tasks

Custom tasks are defined in `gradle/versioning.gradle.kts`.

### Minor bump

```bash
./gradlew bumpMinorAndTag
```

- Increases MINOR: `1.12 → 1.13`
- Commits `gradle.properties`
- Creates tag `v1.13`

### Major bump

```bash
./gradlew bumpMajorAndTag
```

- Increases MAJOR, resets MINOR: `1.12 → 2.0`
- Commits `gradle.properties`
- Creates tag `v2.0`

---

## 🤖 GitHub Actions Automation

File: `.github/workflows/version-bump.yml`

- **Trigger:** when a Pull Request is **merged** into `main`.
- **Default:** bumps **MINOR** and tags.
- **MAJOR bump:** if the PR has the label **`major`**.

```yaml
labels:
  - major  # triggers bumpMajorAndTag
```

The workflow:

1. Checks out `main`
2. Determines bump type:
    - `minor` (default)
    - `major` (if PR labeled `major`)
3. Runs the appropriate Gradle task
4. Commits and pushes the updated `gradle.properties`
5. Pushes the Git tag (`vX.Y`)

---

## 📝 Team Guidelines

1. **Normal feature PRs**
    - Open PR → merge into `main`
    - Action auto-bumps **MINOR**

2. **Breaking changes**
    - Add the label **`major`** to the PR before merging
    - Action auto-bumps **MAJOR**

3. **Manual bump (rare)**
    - Run locally:
    ```bash
    ./gradlew bumpMajorAndTag
    git push && git push --tags
    ```
    - Useful for hotfixes or emergency major releases.

4. **Check current version**
   ```bash
   ./gradlew properties | grep version:
   ```
