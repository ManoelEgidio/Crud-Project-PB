@echo off
setlocal

if exist out rd /s /q out
mkdir out

powershell -NoProfile -Command "$files = Get-ChildItem -Path 'src/main/java' -Recurse -Filter *.java | Select-Object -ExpandProperty FullName; if($files.Count -eq 0){ Write-Error 'Nenhum arquivo fonte encontrado.'; exit 1 }; & javac -d 'out' $files; exit $LASTEXITCODE"
if errorlevel 1 goto end

java -cp out br.com.crud_project.app.Main

:end
endlocal
