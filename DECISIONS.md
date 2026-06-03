- integration target=develop per kickoff+strategy=self-merge (stream.push=auto-pr in fleet.json overridden by project strategy=self-merge). maven.yml currently triggers only on 'dev'; will add 'develop' trigger during P1-G1 migration so director can watch develop CI.

- verified: local Testcontainers blocked by docker-java<->Docker Engine 29 (API 1.54) incompat (npipe /info returns 400). Gated TC integration tests with @Testcontainers(disabledWithoutDocker=true): skip locally, run on CI (Linux standard docker). Validated G2 DDL+entity end-to-end by booting app against a real mysql:8.0 container via docker CLI (Flyway v1 applied, Hibernate ddl-auto=validate passed). CI is the enforcement point for TC tests.

