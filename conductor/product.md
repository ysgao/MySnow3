# Product Definition: MySnow3

## Overview
MySnow3 is a specialized NetBeans Platform application designed for the visualization and exploration of SNOMED CT terminology. It provides a robust interface for clinical terminologists to browse complex hierarchies, view detailed concept information, and perform efficient searches across the SNOMED CT graph.

## Core Features
- **Hierarchical Taxonomy Browsing:** An explorer window that provides a tree view of the SNOMED CT taxonomy, rooted at the top-level SNOMED CT concept (SCTID: 138875005).
- **Concept Details:** A detailed view of SNOMED CT concepts, including:
    - Fully Specified Name (FSN).
    - Preferred Terms (PT) and Synonyms.
    - Text Definitions.
    - Modeling Relationships (ISA and non-ISA).
    - Simple Map Refsets.
    - Parent, child, and descendant counts.
- **QuickSearch:** Multi-category search providers targeting specific sub-hierarchies of SNOMED CT, allowing for rapid concept location.

## Target Audience
- **Clinical Terminologists:** Users who need to navigate and understand the structure and content of SNOMED CT.
- **Healthcare Software Developers:** Developers integrating SNOMED CT into their applications who need a tool to verify and explore the terminology structure.
- **Data Scientists:** Researchers working with clinical data who require a deep understanding of the SNOMED CT ontology.

## Success Metrics
- **Performance:** Smooth navigation and searching even with large SNOMED CT datasets (millions of nodes).
- **Accuracy:** Precise rendering of relationships and concept metadata as defined in the RF2 source files.
- **Stability:** Reliability on macOS, specifically avoiding deadlocks between Java's EDT and the native AppKit thread.
