import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FetchTopstepToken {
    public static void main(String[] args) throws Exception {
        // Path to the script (adjust if needed)
        String scriptPath = "./aside/topstep-token-service/topstep-get-token.ps1";

        // Use "pwsh" for PowerShell 7+ (cross-platform), "powershell" for Windows PowerShell
        ProcessBuilder pb = new ProcessBuilder("pwsh", scriptPath);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String token = reader.readLine(); // Read the first line (the token)
            System.out.println("Fetched token: " + token);
            // Use the token in the rest of your Java bot logic here!
        }
        process.waitFor();
    }
}