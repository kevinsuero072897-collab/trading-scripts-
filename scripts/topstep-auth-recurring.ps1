# Requires -Version 7

# Load environment variables from .env file
$envPath = ".env"
if (Test-Path $envPath) {
    Get-Content $envPath | ForEach-Object {
        if ($_ -match '^\s*[^#][^=]*\s*=\s*(.*)$') {
            $key = $matches[1].Trim()
            $val = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($key, $val)
        }
    }
} else {
    Write-Host ".env file not found. Please create one with your credentials."
    exit 1
}

$userName = $env:TOPSTEP_USERNAME
$apiKey = $env:TOPSTEP_API_KEY

if (-not $userName -or -not $apiKey) {
    Write-Host "Missing Topstep credentials. Please check your .env file."
    exit 1
}

# Prepare headers and body
$headers = @{
    "Content-Type" = "application/json"
}
$body = @{
    userName = $userName
    apiKey   = $apiKey
} | ConvertTo-Json -Depth 2

# Send POST request to get a token
$tokenUrl = "https://api.topstepx.com/api/Auth/loginKey"

try {
    $response = Invoke-RestMethod -Uri $tokenUrl -Method POST -Headers $headers -Body $body
    if ($response.token) {
        Write-Host "Token acquired: $($response.token)"
        # Optionally save the token
        Set-Content -Path "topstep_token.txt" -Value $response.token
    } else {
        Write-Host "Failed to acquire token. Response: $($response | ConvertTo-Json -Depth 5)"
        exit 1
    }
} catch {
    Write-Host "Exception occurred: $($_.Exception.Message)"
    exit 1
}