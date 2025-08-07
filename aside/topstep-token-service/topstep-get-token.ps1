# Topstep Token Service (PowerShell) - Java-style logic
# Place in root. Requires: PowerShell 7+, curl, .env file in root, token_cache.json in root

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
    if ($json.token -and $json.expiry) {
        $now = [int][double]::Parse((Get-Date -UFormat %s))
        if ($now -lt ($json.expiry - 60)) { # Renew 1 min before expiry
            return $json.token
        }
    }
    return $null
}

function Save-Token {
    param([string]$Token, [int]$Expiry, [string]$Path)
    $obj = @{ token = $Token; expiry = $Expiry }
    $obj | ConvertTo-Json | Set-Content $Path
}

function Get-NewToken {
    param($Email, $Password)
    $body = @{ email = $Email; kevinsuero072897@gmail.com = Mrkevins15@} | ConvertTo-Json
    $resp = curl -s -X POST "https://app.topstep.com/api/v2/auth" -H "Content-Type: application/json" -d $body
    $json = $resp | ConvertFrom-Json
    if ($json.token) {
        $expiry = [int][double]::Parse((Get-Date -UFormat %s)) + 3600 # Assume 1 hour token
        return @{ token = $json.token; expiry = $expiry }
    } else {
        throw "Failed to get token: $($resp)"
    }
}

try {
    $email = Get-EnvVar -Key 'TOPSTEP_EMAIL' -Path $EnvPath
    $password = Get-EnvVar -Key 'TOPSTEP_PASSWORD' -Path $EnvPath
    $token = Get-CachedToken -Path $CacheFile
    if (-not $token) {
        $tokendata = Get-NewToken -Email $email -Password $password
        Save-Token -Token $tokendata.token -Expiry $tokendata.expiry -Path $CacheFile
        $token = $tokendata.token
    }
    Write-Output $token
} catch {
    Write-Error $_
}
