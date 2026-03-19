# Specification: Remove Model Search Provider and Display

## Overview
Remove the "Model" search provider and its associated display elements (e.g., "Model (mod)") from the MySnow3 application's search interface. The changes are restricted strictly to the user interface, leaving underlying data models intact.

## Functional Requirements
- **Search Provider Removal:** The "Model" search provider must no longer appear in the QuickSearch or any other search provider selection list.
- **Display Update:** Search results must no longer display the "(mod)" or "Model" designation alongside matching concepts.
- **Backward Compatibility:** Any legacy saved searches, user preferences, or history referencing the "Model" provider must be silently ignored or cleared upon application startup.
- **Data Preservation:** The underlying SNOMED CT Model data must continue to be imported and stored in the Neo4j database without modification.

## Non-Functional Requirements
- **Stability:** The search UI must remain stable and responsive.
- **Regression:** Existing searches using all other active providers must continue to function correctly.

## Out of Scope
- Modifying the Neo4j database import process or schemas.
- Removing, altering, or reviewing any other search providers.
