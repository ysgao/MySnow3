# Implementation Plan: Remove Relationship Counts from Bottom/Foot Panel

## Phase 1: Investigation
- [x] Task: Locate UI Component
    - [x] Search the codebase for keywords like "ISA", "relationships", "descriptions", and "foot" or "bottom" within UI-related classes.
    - [x] Identify the specific Java class and method responsible for rendering the bottom/foot panel information.
- [x] Task: Understand Data Flow
    - [x] Determine how the counts are currently retrieved and passed to the UI component.
    - [x] Identify the exact lines of code that format and display these counts.

## Phase 2: Implementation
- [x] Task: Remove Display Logic
    - [x] Modify the identified UI class to remove or comment out the code that adds the count text to the panel.
    - [x] Ensure the panel layout remains intact (e.g., removing a label component but keeping the parent container).
- [ ] Task: Clean Up Unused Data Retrieval (Optional)
    - [ ] If the data retrieval for these counts is computationally expensive and no longer used elsewhere, safely remove that logic as well.

## Phase 3: Verification
- [ ] Task: Manual UI Testing
    - [ ] Build and run the application using `ant clean build` or `tools/build-mac-fixed.sh`.
    - [ ] Navigate the hierarchy and select a concept.
    - [ ] Verify that the bottom/foot panel no longer shows the counts.
    - [ ] Verify that the panel structure is still visible.
    - [ ] Ensure no UI exceptions are thrown during navigation.
- [ ] Task: Test Suite Verification
    - [ ] Run the existing unit tests to ensure no regressions were introduced by removing the logic.
    - [ ] Fix any tests that were specifically asserting the presence of these counts.