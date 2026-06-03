-- wotos_vehicles_database schema (Phase 3 — the 3D backbone).
-- Flyway owns this schema; Hibernate runs with ddl-auto=validate against it.

-- Local Tankopedia mirror + our own augmentation fields.
CREATE TABLE vehicles (
    id                   INT          NOT NULL,            -- WoT tank_id
    name                 VARCHAR(255) NOT NULL,
    short_name           VARCHAR(255),
    nation               VARCHAR(64),
    tier                 INT,
    type                 VARCHAR(64),                       -- lightTank, mediumTank, heavyTank, AT-SPG, SPG
    weight_kg            INT,                               -- from default_profile.weight
    traverse_speed_deg_s INT,                               -- from default_profile.suspension.traverse_speed
    canonical_name       VARCHAR(255),                      -- our slug, e.g. "t-34-85"
    default_camera       VARCHAR(255),                      -- garage camera preset key
    shell_types          JSON,                              -- augmentation: shell picker data for the garage
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One armor profile per vehicle (latest wins — upserted on re-ingest).
CREATE TABLE vehicle_armor (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    vehicle_id          INT          NOT NULL,
    armor_profile_json  JSON         NOT NULL,
    generated_from      VARCHAR(64)  NOT NULL,              -- 'tanks.gg' | 'manual'
    generated_at        DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_vehicle_armor_vehicle (vehicle_id),
    CONSTRAINT fk_vehicle_armor_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One .glb model-asset metadata row per vehicle (the blob lives in object storage).
CREATE TABLE vehicle_model_assets (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    vehicle_id       INT          NOT NULL,
    bucket           VARCHAR(255) NOT NULL,
    object_key       VARCHAR(512) NOT NULL,
    etag             VARCHAR(255),
    size_bytes       BIGINT,
    format           VARCHAR(16)  NOT NULL DEFAULT 'glb',
    draco_compressed BIT(1)       NOT NULL DEFAULT b'0',
    uploaded_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_vehicle_model_vehicle (vehicle_id),
    CONSTRAINT fk_vehicle_model_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
