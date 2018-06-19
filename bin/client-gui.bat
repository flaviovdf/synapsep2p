@echo off

rem Looks for the root dir

set SYNAPSE_DIR=..

rem Execute

java -DSYNAPSE_DIR=%SYNAPSE_DIR% -cp %SYNAPSE_DIR%/classes;%SYNAPSE_DIR%/lib/yal.jar;%SYNAPSE_DIR%/lib/fast-md5.jar synapse.client.ui.gui.RunGUI