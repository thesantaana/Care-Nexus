param(
    [string]$DatabaseUrl = $env:DB_URL,
    [string]$DatabaseUsername = $env:DB_USERNAME,
    [string]$DatabasePassword = $env:DB_PASSWORD
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot

if (-not $DatabaseUrl) { $DatabaseUrl = 'jdbc:mysql://localhost:3306/care_nexus?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai' }
if (-not $DatabaseUsername) { $DatabaseUsername = 'root' }
if (-not $DatabasePassword) {
    throw 'Set DB_PASSWORD or pass -DatabasePassword before starting CareNexus Lite.'
}

$env:DB_URL = $DatabaseUrl
$env:DB_USERNAME = $DatabaseUsername
$env:DB_PASSWORD = $DatabasePassword
$env:CARE_NEXUS_JWT_SECRET = 'local-lite-jwt-secret-change-before-production'

Start-Process -FilePath 'mvn.cmd' -ArgumentList 'spring-boot:run' -WorkingDirectory (Join-Path $root 'backend') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev' -WorkingDirectory (Join-Path $root 'frontend/admin-web') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev' -WorkingDirectory (Join-Path $root 'frontend/mobile-web') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev','--','--host','0.0.0.0','--port','5175' -WorkingDirectory (Join-Path $root 'frontend/portal-web') -WindowStyle Hidden

Write-Host 'CareNexus Lite is starting:'
Write-Host '  Portal: http://localhost:5175'
Write-Host '  Admin:  http://localhost:5173'
Write-Host '  Care:   http://localhost:5174'
Write-Host '  API:    http://localhost:8080/api/v1/health'
