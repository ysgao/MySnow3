# Specification: Fix Application Crash on macOS (JNA/FSEvents)

## Overview
The MySnow3 application experiences an unexpected closure (crash) on macOS Tahoe 26.3.1 when left open but not actively used. The crash occurs while the Taxonomy Browser and Concept Details windows are active.

## Bug Description
The crash report indicates an `EXC_BAD_ACCESS (SIGABRT)` originating from a JNA native invocation interfacing with the macOS `FSEvents` framework. It appears to be related to a file system monitoring mechanism (like a file watcher) using JNA that fails with a memory protection failure when the application is idle.

## Functional Requirements
- Identify the exact component using JNA to hook into macOS `FSEvents` (likely a file watcher).
- Fix or replace the problematic native call/JNA implementation causing the memory protection failure (`KERN_PROTECTION_FAILURE`).
- Ensure the application remains stable and does not crash when left idle on macOS.

## Acceptance Criteria
- The application can be left open (with Taxonomy Browser and Concept Details active) for an extended period without crashing.
- No `EXC_BAD_ACCESS` or `SIGABRT` crashes related to `FSEvents` or JNA occur.
- Any background file monitoring functionality continues to work correctly (or is safely disabled if unnecessary).

## Out of Scope
- Fixing unrelated bugs or crashes.
- Major updates to the JNA library unless strictly necessary to resolve this specific crash.