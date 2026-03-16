# ============================================================
#  push-to-github.ps1
#  Run this script whenever you want to sync your changes
#  to GitHub: Right-click -> "Run with PowerShell"
# ============================================================

$projectPath = $PSScriptRoot
Set-Location $projectPath

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Hospital Management System — Git Push   " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Show what changed
Write-Host "📂 Changed files:" -ForegroundColor Yellow
git status --short

Write-Host ""

# Ask for a commit message
$commitMsg = Read-Host "✏️  Enter a commit message (or press Enter for default)"
if ([string]::IsNullOrWhiteSpace($commitMsg)) {
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm"
    $commitMsg = "Update: $timestamp"
}

Write-Host ""
Write-Host "📦 Staging all changes..." -ForegroundColor Yellow
git add .

Write-Host "✅ Committing: '$commitMsg'" -ForegroundColor Green
git commit -m "$commitMsg"

Write-Host "🚀 Pushing to GitHub..." -ForegroundColor Yellow
git push origin main

Write-Host ""

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Done! Changes are live on GitHub." -ForegroundColor Green
    Write-Host "🔗 https://github.com/mayankawasthi619-boop/hopsital_management_system" -ForegroundColor Cyan
} else {
    Write-Host "❌ Push failed. Check the error above." -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to exit"
