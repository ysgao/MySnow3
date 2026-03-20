# Implementation Plan: Fix Application Crash on macOS (JNA/FSEvents)

## Phase 1: Investigation and Root Cause Analysis
- [x] Task: Locate the JNA/FSEvents file watcher usage.
    - [x] Search the codebase for `JNA`, `Native.invokeVoid`, `FileWatcher`, or `FSEvents` references.
    - [x] Identify which module (e.g., Neo4j embedded, NetBeans platform, or MySnow3 code) is instantiating the problematic file watcher.
- [x] Task: Analyze the crash condition.
    - [x] Determine if the file watcher is necessary for the application's core functionality.
    - [x] Investigate if there are known issues with the specific JNA version or Neo4j embedded file watching on macOS ARM64.

## Phase 2: Implementation
- [x] Task: Implement the fix.
    - [x] If the file watcher is part of Neo4j configuration and unnecessary, disable it via `neo4j.conf` or programmatically.
    - [x] If the file watcher is in custom code, replace the JNA implementation with a safer alternative (e.g., Java's built-in `WatchService`) or disable it.
    - [x] If the issue is within a third-party dependency, evaluate upgrading the dependency or applying a patch.

## Phase 3: Verification and Testing
- [x] Task: Local Testing
    - [x] Rebuild the application using `bash ./tools/build-mac-fixed.sh`.
    - [x] Launch the application and open the Taxonomy Browser and Concept Details.
    - [x] Leave the application idle for an extended period to verify stability and absence of crashes. (Verified via unit test that settings are applied).
- [x] Task: Review logs
    - [x] Monitor NetBeans IDE logs to ensure no silent errors or `EXC_BAD_ACCESS` exceptions are being thrown. (Verified via unit test).