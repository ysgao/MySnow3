# Implementation Plan: Standardize Concept ID and FSN Formatting

## Objective
Standardize the formatting of SNOMED CT concepts (SCTID and FSN) across various export and copy actions in MySnow3 to follow the `SCTID |FSN|` format.

## Key Files & Context
- **`QuerySCT_UI/src/org/ihtsdo/mysnow/querysct_ui/RootNode.java`**: Handles the "Copy Concept" clipboard action.
- **`QuerySCT_Impl/src/org/ihtsdo/mysnow/querysct_impl/QuerySCTimpl.java`**: Handles the generation of Parent, Children, and Descendant lists, as well as the Transitive Closure table.

## Implementation Steps

### Phase 1: Update UI/Clipboard Formatting
- [ ] Task: Modify `getSctIdFsn()` in `RootNode.java`.
    - Change return format from `ID|FSN|` (or similar) to `ID |FSN|`.
    - Ensure there is exactly one space before the first pipe.

### Phase 2: Update List Generation Formatting
- [ ] Task: Modify `findDirectSuperNodes(long sctid)` in `QuerySCTimpl.java`.
    - Change output format from `id+"|"+fsn` to `id+" |"+fsn+"|"`.
- [ ] Task: Modify `findDirectSubNodes(long sctid)` in `QuerySCTimpl.java`.
    - Change output format from `id+"|"+fsn` to `id+" |"+fsn+"|"`.
- [ ] Task: Modify `findAllSubNodes(long sctid)` in `QuerySCTimpl.java`.
    - Change output format from `conceptid + "|" + conceptfsn` to `conceptid + " |" + conceptfsn + "|"`.

### Phase 3: Update Transitive Closure Table Formatting
- [ ] Task: Modify `findISATCNodes(long sctid)` in `QuerySCTimpl.java`.
    - Retrieve FSN for both `myStartNode` and `myPath.endNode()`.
    - Format both columns as `id+" |"+fsn+"|"`.
    - Update the header if appropriate (though standard headers are usually `supertypeId` and `subtypeId`).

## Verification & Testing
- [ ] **Verification - Copy Concept:**
    - Launch MySnow3.
    - Right-click a concept in the taxonomy browser.
    - Select "Copy Concept" (or verify the context menu triggers `clipboardCopy`).
    - Paste into a text editor and verify format is `SCTID |FSN|`.
- [ ] **Verification - Generated Lists:**
    - Right-click a concept and select "Generate Children List".
    - Open the generated `.txt` file in the user's home directory.
    - Verify entries are in `SCTID |FSN|` format.
    - Repeat for "Generate Parent List" and "Generate Descendant List".
- [ ] **Verification - Transitive Closure:**
    - Right-click a concept and select "Generate Transitive Closure Table".
    - Open the generated `.txt` file.
    - Verify both columns use the `SCTID |FSN|` format.
