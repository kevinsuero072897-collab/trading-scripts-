# TopstepXTrading Authentication

This document describes the TopstepX authentication implementation using the /Auth/loginKey endpoint.

## Overview

The `TopstepXTrading.java` class provides a method to authenticate with the TopstepX API and obtain session tokens for subsequent API calls.

## Authentication Method

### `authenticateWithLoginKey(String userName, String apiKey)`

Authenticates with the TopstepX API using the `/Auth/loginKey` endpoint.

**Parameters:**
- `userName` (String): The TopstepX username/email for authentication
- `apiKey` (String): The TopstepX API key for authentication

**Returns:**
- `String`: The session token on successful authentication

**Throws:**
- `IllegalArgumentException`: If userName or apiKey are null or empty
- `IOException`: If there's a network error or I/O issue
- `InterruptedException`: If the HTTP request is interrupted
- `RuntimeException`: If authentication fails (non-zero result code)

## Usage Example

```java
import java.io.IOException;

public class Example {
    public static void main(String[] args) {
        try {
            TopstepXTrading trading = new TopstepXTrading();
            
            // Replace with your actual credentials
            String userName = "your@email.com";
            String apiKey = "yourApiKey123";
            
            // Authenticate and get session token
            String sessionToken = trading.authenticateWithLoginKey(userName, apiKey);
            
            System.out.println("Authentication successful!");
            System.out.println("Session token: " + sessionToken);
            
            // Use the session token for subsequent API calls
            // ...
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid parameters: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Authentication failed: " + e.getMessage());
        }
    }
}
```

## Command Line Usage

You can also run the class directly from the command line:

```bash
# Compile with Jackson dependencies
javac -cp ".:*" TopstepXTrading.java

# Run with your credentials
java -cp ".:*" TopstepXTrading your@email.com yourApiKey123
```

## API Endpoint Details

The method makes a POST request to:
- **URL**: `https://api.topstepx.com/api/Auth/loginKey`
- **Content-Type**: `application/json`
- **Accept**: `text/plain`

**Request Body Format:**
```json
{
  "userName": "your@email.com",
  "apiKey": "yourApiKey123"
}
```

**Success Response Format:**
```json
{
  "result": 0,
  "sessionToken": "your-session-token-here",
  "expiry": 1234567890
}
```

**Error Response Format:**
```json
{
  "result": 1,
  "error": "Authentication failed"
}
```

## Dependencies

This implementation requires the Jackson library for JSON processing:
- `jackson-databind-2.15.2.jar`
- `jackson-core-2.15.2.jar`
- `jackson-annotations-2.15.2.jar`

## Error Handling

The method includes comprehensive error handling:

1. **Input Validation**: Checks for null/empty parameters
2. **HTTP Errors**: Handles network connectivity issues
3. **JSON Parsing**: Validates response format
4. **Authentication Errors**: Checks result code from API
5. **Missing Fields**: Ensures required response fields are present

## Integration with Other Code

The authentication method is designed to be easily integrated into existing trading systems:

```java
// Example integration
public class TradingBot {
    private TopstepXTrading topstepX;
    private String sessionToken;
    
    public TradingBot() {
        this.topstepX = new TopstepXTrading();
    }
    
    public void initialize(String userName, String apiKey) throws Exception {
        // Get authentication token
        this.sessionToken = topstepX.authenticateWithLoginKey(userName, apiKey);
        
        // Now ready for trading API calls
    }
    
    // ... rest of trading logic
}
```

## Comparison with PowerShell Implementation

This Java implementation matches the functionality of the PowerShell sample:

| Feature | PowerShell | Java |
|---------|------------|------|
| Endpoint | `/Auth/loginKey` | `/Auth/loginKey` |
| Method | POST | POST |
| Content-Type | `application/json` | `application/json` |
| Request Format | `{userName, apiKey}` | `{userName, apiKey}` |
| Response Parsing | JSON | JSON (Jackson) |
| Error Handling | try/catch | Exception handling |
| Success Check | `result == 0` | `result == 0` |