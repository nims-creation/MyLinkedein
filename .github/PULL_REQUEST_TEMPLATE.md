## Summary

<!-- What changed, why is it needed, and what problem does it solve? Keep this understandable without reading every diff. -->

**Related issue:** Closes #<!-- issue number -->  
<!-- Use "Refs #123" instead if this PR does not fully resolve the issue. -->

## Type of change

<!-- Check all that apply. -->

- [ ] Bug fix
- [ ] New feature / enhancement
- [ ] Refactor / maintenance
- [ ] Tests
- [ ] Documentation
- [ ] CI/CD / infrastructure

## What changed?

<!-- Mention relevant components, behavior changes, and major design decisions. -->

**Affected areas:**
- [ ] API Gateway / authentication
- [ ] User Service
- [ ] Post Service
- [ ] Connection Service
- [ ] Notification Service
- [ ] Uploader Service
- [ ] Discovery Server
- [ ] React frontend
- [ ] Kafka / databases
- [ ] Docker / Kubernetes / GitHub Actions
- [ ] Documentation / other

**Implementation notes:**

<!-- Explain service interactions, important tradeoffs, or alternate approaches you considered. For substantial features, link the design discussion. -->

## How was this tested?

<!-- List exact commands and observed outcomes; don't claim tests ran just because CI is green. -->

| Check | Result / notes |
| --- | --- |
| Unit tests | <!-- e.g. `cd user-service && ./mvnw test` — passed/failed/not run --> |
| Integration / API tests | <!-- Relevant requests, expected responses, test infrastructure --> |
| Frontend build (if applicable) | <!-- `cd mylinkedin-frontend && npm ci && npm run build` --> |
| Manual verification (if applicable) | <!-- Reproduction steps and actual result --> |

**Not tested / known limitations:**

<!-- Be transparent about untested paths or environment constraints. -->

> **CI note:** The repository's current backend GitHub Actions workflow builds with `-DskipTests`. A green workflow does **not** by itself prove backend tests passed. Run relevant tests separately or explain why they could not be run.

## API, data, and deployment impact

<!-- Check if applicable; add details for any checked item. -->

- [ ] Public API or response contract changed
- [ ] Database schema / migrations changed
- [ ] Kafka event payload, topic, or consumer behavior changed
- [ ] Authentication / authorization / sensitive data handling changed
- [ ] Environment variables, secrets, ports, or infrastructure changed
- [ ] Backward compatibility or rollout concerns
- [ ] None of the above

**Migration, compatibility, or rollback notes (if needed):**

## Screenshots or evidence

<!-- For UI changes: provide before/after screenshots or a short recording. For backend changes: logs, API examples, traces, or test output as appropriate. Redact secrets and personal data. -->

## Contributor checklist

- [ ] I read [CONTRIBUTING.md](../CONTRIBUTING.md) and kept this PR focused.
- [ ] I reviewed my changes and removed accidental debug code, generated files, and unrelated edits.
- [ ] I have not included credentials, access tokens, private keys, personal data, or real customer information.
- [ ] I documented relevant API, configuration, database, or event-contract changes.
- [ ] I added/updated tests where practical, or explained why not.
- [ ] I linked a related issue (or explained why this small change needs none).

<!-- Thanks for contributing to MyLinkedin! Maintainers may request changes before merging. -->
