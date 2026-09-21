# Workflow: MySnow3

## Development Environment Setup
1. **JDK:** Any JDK 21 or newer. Set `JAVA_HOME` to pick one; the build uses whichever
   JDK runs Ant and hardcodes no path.
2. **NetBeans Platform:** downloaded automatically by the build from the official Apache
   distribution (see `nbproject/platform.xml`). No NetBeans installation is needed to build.
3. **NetBeans IDE:** optional, for development.

## Build Processes
- **Clean and Build:**
    ```bash
    ant clean build
    ```
- **macOS Native Build (Recommended):**
    ```bash
    bash ./tools/build-mac-fixed.sh
    ```
    This script sets the correct JDK home and runs the `build-mac-fixed` target.

## Data Management
- **Embedded Database:**
    - Location: `~/Library/Application Support/MySnow-2026/neo4j-sct3-store`
    - To reset the data, delete this directory and trigger a re-import.
- **Importing SNOMED CT:**
    - Use the `ImportSCT_UI` module within the running application to select an RF2 folder and start the import.
    - Order: Concepts → Descriptions/Language → Relationships.

## Packaging & Distribution
- **App Bundle:** The `ant build-mac-fixed` target generates the `.app` bundle in `dist/`.
- **DMG Creation:**
    ```bash
    hdiutil create -ov -fs HFS+ -volname mysnow3 -srcfolder dist/mysnow3.app dist/mysnow3.dmg
    ```

## Diagnostic & Troubleshooting
- **Relationship Summary:** Check the status bar when selecting a concept for a count of ISA and non-ISA relationships.
- **Log Files:** Check NetBeans logs for any `ClassCastException` or `EDT` deadlocks.
