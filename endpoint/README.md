# Endpoint Directory

This directory contains all endpoint and API client Java files for the trading system. This organized structure provides better maintainability and clear separation of concerns for all API-related functionality.

## Contents

### API Client Files

- **`PropFirmEndpoints.java`** - Main prop firm API client with authentication, token caching, and trading endpoint methods for Topstep platform
- **`TopstepTokenService.java`** - Dedicated Topstep token service with HTTP client implementation, token caching, and configuration management
- **`FetchTopstepToken.java`** - Command-line utility to fetch authentication tokens from Topstep using PowerShell integration
- **`FetchTopstepTokenApp.java`** - GUI application (Swing-based) for fetching Topstep authentication tokens with user-friendly interface

## Features

- **Token Management**: Automatic token caching and renewal across all endpoint services
- **Authentication**: Centralized authentication handling for prop firm platforms
- **API Integration**: HTTP client implementations for trading platform APIs
- **Configuration**: Support for multiple configuration methods (properties files, environment variables)

## Usage

### Compiling Files

```bash
# Compile basic files (no external dependencies)
javac FetchTopstepToken.java
javac FetchTopstepTokenApp.java

# For files requiring Jackson library dependency:
# javac -cp "path/to/jackson-databind.jar:." PropFirmEndpoints.java TopstepTokenService.java
```

### Running Applications

```bash
java FetchTopstepToken
java FetchTopstepTokenApp
# java PropFirmEndpoints  # Requires Jackson dependency
# java TopstepTokenService  # Requires Jackson dependency and config.properties
```

## Dependencies

### Required for All Files
- Java 8+

### External Dependencies (PropFirmEndpoints.java, TopstepTokenService.java)
- Jackson library for JSON processing (`com.fasterxml.jackson.databind.ObjectMapper`)
- Java HTTP Client (built-in since Java 11)

### Configuration Files
- `config.properties` - Required for TopstepTokenService.java
- `.env` file - Alternative configuration for PowerShell integration

## Future Endpoint Files

All future endpoint logic and API client files should be placed in this directory to maintain the organized structure and ensure consistency across the project.