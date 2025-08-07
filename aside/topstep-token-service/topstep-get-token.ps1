# Topstep Token Auto-Renew Script (PowerShell)
# Requires: PowerShell 7+, curl, .env file in same directory with TOPSTEP_EMAIL and TOPSTEP_PASSWORD

param(
    [string]$EnvPath = "$(Join-Path $PSScriptRoot '.env')",
    [string]$CacheFile = "$(Join-Path $PSScriptRoot 'token_cache.json')"
)

function Get-EnvVar {
    param([string]$Key, [string]$Path)
    if (!(Test-Path $Path)) { throw ".env file not found at $Path" }
    $lines = Get-Content $Path | Where-Object { $_ -match "^\s*${Key}\s*=" }
    if ($lines) {
        $lines[0] -replace "^\s*${Key}\s*=\s*", ''
    } else {
        throw "Key '$Key' not found in $Path"
    }
}

function Get-CachedToken {
    param([string]$Path)
    if (!(Test-Path $Path)) { return $null }
    $json = Get-Content $Path -Raw | ConvertFrom-Json
    if ($json.expires_at -and ($json.expires_at -as [datetime]) -gt (Get-Date).AddMinutes(1)) {
        return $json.token
    }
    return $null
}

function Save-Token {
    param([string]$Token, [datetime]$Expiry, [string]$Path)
    $obj = @{ token = $Token; expires_at = $Expiry.ToString("o") }
    $obj | ConvertTo-Json | Set-Content $Path
}

function Get-NewToken {
    param($Email, $Password)
    $body = @{ email = $Email; password = $Password } | ConvertTo-Json
    $resp = curl -s -X POST "https://auth.topstep.com/api/v1/login" -H "Content-Type: application/json" -d $body
    $json = $resp | ConvertFrom-Json
    if ($json.token -and $json.expiry) {
        return @{ token = $json.token; expires_at = $json.expiry }
    } else {
        throw "Failed to get token: $($resp)"
    }
}

try {
    $email = Get-EnvVar -Key 'TOPSTEP_EMAIL' -Path $EnvPath
    $password = Get-EnvVar -Key 'TOPSTEP_PASSWORD' -Path $EnvPath
    $token = Get-CachedToken -Path $CacheFile
    if (-not $token) {
        Write-Host "Renewing Topstep token..."
        $tokendata = Get-NewToken -Email $email -Password $password
        Save-Token -Token $tokendata.token -Expiry ($tokendata.expires_at -as [datetime]) -Path $CacheFile
        $token = $tokendata.token
    }
    Write-Output $token
} catch {
    Write-Error $_
}