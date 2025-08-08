# Topstep Token Service

Authentication service for Topstep trading platform with token caching and management.

## Contents

- **`TopstepTokenService.java`** - Java-based token service with HTTP client implementation, token caching, and configuration management (moved to `../../endpoint/TopstepTokenService.java`)
- **`topstep-get-token.ps1`** - PowerShell 7+ equivalent implementation for token retrieval with environment variable support

## Features

- **Token Caching**: Automatic token caching to avoid unnecessary authentication requests
- **Configuration Management**: Support for both `config.properties` (Java) and `.env` files (PowerShell)
- **Token Expiry Handling**: Automatic token renewal before expiration (1 minute buffer)
- **HTTP Integration**: Modern HTTP client implementation for API communication
- **Cross-platform**: PowerShell script works on Windows, Linux, and macOS

## Usage

### Java Implementation

1. Create a `config.properties` file:
```properties
topstep.email=your-email@example.com
topstep.password=your-password
```

2. Compile and run:
```bash
# Note: TopstepTokenService.java has been moved to ../../endpoint/
cd ../../endpoint
javac TopstepTokenService.java  # Requires Jackson dependency
java TopstepTokenService
```

### PowerShell Implementation

1. Create a `.env` file:
```env
TOPSTEP_EMAIL=your-email@example.com
TOPSTEP_PASSWORD=your-password
```

2. Run the script:
```powershell
pwsh topstep-get-token.ps1
```

## Dependencies

### Java
- Java 8+
- Jackson library for JSON processing (`com.fasterxml.jackson.databind.ObjectMapper`)
- Java HTTP Client (built-in since Java 11)

### PowerShell
- PowerShell 7+
- `curl` command available in PATH
- `.env` file in the root directory

## Output

Both implementations create a `token_cache.json` file with the following structure:
```json
{
  "token": "your-jwt-token-here",
  "expiry": 1234567890
}
```

The token is automatically renewed when it's within 60 seconds of expiration.