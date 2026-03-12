# Tech Stack: MySnow3

## Frontend & Application Framework
- **Framework:** [NetBeans Platform](https://netbeans.apache.org/kb/docs/platform.html)
    - Provides the modular architecture, window system, and plugin management.
    - Bundled version used for builds.
- **UI Toolkit:** Java Swing (Standard for NetBeans RCP).

## Language & Runtime
- **Language:** Java
- **JDK Version:** JDK 25 (Primary runtime and build target).
- **Runtime Environment:** macOS (Optimized for AppKit/EDT compatibility).

## Database & Persistence
- **Database:** [Neo4j Community Edition 5.26.21](https://neo4j.com/docs/operations-manual/5/introduction/)
    - Embedded mode used for local terminology storage.
    - Utilizes `DatabaseManagementService` (Neo4j 5.x API).
- **Data Source:** SNOMED CT RF2 (Release Format 2) Snapshot files.

## Build & Tooling
- **Build System:** Apache Ant.
- **Build Scripts:** `build.xml` (Project root and module levels).
- **Helper Scripts:** 
    - `tools/build-mac-fixed.sh`: Custom wrapper for macOS builds using JDK 25.
    - `tools/neo4j-migrate.sh`: Database migration utility.

## Configuration
- **Neo4j Config:** `~/.mysnow3/neo4j.conf`.
- **App Launcher Config:** `dist/mysnow3.app/Contents/Resources/mysnow3/etc/mysnow3.conf`.
