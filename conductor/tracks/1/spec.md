# Specification: MySnow3 Rebranding

## 1. Requirements
- **Global Search & Replace:** All instances of "MySnow2", "MySnow-2", "mysnow2", and ".mysnow2" must be updated to "MySnow3" and "mysnow3".
- **Branding Update:** The NetBeans branding token must be updated to reflect the version change.
- **Config Path Update:** Default configuration and storage directories must be moved to `.mysnow3` to avoid conflicts with older versions.
- **Build Verification:** The custom macOS build script must be able to produce a working application bundle.

## 2. Technical Details
- **Files Affected:**
    - `readme.md`
    - `conductor/*.md`
    - `nbproject/platform.properties`
    - `Neo4j/src/org/ihtsdo/mysnow/neo4j/Neo4jConfig.java`
    - `Neo4j/nbproject/platform.properties`
- **Build Tool:** `tools/build-mac-fixed.sh` (JDK 25 required).

## 3. Success Criteria
- Grep search for "mysnow2" returns zero results.
- `ant build-mac-fixed` (via script) returns "BUILD SUCCESSFUL".
- `dist/MySnow-2026-arm64.dmg` exists.
