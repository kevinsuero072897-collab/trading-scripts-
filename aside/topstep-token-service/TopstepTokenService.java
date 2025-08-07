import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TopstepTokenService {

    private static final String CONFIG_FILE = "config.properties";
    private static final String TOKEN_FOLDER = "token";
    private static final String TOKEN_CACHE = TOKEN_FOLDER + "/token_cache.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String email;
    private final String password;

    public TopstepTokenService() throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        }
        this.email = props.getProperty("topstep.email");
        this.password = props.getProperty("topstep.password");
        if (email == null || password == null) {
            throw new IllegalStateException("Email or password missing in config.properties.");
        }
        ensureTokenFolder();
    }

    private void ensureTokenFolder() throws IOException {
        Path folder = Paths.get(TOKEN_FOLDER);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
    }

    public String getToken() throws IOException, InterruptedException {
        TokenCache cache = readTokenCache();
        long now = Instant.now().getEpochSecond();
        if (cache != null && cache.token != null && cache.expiry > now + 60) {
            return cache.token;
        }
        TokenCache newCache = fetchAndCacheToken();
        return newCache.token;
    }

    private TokenCache readTokenCache() {
        File cacheFile = new File(TOKEN_CACHE);
        if (!cacheFile.exists()) return null;
        try {
            return mapper.readValue(cacheFile, TokenCache.class);
        } catch (IOException e) {
            // Ignore and force refresh
            return null;
        }
    }

    private TokenCache fetchAndCacheToken() throws IOException, InterruptedException {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);

        String jsonPayload = mapper.writeValueAsString(payload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://app.topstep.com/api/v2/auth"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to get token: " + response.statusCode() + " " + response.body());
        }

        Map<?, ?> resp = mapper.readValue(response.body(), Map.class);
        if (!resp.containsKey("token")) {
            throw new IOException("No token in response: " + response.body());
        }
        String token = (String) resp.get("token");
        long expiry = Instant.now().getEpochSecond() + 3600; // default 1 hour
        if (resp.get("expiry") instanceof String) {
            try {
                expiry = Instant.parse((String) resp.get("expiry")).getEpochSecond();
            } catch (Exception ignored) {}
        }
        TokenCache cache = new TokenCache(token, expiry);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(TOKEN_CACHE), cache);
        return cache;
    }

    private static class TokenCache {
        public String token;
        public long expiry;

        public TokenCache() {}

        public TokenCache(String token, long expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }

    // Demo main method
    public static void main(String[] args) {
        try {
            TopstepTokenService service = new TopstepTokenService();
            String token = service.getToken();
            System.out.println("Topstep token: " + token);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}