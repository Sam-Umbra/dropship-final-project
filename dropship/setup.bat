@echo off
echo Limpando projeto...
call mvn clean

echo Compilando e gerando mappers...
call mvn compile

echo Setup concluido! Recarregue a janela do VSCode agora.
echo Pressione: Ctrl+Shift+P -^> 'Developer: Reload Window'
echo Depois Pressione: Ctrl+Shift+P -> Java: Clean Java Language Server Workspace
pause