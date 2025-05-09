# NoConsoleSpam

A Minecraft Fabric mod that reduces console spam by filtering out unnecessary messages and warnings.

## Features

- Filters out common spam patterns including:
  - Lithium-related messages
  - Mismatch block errors
  - Server overloading messages
  - Player movement warnings
  - Class Analysis Errors
  - Support for custom patterns via configuration
- External JSON configuration file for easy customization
- Enhanced exception logging system:
  - Organizes exceptions by source (mod/plugin/class)
  - Captures all types of Java exceptions (Runtime, IO, Reflection, Security, Network, etc.)
  - Logs JVM errors with detailed context
  - Detailed exception information including cause, thread, logger, and context
  - Individual error logs with full stack traces
  - Automatic categorization of exceptions into logical groups
- Robust error handling with graceful recovery mechanisms
- Null-safe implementation for server stability
- Lightweight and efficient
- Compatible with Minecraft 1.20.1, 1.21.x, and 1.21.4

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.20.1, 1.21.x, or 1.21.4
2. Download the latest version (1.4.1 or newer) of NoConsoleSpam from the [releases page](https://github.com/MichaJDev/NoConsoleSpam/releases) or check the [versions list](VERSIONS.md)
3. Place the mod JAR file in your Minecraft mods folder
4. Launch Minecraft with Fabric Loader

## Downloads

All versions of the mod can be found in the [VERSIONS.md](VERSIONS.md) file, which is automatically updated when new releases are published.

## Configuration

The mod now uses an external configuration file located at `config/NoConsoleSpam/spamfilters.json`. This file is automatically created when the mod is first run.

You can customize the following settings:
- Spam patterns (using regular expressions)
- Ignored loggers (to completely silence specific loggers)
- Exception logging settings

Example configuration:

```json
{
  "spamPatterns": [
    ".*Lithium.*",
    ".*mismatch.*",
    ".*overloading.*",
    ".*moving.*wrongly.*",
    ".*Class Analysis Error.*"
  ],
  "ignoredLoggers": [
    "net.minecraft.class_5458",
    "net.minecraft.class_5459",
    "net.minecraft.class_5460"
  ],
  "exceptionSettings": {
    "captureExceptions": true,
    "logFile": "logs/exceptions.log",
    "maxLogSize": 10485760,
    "maxBackupIndex": 3,
    "organizeBySource": true,
    "captureRuntimeExceptions": true,
    "captureIOExceptions": true,
    "captureFabricExceptions": true,
    "captureMinecraftExceptions": true,
    "captureReflectionExceptions": true,
    "captureSecurityExceptions": true,
    "captureNetworkExceptions": true,
    "captureDataExceptions": true,
    "captureErrors": true,
    "captureConcurrentExceptions": true
  }
}
```

## Exception Logging

Exceptions are now logged individually to separate files in the `Console Errors` directory. By default, the exceptions are organized into subdirectories based on their source (mod/plugin/class), making it much easier to track down issues from specific components.

Each exception log file contains:
- Timestamp and exception type
- Source identification (what mod/plugin/class caused it)
- Exception category (Runtime, IO, Reflection, etc.)
- Thread name, logger name, and log level
- Full exception message and stack trace
- Cause information, when available
- Any suppressed exceptions

This enhanced logging system provides much more context for debugging issues in Minecraft and Fabric mods.

### Exception Logging Settings

The new exception logging system provides several options to control what gets logged:

- `organizeBySource`: When enabled, exceptions are organized into subdirectories by their source
- `captureRuntimeExceptions`: Controls whether to log RuntimeExceptions (NullPointerException, etc.)
- `captureIOExceptions`: Controls whether to log IOExceptions (file errors)
- `captureFabricExceptions`: Controls whether to log Fabric-specific exceptions
- `captureMinecraftExceptions`: Controls whether to log Minecraft-specific exceptions
- `captureReflectionExceptions`: Controls whether to log reflection-related exceptions
- `captureSecurityExceptions`: Controls whether to log security-related exceptions
- `captureNetworkExceptions`: Controls whether to log network-related exceptions
- `captureDataExceptions`: Controls whether to log data-related exceptions
- `captureErrors`: Controls whether to log JVM errors
- `captureConcurrentExceptions`: Controls whether to log concurrent-related exceptions

## Changelog

### [1.4.1](CHANGES.md#141) - Current
- Updated for compatibility with Minecraft 1.21.4 and 1.21.5
- Updated Fabric Loader dependency to 0.15.11
- Updated Java requirements to Java 21
- Updated Log4j dependencies to 2.22.1
- Enhanced error handling with robust null-safety checks
- Fixed issues with configuration loading in server environments

### [1.4](CHANGES.md#14)
- Enhanced exception logging with organization by source
- Added support for all Java standard exceptions with categorization
- Improved exception context information
- Added configuration options for different exception types
- Better identification of exception sources including mods and plugins

### [1.3.0](CHANGES.md#130)
- Added support for Minecraft 1.21.x
- Updated dependencies
- Performance optimizations

### [1.2.1](CHANGES.md#121)
- Streamlined codebase by removing unused features
- Optimized jar file size
- Simplified project structure

### [1.2.0](CHANGES.md#120)
- Added individual error logging to separate files in "Console Errors" directory
- Improved exception handling and logging

### [1.1.0](CHANGES.md#110)
- Added external configuration file support
- Moved spam filters to config/NoConsoleSpam/spamfilters.json
- Added more default spam patterns

### [1.0.0](CHANGES.md#100)
- Initial release
- Basic console spam filtering

For more detailed information about each release, see the [CHANGES.md](CHANGES.md) file.

## Contributing

Feel free to submit issues and pull requests. When submitting a pull request, please ensure that your changes are well-tested and documented.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 