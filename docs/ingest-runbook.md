# Dataset Ingestion Runbook

## Catalog format

The tank catalog lives at `src/main/resources/tanks/tanks-catalog.json`. It is a JSON
array of objects with the following fields:

| Field     | Type   | Description                                              |
|-----------|--------|----------------------------------------------------------|
| `tankId`  | int    | WoT `tank_id` — must match the value in `vehicles.id`   |
| `name`    | string | Display name (used to derive `canonical_name`)           |
| `nation`  | string | Lowercase nation key: `ussr`, `germany`, `usa`, `france`, `uk`, `japan`, `china`, `sweden`, `italy`, `poland` |
| `tier`    | int    | 1–10                                                     |
| `type`    | string | `lightTank`, `mediumTank`, `heavyTank`, `AT-SPG`, `SPG` |

Example entry:
```json
{"tankId": 5137, "name": "T-34-85", "nation": "ussr", "tier": 6, "type": "mediumTank"}
```

## Armor data

Place an armor JSON file at `src/main/resources/tanks/armor/{tankId}.json` for each
tank whose profile you want to ingest. The file must conform to the `ArmorProfile`
record contract (see `ArmorProfile.java`):

```json
{
  "vehicleId": 5137,
  "zones": [
    {"key": "hull_front", "thicknessMm": 45.0, "geometryRef": "hull"},
    {"key": "turret_front", "thicknessMm": 90.0, "geometryRef": "turret", "normalHint": [0.0, 0.0, 1.0]}
  ],
  "generatedFrom": "tanks.gg",
  "generatedAt": "2026-01-01T00:00:00Z"
}
```

`normalHint` is optional and may be omitted. `generatedFrom` should be `"tanks.gg"` for
data sourced from tanks.gg or `"manual"` for hand-authored profiles.

## Model data

Place a `.glb` file at `src/main/resources/tanks/models/{tankId}.glb` for each tank
whose 3-D model you want to bundle. The file is uploaded as-is to the configured
object storage bucket under `models/{canonical-name}.glb`.

## Running the ingest job

```bash
# Ingest all tanks in the catalog
java -jar wotos-vehicle-service.jar --all

# Ingest only tier-6 tanks
java -jar wotos-vehicle-service.jar --tier=6

# Ingest only Soviet tanks
java -jar wotos-vehicle-service.jar --nation=ussr

# Re-ingest tanks that are already complete (overwrite existing rows)
java -jar wotos-vehicle-service.jar --tier=6 --force
```

Required environment variables when running against MinIO / S3:

| Variable              | Description                                      |
|-----------------------|--------------------------------------------------|
| `AWS_S3_ENDPOINT`     | MinIO URL (e.g. `http://minio:9000`); empty for real AWS |
| `AWS_S3_REGION`       | AWS region (default: `us-east-1`)                |
| `AWS_S3_BUCKET`       | Target bucket name (default: `wotos-models`)     |
| `AWS_ACCESS_KEY_ID`   | Access key / MinIO username                      |
| `AWS_SECRET_ACCESS_KEY` | Secret key / MinIO password                    |

## Idempotency and resumability

A tank is considered **already ingested** when its `vehicle_armor` row is present and
(if storage is configured) its `vehicle_model_assets` row is also present. On a
subsequent run without `--force`, such tanks are silently skipped.

If the run is interrupted (OOM, network error, etc.), restart with the same filter.
Already-completed tanks are skipped; the run continues from the first incomplete tank.

Use `--force` to overwrite all rows and re-upload all models for the filtered set.

## Adding new tanks

1. Add an entry to `tanks-catalog.json`.
2. Optionally drop a corresponding `tanks/armor/{tankId}.json` and/or
   `tanks/models/{tankId}.glb` into the resources.
3. Re-deploy the jar and run `--tier=N` (or `--all --force` to refresh the whole set).
