- integration target=develop per kickoff+strategy=self-merge (stream.push=auto-pr in fleet.json overridden by project strategy=self-merge). maven.yml currently triggers only on 'dev'; will add 'develop' trigger during P1-G1 migration so director can watch develop CI.

- verified: local Testcontainers blocked by docker-java<->Docker Engine 29 (API 1.54) incompat (npipe /info returns 400). Gated TC integration tests with @Testcontainers(disabledWithoutDocker=true): skip locally, run on CI (Linux standard docker). Validated G2 DDL+entity end-to-end by booting app against a real mysql:8.0 container via docker CLI (Flyway v1 applied, Hibernate ddl-auto=validate passed). CI is the enforcement point for TC tests.

- PUBLISHED contract: ArmorProfile JSON = {vehicleId:int, zones:[{key:string, thicknessMm:number, geometryRef:string, normalHint?:[x,y,z]}], generatedFrom:'tanks.gg'|'manual', generatedAt:ISO-8601}. normalHint omitted when absent. GET /api/vehicles/{id}/armor -> 200 ArmorProfile or 404. LOCKED for backend-edge (/garage fan-out) + frontend-garage (mesh paint).

- PUBLISHED contract: GET /api/vehicles/{id}/model -> 200 {url, etag, sizeBytes, format} (url = 60s presigned GET) or 404. ModelResponse LOCKED for frontend-garage (fetches .glb directly from url).

