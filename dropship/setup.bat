@echo off
chcp 65001 >nul
echo ╔════════════════════════════════════════╗
echo ║  Setup Pós Git Pull - Dropship       ║
echo ╚════════════════════════════════════════╝
echo.

:: Verificar se o VSCode está no PATH
where code >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  VSCode não encontrado no PATH!
    echo    Pulando comandos do VSCode...
    goto :maven_only
)

echo [1/5] 🧹 Limpando workspace do Java Language Server...
code --command "java.clean.workspace" --wait
if %ERRORLEVEL% EQU 0 (
    echo ✅ Workspace limpo
) else (
    echo ⚠️  Comando executado, aguardando...
)
timeout /t 3 /nobreak >nul

:maven_only
echo.
echo [2/5] 🗑️  Removendo diretório target...
if exist target (
    rmdir /s /q target
    echo ✅ Diretório target removido
) else (
    echo ℹ️  Diretório target não existe
)

echo.
echo [3/5] 🧼 Executando mvn clean...
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erro ao executar mvn clean
    pause
    exit /b 1
)
echo ✅ Clean executado com sucesso

echo.
echo [4/5] 📦 Compilando e gerando mappers...
call mvn compile
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erro ao compilar projeto
    pause
    exit /b 1
)
echo ✅ Compilação concluída

echo.
echo [5/5] 🔄 Verificando arquivos gerados...
if exist "target\generated-sources\annotations\br\dev\kajosama\dropship\api\mappers\UserMapperImpl.java" (
    echo ✅ UserMapperImpl.java gerado com sucesso!
) else (
    echo ⚠️  UserMapperImpl.java NÃO foi encontrado!
    echo    Verifique a anotação @Mapper(componentModel = "spring")
)

echo.
where code >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [6/5] 🔃 Recarregando VSCode...
    code --command "workbench.action.reloadWindow"
    echo ✅ Comando de reload enviado
) else (
    echo ℹ️  Por favor, recarregue o VSCode manualmente:
    echo    Ctrl+Shift+P -^> "Developer: Reload Window"
)

echo.
echo ╔════════════════════════════════════════╗
echo ║  ✅ Setup concluído com sucesso!      ║
echo ╚════════════════════════════════════════╝
echo.
pause