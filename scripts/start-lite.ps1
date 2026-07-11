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
    throw '请先设置 DB_PASSWORD 环境变量，或通过 -DatabasePassword 传入本机数据库密码。'
}

$env:DB_URL = $DatabaseUrl
$env:DB_USERNAME = $DatabaseUsername
$env:DB_PASSWORD = $DatabasePassword
$env:CARE_NEXUS_JWT_SECRET = 'local-lite-jwt-secret-change-before-production'

Start-Process -FilePath 'mvn.cmd' -ArgumentList 'spring-boot:run' -WorkingDirectory (Join-Path $root 'backend') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev' -WorkingDirectory (Join-Path $root 'frontend/admin-web') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev' -WorkingDirectory (Join-Path $root 'frontend/mobile-web') -WindowStyle Hidden
Start-Process -FilePath 'npm.cmd' -ArgumentList 'run','dev','--','--host','0.0.0.0','--port','5175' -WorkingDirectory (Join-Path $root 'frontend/portal-web') -WindowStyle Hidden

Write-Host 'CareNexus 正在启动：'
Write-Host '  门户：http://localhost:5175'
Write-Host '  管理端：http://localhost:5173'
Write-Host '  护工端：http://localhost:5174'
Write-Host '  后端：http://localhost:8080/api/v1/health'
