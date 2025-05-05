# Changelog

This document provides detailed information about changes in each version of NoConsoleSpam.

## 1.2.0

*Released: May 5, 2025*

### Major Changes

- Completely redesigned exception logging system
  - Each exception is now logged to a separate file in the "Console Errors" directory
  - Filenames include timestamp and exception type for easy identification
  - Improved exception handling with proper cleanup of old log files
- Added color customization system
  - New colorize.json file for defining console output colors
  - Support for customizing log level colors and formats
  - Minecraft color code integration for rich text formatting

### Technical Details

- Replaced Log4j-based logging implementation with direct file I/O for more control
- Added sorting mechanism to manage log file retention based on age
- Improved error reporting with better formatted exception details
- Simplified configuration interface for exception handling
- Implemented color parsing system for console output styling

### Fixed Issues

- Fixed potential resource leak in exception logging
- Addressed deprecation warnings in logging implementation
- Improved error capture with more comprehensive stack traces

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