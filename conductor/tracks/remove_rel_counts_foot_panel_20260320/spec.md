# Specification: Remove Relationship Counts from Bottom/Foot Panel

## Overview
This track involves removing the display of relationship and description counts from the bottom panel (often referred to as the foot panel) of the application where the selected concept's information is displayed.

## Functional Requirements
1.  **Remove Relationship Counts:** Remove the display of the number of ISA and non-ISA relationships of the focus concept from the application's bottom/foot panel.
2.  **Remove Description Count:** Remove the display of the number of descriptions of the focus concept from the application's bottom/foot panel.
3.  **Preserve Panel Structure:** The panel itself must remain visible in the UI layout, even if the count text is removed. The layout structure should not be entirely hidden or destroyed.

## Non-Functional Requirements
-   **Performance:** The UI responsiveness should remain unchanged or slightly improve.
-   **Stability:** The UI layout should not break or become misaligned when the text elements are removed.

## Acceptance Criteria
-   When a concept is selected, the bottom/foot panel no longer displays counts for ISA relationships, non-ISA relationships, or descriptions.
-   The panel remains visible.

## Out of Scope
-   Redesigning the panel layout beyond removing the specific text elements.