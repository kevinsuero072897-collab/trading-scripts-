package com.yourorg.tradingbot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TopstepXTrading endpoint for trading ES and NQ contracts using dynamically supplied tokens.
 * This class provides methods to get contract IDs and place orders for ES and NQ futures.
 */
public class TopstepXTrading {
    
    private static final String BASE_URL = "https://api.topstepx.com/api";
    
    private final String sessionToken;
    private final HttpClient httpClient;
    
    /**
     * Constructor that accepts a dynamically supplied token
     * @param token The session token to use for API authentication
     */
    public TopstepXTrading(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        this.sessionToken = token;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * Get contract ID for ES (E-mini S&P 500) futures
     * @return Contract ID for ES futures
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String getESContractId() throws IOException, InterruptedException {
        return getContractId("ES");
    }
    
    /**
     * Get contract ID for NQ (E-mini NASDAQ-100) futures
     * @return Contract ID for NQ futures
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String getNQContractId() throws IOException, InterruptedException {
        return getContractId("NQ");
    }
    
    /**
     * Get contract ID for a specific symbol
     * @param symbol The futures symbol (ES or NQ)
     * @return Contract ID for the symbol
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String getContractId(String symbol) throws IOException, InterruptedException {
        String url = BASE_URL + "/instruments/search";
        
        // Create simple JSON request body
        String requestBody = String.format("{\"symbol\":\"%s\",\"secType\":\"FUT\"}", symbol);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + sessionToken)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Failed to get contract ID for " + symbol + 
                                ": HTTP " + response.statusCode() + " - " + response.body());
        }
        
        String responseBody = response.body();
        
        // Simple pattern matching to extract contract ID from JSON response
        Pattern contractIdPattern = Pattern.compile("\"contractId\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = contractIdPattern.matcher(responseBody);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        throw new IOException("Contract ID not found for symbol: " + symbol + ". Response: " + responseBody);
    }
    
    /**
     * Place an order for ES futures
     * @param side Order side ("BUY" or "SELL")
     * @param quantity Number of contracts
     * @param orderType Order type ("MKT" for market, "LMT" for limit)
     * @param price Price for limit orders (can be null for market orders)
     * @return Order ID or confirmation
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String placeESOrder(String side, int quantity, String orderType, Double price) 
            throws IOException, InterruptedException {
        String contractId = getESContractId();
        return placeOrder(contractId, "ES", side, quantity, orderType, price);
    }
    
    /**
     * Place an order for NQ futures
     * @param side Order side ("BUY" or "SELL")
     * @param quantity Number of contracts
     * @param orderType Order type ("MKT" for market, "LMT" for limit)
     * @param price Price for limit orders (can be null for market orders)
     * @return Order ID or confirmation
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String placeNQOrder(String side, int quantity, String orderType, Double price) 
            throws IOException, InterruptedException {
        String contractId = getNQContractId();
        return placeOrder(contractId, "NQ", side, quantity, orderType, price);
    }
    
    /**
     * Place an order for a specific contract
     * @param contractId The contract ID
     * @param symbol The symbol for logging
     * @param side Order side ("BUY" or "SELL")
     * @param quantity Number of contracts
     * @param orderType Order type ("MKT" for market, "LMT" for limit)
     * @param price Price for limit orders (can be null for market orders)
     * @return Order ID or confirmation
     * @throws IOException if the request fails
     * @throws InterruptedException if the request is interrupted
     */
    private String placeOrder(String contractId, String symbol, String side, int quantity, 
                            String orderType, Double price) throws IOException, InterruptedException {
        
        // Validate inputs
        if (!side.equals("BUY") && !side.equals("SELL")) {
            throw new IllegalArgumentException("Side must be 'BUY' or 'SELL'");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (!orderType.equals("MKT") && !orderType.equals("LMT")) {
            throw new IllegalArgumentException("Order type must be 'MKT' or 'LMT'");
        }
        if (orderType.equals("LMT") && price == null) {
            throw new IllegalArgumentException("Price is required for limit orders");
        }
        
        String url = BASE_URL + "/trading/orders";
        
        // Create simple JSON request body
        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("{");
        requestBodyBuilder.append("\"contractId\":\"").append(contractId).append("\",");
        requestBodyBuilder.append("\"symbol\":\"").append(symbol).append("\",");
        requestBodyBuilder.append("\"side\":\"").append(side).append("\",");
        requestBodyBuilder.append("\"quantity\":").append(quantity).append(",");
        requestBodyBuilder.append("\"orderType\":\"").append(orderType).append("\"");
        
        if (price != null) {
            requestBodyBuilder.append(",\"price\":").append(price);
        }
        
        requestBodyBuilder.append("}");
        String requestBody = requestBodyBuilder.toString();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + sessionToken)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IOException("Failed to place " + symbol + " order: HTTP " + 
                                response.statusCode() + " - " + response.body());
        }
        
        String responseBody = response.body();
        
        // Simple pattern matching to extract order ID from JSON response
        Pattern orderIdPattern = Pattern.compile("\"orderId\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = orderIdPattern.matcher(responseBody);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Return the full response if no specific order ID field
        return responseBody;
    }
    
    /**
     * Example usage demonstrating ES and NQ trading with dynamically supplied token
     */
    public static void main(String[] args) {
        // Example token - in real usage, this would come from command line args,
        // environment variable, or token service
        String token = args.length > 0 ? args[0] : "EXAMPLE_TOKEN_HERE";
        
        try {
            // Create TopstepXTrading instance with dynamically supplied token
            TopstepXTrading tradingClient = new TopstepXTrading(token);
            
            System.out.println("=== TopstepXTrading Example Usage ===\n");
            
            // Example 1: Get contract IDs
            System.out.println("1. Getting Contract IDs:");
            try {
                String esContractId = tradingClient.getESContractId();
                System.out.println("ES Contract ID: " + esContractId);
            } catch (Exception e) {
                System.out.println("Failed to get ES contract ID: " + e.getMessage());
            }
            
            try {
                String nqContractId = tradingClient.getNQContractId();
                System.out.println("NQ Contract ID: " + nqContractId);
            } catch (Exception e) {
                System.out.println("Failed to get NQ contract ID: " + e.getMessage());
            }
            
            System.out.println();
            
            // Example 2: Place ES orders (commented out for safety - remove comments for actual trading)
            System.out.println("2. ES Order Examples (not executed - uncomment to trade):");
            System.out.println("   Market Buy: tradingClient.placeESOrder(\"BUY\", 1, \"MKT\", null)");
            System.out.println("   Limit Sell: tradingClient.placeESOrder(\"SELL\", 1, \"LMT\", 4500.0)");
            
            // Uncomment the following lines for actual trading:
            // String esBuyOrderId = tradingClient.placeESOrder("BUY", 1, "MKT", null);
            // System.out.println("ES Market Buy Order ID: " + esBuyOrderId);
            
            System.out.println();
            
            // Example 3: Place NQ orders (commented out for safety)
            System.out.println("3. NQ Order Examples (not executed - uncomment to trade):");
            System.out.println("   Market Buy: tradingClient.placeNQOrder(\"BUY\", 1, \"MKT\", null)");
            System.out.println("   Limit Sell: tradingClient.placeNQOrder(\"SELL\", 1, \"LMT\", 15000.0)");
            
            // Uncomment the following lines for actual trading:
            // String nqBuyOrderId = tradingClient.placeNQOrder("BUY", 1, "MKT", null);
            // System.out.println("NQ Market Buy Order ID: " + nqBuyOrderId);
            
            System.out.println();
            System.out.println("=== Example Complete ===");
            System.out.println("Note: Order placement examples are commented out for safety.");
            System.out.println("Uncomment the order placement lines to execute actual trades.");
            
        } catch (Exception e) {
            System.err.println("Error in TopstepXTrading example: " + e.getMessage());
            e.printStackTrace();
        }
    }
}