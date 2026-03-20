# Specification: Standardize Concept ID and FSN Formatting

## Overview
MySnow3 currently uses an inconsistent format for representing SNOMED CT concepts in various export and copy actions. The current format is often `SCTID|FSN` (e.g., `281722005|Skin of part of back (body structure)`). This track aims to standardize this formatting to the industry-standard `SCTID |FSN|` format.

## Functional Requirements
- Update the string representation of SNOMED CT concepts used in specific context menu actions to follow the format: `SCTID |FSN|`.
- **Target Actions:**
    - **Copy Concept:** When a user copies a concept ID/FSN to the clipboard.
    - **Generate Children List:** Output format in the generated list.
    - **Generate Descendant List:** Output format in the generated list.
    - **Generate Parent List:** Output format in the generated list.
    - **Generate Transitive Closure Table:** Output format in the generated table.

## Non-Functional Requirements
- **Consistency:** Ensure the exact same format (`SCTID |FSN|`) is used across all specified actions.
- **Accuracy:** Ensure the SCTID and FSN are correctly retrieved from the Neo4j database before formatting.

## Acceptance Criteria
- Copying a concept (e.g., SCTID `281722005`) results in `281722005 |Skin of part of back (body structure)|` in the clipboard.
- All generated lists (Children, Descendants, Parents) use the `SCTID |FSN|` format for each entry.
- The Transitive Closure table generation uses the `SCTID |FSN|` format for concept columns.

## Out of Scope
- Changing the formatting in the main Taxonomy Browser tree view (unless it directly impacts the specified actions).
- Changing the formatting in the Concept Details window (unless it directly impacts the specified actions).
- General UI layout changes.
