@ECHO OFF
for /f "tokens=*" %%I in ('dir /b /s *.png') do ( call :renameFile %%I)
goto End

:renameFile
set path=%~p1
set filename=%~n1
set extension=%~x1
set volume=%~d1
set filename=%filename:.=_%
set oldPath=%~1
set newPath=%volume%%path%%filename%%extension%
echo ###############
if not "%oldPath%" == "%newPath%" (
	echo move %oldPath% to %newPath%
	move /y %oldPath% %newPath%
) else (
	echo nothing to move here %oldPath%
)
goto :eof
:End
PAUSE