# Implementation Plan: Move Neo4j Database to Library Folder

## Phase 1: Code Implementation
- [ ] Task: Locate the current database path configuration
    - [ ] Search the codebase for references to `neo4j-sct3-store`.
- [ ] Task: Update the database path
    - [ ] Modify the configuration to use the new path: `~/Library/Application Support/MySnow-2026/neo4j-sct3-store`.
    - [ ] Ensure the code correctly resolves the user's home directory to build the absolute path.
    - [ ] Ensure the necessary parent directories (`~/Library/Application Support/MySnow-2026`) are created if they do not exist before attempting to create or open the database.

## Phase 2: Documentation Updates
- [ ] Task: Update project documentation
    - [ ] Modify `conductor/workflow.md` to reflect the new default database location.
    - [ ] Search and modify any other documentation referencing the old `~/neo4j-sct3-store` location.

## Phase 3: Testing and Verification
- [ ] Task: Verify fresh start functionality
    - [ ] Ensure no `neo4j-sct3-store` exists at the new location.
    - [ ] Start the application and verify that a new database directory is created at `~/Library/Application Support/MySnow-2026/neo4j-sct3-store`.
- [ ] Task: Verify database connectivity and basic functions
    - [ ] Check application logs for any connection errors.
    - [ ] (If possible) Perform a basic SNOMED CT import to verify write access.