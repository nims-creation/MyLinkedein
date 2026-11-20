# Security Policy

Thank you for helping keep **MyLinkedin** and its contributors safe.

MyLinkedin is an actively developed open-source learning project. It has not been independently security-audited. **Do not assume it is ready to handle real credentials, sensitive personal data, or production traffic without your own security review.**

## Supported versions

| Version | Security maintenance |
| --- | --- |
| Latest `main` branch | Best-effort review and fixes |
| Older commits, forks, or unofficial deployments | Not actively maintained |

There are currently no separately supported release branches or guaranteed security-response SLAs.

## Report a vulnerability privately

**Please do not publish suspected vulnerabilities, exploit steps, credentials, or sensitive logs in a public GitHub Issue, Discussion, or Pull Request.**

1. Open the repository's [Security tab](https://github.com/nims-creation/MyLinkedein/security).
2. If **Report a vulnerability** is available, use GitHub's private vulnerability reporting workflow to submit the report.
3. If private reporting is unavailable, **do not post the vulnerability details publicly**. Use a private contact method listed on the [maintainer's GitHub profile](https://github.com/nims-creation) if available, or request that the maintainer enable a secure reporting channel through a *minimal public issue containing no technical or sensitive details*. Wait until a private channel is established before sharing a proof of concept.

The availability of GitHub's private reporting option can vary with repository settings; do not assume that it is enabled.

### What to include in a private report

- **Summary:** what the vulnerability is and its potential impact
- **Affected component:** for example, `api-gateway`, `user-service`, `ConnectionService`, `post-service`, Kubernetes configuration, or CI/CD
- **Affected revision:** commit SHA, branch, and deployment configuration if known
- **Reproduction:** minimal steps using local/test data and an isolated environment
- **Observed vs. expected behavior**
- **Evidence:** sanitized logs, screenshots, or a proof of concept that does not expose secrets or personal data
- **Suggested mitigation**, if you have one

Please allow the maintainer time to investigate and coordinate a fix before any public disclosure. Reports are reviewed on a best-effort basis; specific acknowledgement or remediation timelines are **not guaranteed**.

## Examples of issues worth reporting privately

- Authentication or authorization bypasses at Spring Cloud Gateway or downstream services
- JWT validation, token revocation, or session-handling vulnerabilities
- Cross-user or cross-tenant data access
- Injection vulnerabilities, unsafe file uploads, or server-side request forgery
- Exposed secrets or insecure Docker/Kubernetes/CI configuration
- Kafka event-processing flaws that enable unauthorized actions or sensitive-data exposure

Ordinary bugs and non-sensitive reliability improvements should use the [public Bug Report form](https://github.com/nims-creation/MyLinkedein/issues/new/choose).

## Responsible testing

Only test systems you own or are explicitly authorized to assess. Prefer local Docker Compose or other isolated test deployments. Do not run disruptive scans, access other people's accounts or data, or target third-party services.

Avoid committing or attaching API keys, tokens, passwords, private keys, customer data, or raw logs containing sensitive information. Use redacted examples.

## Fixes and disclosure

The maintainer will assess reported issues, determine severity and affected versions, and coordinate fixes as resources permit. Where appropriate, fixes can be documented using a GitHub Security Advisory or a release note after a safe disclosure plan is agreed.

Security contributions are welcome. For ordinary code improvements, see [CONTRIBUTING.md](CONTRIBUTING.md) and the [MIT License](LICENSE).
