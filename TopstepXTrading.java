import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TopstepX Trading API client providing authentication and trading functionality.
 * 
 * This class provides methods to authenticate with the TopstepX API and obtain
 * session tokens for subsequent API calls.
 */
public class TopstepXTrading {
    
    private static final String LOGIN_URL = "https://api.topstepx.com/api/Auth/loginKey";
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a new TopstepXTrading instance.
     */
    public TopstepXTrading() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Authenticates with the TopstepX API using the /Auth/loginKey endpoint.
     * 
     * This method sends a POST request to the TopstepX authentication endpoint
     * with the provided credentials and returns a session token that can be used
     * for subsequent API calls.
     * 
     * @param userName The TopstepX username/email for authentication
     * @param apiKey The TopstepX API key for authentication
     * @return The session token string on successful authentication
     * @throws IOException If there's a network error or I/O issue
     * @throws InterruptedException If the HTTP request is interrupted
     * @throws RuntimeException If authentication fails (non-zero result code)
     * 
     * @example
     * <pre>
     * TopstepXTrading trading = new TopstepXTrading();
     * String token = trading.authenticateWithLoginKey("your@email.com", "yourApiKey");
     * System.out.println("Session token: " + token);
     * </pre>
     */
    public String authenticateWithLoginKey(String userName, String apiKey) 
            throws IOException, InterruptedException {
        
        // Validate input parameters
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("userName cannot be null or empty");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("apiKey cannot be null or empty");
        }
        
        // Create the JSON request body
        String requestBody = String.format("{\"userName\":\"%s\",\"apiKey\":\"%s\"}", 
                                         userName, apiKey);
        
        // Build the HTTP request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("accept", "text/plain")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        // Send the request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Parse the JSON response
        JsonNode responseJson = objectMapper.readTree(response.body());
        
        // Check if authentication was successful
        if (!responseJson.has("result")) {
            throw new RuntimeException("Invalid response format: missing 'result' field. Response: " + response.body());
        }
        
        int resultCode = responseJson.get("result").asInt();
        if (resultCode != 0) {
            throw new RuntimeException("Authentication failed with result code " + resultCode + ". Response: " + response.body());
        }
        
        // Extract and return the session token
        if (!responseJson.has("sessionToken")) {
            throw new RuntimeException("Invalid response format: missing 'sessionToken' field. Response: " + response.body());
        }
        
        return responseJson.get("sessionToken").asText();
    }
    
    /**
     * Example usage and demonstration of the authentication method.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TopstepXTrading <userName> <apiKey>");
            System.out.println("");
            System.out.println("Example:");
            System.out.println("  java TopstepXTrading your@email.com yourApiKey123");
            return;
        }
        
        String userName = args[0];
        String apiKey = args[1];
        
        try {
            TopstepXTrading trading = new TopstepXTrading();
            String sessionToken = trading.authenticateWithLoginKey(userName, apiKey);
            
            System.out.println("Authentication successful!");
            System.out.println("Session token: " + sessionToken);
            
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}