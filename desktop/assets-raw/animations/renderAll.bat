@ECHO OFF
for /f "tokens=*" %%I in ('dir /b /s *.sifz') do ( 
	synfig -T 4 %%I
)
CALL remove_dot.bat