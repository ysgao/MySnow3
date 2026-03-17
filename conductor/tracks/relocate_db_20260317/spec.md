# Specification: Move Neo4j Database to Library Folder

## 1. Overview
The embedded Neo4j database (`neo4j-sct3-store`) is currently located in the user's home folder. This location causes access issues on macOS due to strict folder permission requirements. The goal of this track is to change the default database location to a more appropriate, application-specific directory within the user's `Library` folder to ensure smooth operation on macOS.

## 2. Functional Requirements
*   **New Database Location:** The embedded Neo4j database MUST be created and accessed at `~/Library/Application Support/MySnow-2026/neo4j-sct3-store`.
*   **Fresh Start:** The application MUST NOT attempt to migrate any existing database from the old location. It should start fresh at the new location.

## 3. Non-Functional Requirements
*   **macOS Compatibility:** The change should resolve any existing permission-related issues when accessing the database directory on macOS.

## 4. Acceptance Criteria
*   [ ] When the application starts, it creates a new database directory at `~/Library/Application Support/MySnow-2026/neo4j-sct3-store` if one does not exist.
*   [ ] The application successfully connects to the database at the new location.
*   [ ] No existing database data from the old user folder location is migrated or used.
*   [ ] The application functions normally (browsing taxonomy, viewing concept details, searching) with the new database location.

## 5. Out of Scope
*   Migrating data from previous database locations.
*   Changing the Neo4j database version.
*   Modifying database schema or content structure.