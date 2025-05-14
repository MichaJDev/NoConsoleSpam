# Changelog

This document provides detailed information about changes in each version of NoConsoleSpam.

## 1.4.1

*Released: June 10, 2025*

### Major Changes

- Updated for compatibility with both Minecraft 1.21.4 and 1.21.5
- Support for multiple Minecraft versions in a single build
- Updated Fabric Loader dependency to work across versions
- Updated mappings to support both 1.21.4 and 1.21.5
- Updated Loom version to 1.6.12

### Technical Improvements

- Updated Java requirements to support Java 21
- Updated Log4j dependencies to 2.22.1
- Improved compatibility with different Fabric API versions
- Enhanced error handling with robust null-safety checks
- Fixed issues with configuration loading in server environments
- Improved exception handling in SpamFilter class
- Added graceful fallback mechanisms for initialization failures

## 1.4

*Released: May 15, 2025*

### Major Changes

- Enhanced exception logging system with source-specific organization
  - Exceptions are now organized into subdirectories by their source (mod/plugin/class)
  - Better identification of exception sources through stack trace analysis
  - Supports all Java exception types (RunTime, IO, etc.) and Fabric/Minecraft specific exceptions
  - More comprehensive exception information including cause and suppressed exceptions
  - Expanded support for Java standard exceptions including reflection, network, security, concurrent, and more

### New Configuration Options

- Added fine-grained control over exception logging:
  - `organizeBySource`: Controls whether exceptions are organized by source (default: true)
  - `captureRuntimeExceptions`: Controls logging of RuntimeExceptions (default: true)
  - `captureIOExceptions`: Controls logging of IOExceptions (default: true)
  - `captureFabricExceptions`: Controls logging of Fabric-specific exceptions (default: true)
  - `captureMinecraftExceptions`: Controls logging of Minecraft-specific exceptions (default: true)
  - `captureReflectionExceptions`: Controls logging of reflection-related exceptions (default: true)
  - `captureSecurityExceptions`: Controls logging of security-related exceptions (default: true)
  - `captureNetworkExceptions`: Controls logging of network-related exceptions (default: true)
  - `captureDataExceptions`: Controls logging of data-related exceptions (default: true)
  - `captureErrors`: Controls logging of JVM errors (default: true)
  - `captureConcurrentExceptions`: Controls logging of concurrent-related exceptions (default: true)

### Technical Improvements

- Improved exception source detection using regex pattern matching
- Enhanced log file content with more contextual information
- Better directory structure management for exception logs
- Added logger name, thread name, and level to exception context for improved diagnosis
- More efficient management of log files across directories
- Categorization of exceptions into logical groups
- Robust error handling for exception-logging itself to prevent cascading failures
- Added support for many more package patterns commonly used in Minecraft mods

### Other Changes

- Updated version number in metadata and documentation
- Improved startup logging with detailed exception logging configuration information
- Code refactoring for better maintainability and readability

## 1.3.0

*Released: May 2025*

### Major Changes

- Updated for compatibility with Minecraft 1.21.x (Fabric Loader and API)
- Updated dependencies and mappings for 1.21.x
- Updated documentation and mod metadata

## 1.2.1

*Released: May 5, 2025*

### Changes

- Removed unused features for streamlined codebase
- Simplified project structure
- Optimized jar file size

## 1.2.0

*Released: May 5, 2025*

### Major Changes

- Completely redesigned exception logging system
  - Each exception is now logged to a separate file in the "Console Errors" directory
  - Filenames include timestamp and exception type for easy identification
  - Improved exception handling with proper cleanup of old log files

### Technical Details

- Replaced Log4j-based logging implementation with direct file I/O for more control
- Added sorting mechanism to manage log file retention based on age
- Improved error reporting with better formatted exception details
- Simplified configuration interface for exception handling

### Fixed Issues

- Fixed potential resource leak in exception logging
- Addressed deprecation warnings in logging implementation
- Improved error capture with more comprehensive stack traces
- Deleted hot-reloading as it casts errors because of the priotisation of running order of the mod

## 1.1.0

*Released: May 5, 2025*

### Major Changes

- Added external configuration system
  - Moved filter patterns from hardcoded values to an external JSON file
  - Added support for custom spam patterns
  - Implemented hot-reloading of configuration changes
  - Created config directory structure at config/NoConsoleSpam/

### Added Patterns

- "your custom create"
- "Your custom createaddition"
- "Parsing error loading recipe"

### Technical Details

- Added JSON configuration parser using Google Gson
- Implemented fallback mechanism for when external config isn't available
- Added directory creation and validation
- Config file is automatically generated on first run

### Other Improvements

- Better error handling and fallback to defaults
- More descriptive logging messages
- Configuration documentation in README

## 1.0.0

*Released: April 30, 2025*

### Initial Release

- Basic console spam filtering functionality
- Hardcoded filter patterns for common spam sources
- Support for Minecraft 1.20.1 with Fabric
- Early loading with high priority

### Included Filter Patterns

- Lithium-related messages
- Block mismatch errors
- Server overloading messages
- Player movement validation warnings
- Class Analysis Errors

### Technical Features

- Log4j filter implementation
- Support for regex-based pattern matching
- Efficient filtering with minimal performance impact 
