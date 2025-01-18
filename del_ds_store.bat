@echo off
setlocal enabledelayedexpansion

echo Searching for .DS_Store files...

for /r %%i in (.DS_Store) do (
    if exist "%%i" (
        echo Found: %%i
        del /f "%%i"
        echo Deleted: %%i
    )
)

echo.
echo Search and deletion complete.
@echo on
