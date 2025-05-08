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
- Individual error logging to separate files for better debugging
- Lightweight and efficient
- Compatible with Minecraft 1.20.1

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.20.1
2. Download the latest version of NoConsoleSpam from the [releases page](https://github.com/MichaJDev/NoConsoleSpam/releases) or check the [versions list](VERSIONS.md)
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
    "maxBackupIndex": 3
  }
}
```

## Exception Logging

Exceptions are now logged individually to separate files in the `Console Errors` directory. Each exception gets its own file with a timestamp and exception type in the filename. This makes debugging much easier as you can easily identify and review specific errors.

## Changelog

### [[1.3.0]](https://github.com/MichaJDev/NoConsoleSpam/releases/tag/1.3.0)
- Cleaned more code
- Fixed some small bugs and typos
- Release candidate
  
### [1.2.1] - Current
- Streamlined codebase by removing unused features
- Optimized jar file size
- Simplified project structure

### [1.2.0]
- Added individual error logging to separate files in "Console Errors" directory
- Improved exception handling and logging

### [1.1.0]
- Added external configuration file support
- Moved spam filters to config/NoConsoleSpam/spamfilters.json
- Added more default spam patterns

### [1.0.0]
- Initial release
- Basic console spam filtering

For more detailed information about each release, see the [CHANGES.md](CHANGES.md) file.

## Contributing

Feel free to submit issues and pull requests. When submitting a pull request, please ensure that your changes are well-tested and documented.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
