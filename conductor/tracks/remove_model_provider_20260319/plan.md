# Implementation Plan: Remove Model Search Provider and Display

## Phase 1: Investigation & Code Location
- [ ] Task: Locate the search provider registry/initialization code where the "Model" provider is added.
- [ ] Task: Locate the UI rendering code where "(mod)" or "Model" is appended to search results.
- [ ] Task: Identify where search history or preferences are loaded/saved to ensure the "Model" provider is safely ignored.

## Phase 2: Implementation
- [ ] Task: Remove or disable the registration of the "Model" search provider.
- [ ] Task: Update the search result renderer to omit the "Model (mod)" display text.
- [ ] Task: Add logic to filter out or ignore the "Model" provider when loading user search history/preferences on startup.

## Phase 3: Testing & Verification
- [ ] Task: Perform a Clean and Build (`ant clean build`).
- [ ] Task: Run the application and verify the "Model" provider is absent from the QuickSearch dropdown.
- [ ] Task: Perform searches and ensure results do not display "(mod)".
- [ ] Task: Verify that any previously saved preferences or history related to "Model" are silently ignored and do not cause exceptions.
