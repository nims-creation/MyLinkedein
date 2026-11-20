# Contributing to MyLinkedin

Thanks for your interest in improving **MyLinkedin**! Contributions are welcome from backend, frontend, DevOps, testing, and documentation developers.

This is an evolving learning and engineering project. Please keep changes focused, explain your reasoning, and be respectful of other contributors.

## Before you start

- Read the [README](README.md) for the architecture, services, API overview, and roadmap.
- Check [open issues](https://github.com/nims-creation/MyLinkedein/issues) and existing pull requests to avoid duplicate work.
- For a new feature or significant architectural change, **open an issue first** to discuss the scope and approach. Please wait for agreement before investing in a large implementation.
- For small bug fixes, documentation improvements, or tests, you can open a pull request directly and explain the change.

Good first contributions include fixing a reproducible bug, adding tests, improving error handling, documenting an API, or polishing frontend accessibility.

## Development setup

**Prerequisites:** Git, JDK 21, Docker with Docker Compose, and Node.js/npm if working on the React frontend.

1. Fork [this repository](https://github.com/nims-creation/MyLinkedein) on GitHub.
2. Clone **your fork** and enter the repository directory:

   ```bash
   git clone https://github.com/<your-username>/MyLinkedein.git
   cd MyLinkedein
   ```

3. Configure local environment variables (do not commit secrets):

   ```bash
   cp .env.example .env
   ```

   Update the values in `.env` for your local environment. Keep passwords, tokens, private keys, and real customer data out of commits, screenshots, issue reports, and logs.

4. Follow the README's Docker Compose instructions to start the application:

   ```bash
   docker compose up --build -d
   docker compose ps
   ```

   Local setup can require additional external-service credentials. If you get blocked, open an issue with redacted logs and the steps you've tried.

## Repository areas

- `user-service/` — user-related APIs
- `post-service/` — posts
- `ConnectionService/` — social connections
- `notification-service/` — notifications
- `uploader-service/` — uploads
- `api-gateway/` — gateway routing and authentication
- `discovery-server/` — service discovery
- `mylinkedin-frontend/` — React frontend
- `k8s/` — Kubernetes manifests
- `.github/workflows/` — CI/CD configuration
- `docs/` — architecture and related documentation

For changes spanning several services, explain the service boundaries and data/event flows in your issue or PR.

## Branch and pull request workflow

Create a branch **from the latest `main` on your fork**:

```bash
git checkout main
git pull origin main
git checkout -b feature/short-description
```

Use descriptive branch names such as `feature/profile-search`, `fix/connection-validation`, or `docs/setup-guide`.

Make a focused change, then commit and push:

```bash
git add .
git commit -m "Fix connection request validation"
git push -u origin feature/short-description
```

Open a pull request **from your fork to `nims-creation/MyLinkedein:main`**. Link the related issue (for example, `Closes #123` when appropriate).

In your PR description, include:
- **What** changed and **why**
- Which services or frontend pages are affected
- How you tested it, including commands/results and any untested areas
- Screenshots or short recordings for UI changes
- Any database migrations, Kafka event changes, environment variables, or compatibility considerations

Please respond to review feedback and avoid unrelated formatting changes in the same PR. Contributors should not push directly to the upstream `main` branch.

## Code, tests, and documentation

- Follow the patterns and naming conventions already used in the relevant service.
- Keep controllers, business logic, and persistence concerns appropriately separated.
- Validate inputs, handle errors clearly, and avoid logging credentials or sensitive data.
- When changing database schemas, message payloads, authentication behavior, or APIs, document any migration or backward-compatibility impact.
- Add or update tests where practical, especially for bug fixes, authentication, event handling, and data access.
- For a backend module, try running its Maven wrapper tests from that module's directory:

  ```bash
  ./mvnw test
  ```

- For frontend changes, from `mylinkedin-frontend/`:

  ```bash
  npm ci
  npm run build
  ```

**Note:** The existing GitHub Actions backend workflow currently uses `-DskipTests` during its build. A passing CI build therefore does **not** mean automated backend tests passed. Run relevant tests locally when possible and report failures or limitations transparently.

## Reporting bugs and proposing features

Open a [GitHub issue](https://github.com/nims-creation/MyLinkedein/issues/new) with:

**Bug reports:** a short summary, steps to reproduce, expected vs. actual behavior, affected component, environment, and redacted logs/screenshots.

**Feature requests:** the user problem, proposed behavior, affected services/UI, alternatives considered, and any relevant tradeoffs.

Please avoid posting private credentials or security-sensitive exploit details in public issues. For a suspected vulnerability, contact the maintainer privately through their GitHub profile rather than publishing a working exploit.

## Community expectations

Be kind and constructive. Review ideas and code, not people. Assume good intent, communicate respectfully, and credit others' contributions.

By submitting a contribution, you agree that it may be distributed under the repository's [MIT License](LICENSE).

Thank you for helping make MyLinkedin better!
