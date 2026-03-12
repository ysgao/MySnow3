# Workflow: MySnow3

## Development Environment Setup
1. **JDK 25:** Ensure JDK 25 is installed and accessible (Default: `/Applications/Apache NetBeans.app/Contents/Home`).
2. **NetBeans:** Apache NetBeans IDE should be used for development.

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
    - Location: `~/neo4j-sct3-store`
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
