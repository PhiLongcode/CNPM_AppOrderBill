$searchStr = "com.giadinh.apporderbill.presentation"
$replaceStr = "com.giadinh.apporderbill.javafx"
Get-ChildItem -Path "src\main" -Include *.java,*.fxml -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    if ($content -match [regex]::Escape($searchStr)) {
        $newContent = $content -replace [regex]::Escape($searchStr), $replaceStr
        Set-Content -Path $_.FullName -Value $newContent -NoNewline -Encoding UTF8
        Write-Host "Updated $($_.FullName)"
    }
}
