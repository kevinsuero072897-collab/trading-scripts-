// Combined Prop Firm Endpoints and Token Service Example

import java.io.*;
import java.net.URI;
import java.net.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropFirmEndpoints {
    private static final String TOKEN_CACHE_FILE = "token_cache.json";
    private static final String LOGIN_URL = "https://api.topstepx.com/api/Auth/loginKey";

    private String userName = "kevinsuero072897@gmail.com";
    private String apiKey = "7IrbO/+ieWsRK83krOrNCPKuTFmD/5nkALj9WMZBEz4=";
    private String sessionToken;
    private long expiry;

    public PropFirmEndpoints() throws Exception {
        loadTokenCache();
        if (sessionToken == null || isTokenExpiring()) {
            fetchAndCacheToken();
        }
    }

    private void loadTokenCache() throws IOException {
        File cacheFile = new File(TOKEN_CACHE_FILE);
        if (cacheFile.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(cacheFile);
            sessionToken = node.get("sessionToken").asText();
            expiry = node.has("expiry") ? node.get("expiry").asLong() : 0;
        }
    }

    private boolean isTokenExpiring() {
        return expiry == 0 || System.currentTimeMillis() / 1000 >= (expiry - 60);
    }

    private void fetchAndCacheToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String body = String.format("{\"userName\":\"%s\",\"apiKey\":\"%s\"}", userName, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("accept", "text/plain")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        var json = mapper.readTree(response.body());

        if (json.get("result").asInt() != 0) {
            throw new RuntimeException("Failed to authenticate: " + json.toString());
        }

        sessionToken = json.get("sessionToken").asText();
        expiry = json.has("expiry") ? json.get("expiry").asLong() : (System.currentTimeMillis() / 1000) + 3600;

        ObjectNode cache = mapper.createObjectNode();
        cache.put("sessionToken", sessionToken);
        cache.put("expiry", expiry);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(TOKEN_CACHE_FILE), cache);
    }

    // Example: Placeholder for a trading endpoint method
    public void placeOrder(String symbol, String side, int quantity, String type) throws Exception {
        String orderUrl = "https://api.topstepx.com/api/trading/orders"; // Replace with actual endpoint
        HttpClient client = HttpClient.newHttpClient();
        String body = String.format("{\"symbol\":\"%s\",\"side\":\"%s\",\"quantity\":%d,\"type\":\"%s\"}",
                symbol, side, quantity, type);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(orderUrl))
                .header("Authorization", "Bearer " + sessionToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Order response: " + response.body());
    }

    public static void main(String[] args) throws Exception {
        PropFirmEndpoints api = new PropFirmEndpoints();
        // Example: Place a market buy order (update with valid params)
        // api.placeOrder("ESU5", "buy", 1, "market");
        System.out.println("Session token: " + api.sessionToken);
    }
}