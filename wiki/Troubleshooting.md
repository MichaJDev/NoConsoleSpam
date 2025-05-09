# Troubleshooting

This page contains information about common issues and solutions for NoConsoleSpam.

## Common Issues

### NullPointerException during initialization

Prior to version 1.4.1, some users experienced NullPointerExceptions during the initialization of NoConsoleSpam, especially in server environments. This has been fixed in version 1.4.1 with improved error handling and null-safety checks.

If you're experiencing this issue, update to version 1.4.1 or later.

### Configuration isn't being loaded

If your custom configuration isn't being applied:

1. Verify the config file exists at `config/NoConsoleSpam/spamfilters.json`
2. Check the file for formatting errors (valid JSON)
3. Ensure you have the correct permissions for the config directory

As of version 1.4.1, NoConsoleSpam includes fallback mechanisms to handle configuration loading errors gracefully, using default values when necessary.

### Filter not working as expected

If spam messages aren't being filtered properly:

1. Check your `spamfilters.json` file to ensure your patterns are correct
2. Remember that patterns use regular expressions (regex) syntax
3. Try simplifying complex patterns for better matching
4. Check the server logs for any errors related to pattern matching

### Capturing exceptions but not seeing logs

If exceptions are being captured but you don't see log files:

1. Ensure `captureExceptions` is set to `true` in your configuration
2. Check if the `Console Errors` directory exists and has proper write permissions
3. Verify the specific exception type settings match what you're trying to capture

## Recovering from errors

As of version 1.4.1, NoConsoleSpam has improved error handling that will:

1. Attempt to recover from initialization failures
2. Initialize default configurations when loading fails
3. Handle null values gracefully throughout the codebase
4. Log detailed error information to help diagnose issues

## Getting Help

If you're still experiencing issues:

1. Check the [GitHub Issues](https://github.com/MichaJDev/NoConsoleSpam/issues) to see if it's a known problem
2. Look for error messages in your logs that might provide more details
3. Create a new issue with detailed information about your environment and the problem

When reporting issues, please include:

- NoConsoleSpam version
- Minecraft version
- Fabric Loader version
- Any relevant log files
- Steps to reproduce the issue
