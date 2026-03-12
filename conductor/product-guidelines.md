# Product Guidelines: MySnow3

## 1. Architectural Integrity
- **Modularity:** Maintain strict separation between API and Implementation modules (e.g., `QuerySCT_API` vs `QuerySCT_Impl`).
- **Dependency Management:** Use `project.xml` and `manifest.mf` for managing module dependencies. Avoid direct jar references outside of `ext` folders.

## 2. Performance & Threading
- **EDT Safety (CRITICAL):** Never perform blocking database operations on the Event Dispatch Thread (EDT).
- **Background Processing:** Use NetBeans `RequestProcessor` for all heavy Neo4j queries.
- **UI Updates:** Use `SwingUtilities.invokeLater` to schedule UI updates after background operations complete.

## 3. Data Integrity & Neo4j
- **Transaction Management:** All database reads and writes must occur within a transaction.
- **Lazy Loading:** Prefer `Iterator`-based graph traversal over loading large collections into memory.
- **Property Access:** Use helper methods like `matchesIntProperty` to safely handle variations in numeric types (Integer vs Long) returned by Neo4j.

## 4. macOS Compatibility
- **Assistive Technologies:** Keep `javax.accessibility.assistive_technologies` disabled to prevent AppKit deadlocks.
- **Launcher Config:** Ensure `jdkhome` is correctly set in `mysnow3.conf` during the build process.

## 5. Coding Standards
- **Error Handling:** Avoid silent failures. Log exceptions with enough context to diagnose data-related issues.
- **Documentation:** Maintain `readme.md` and this Conductor structure as the primary technical documentation.
