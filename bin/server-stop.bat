@echo off

rem Looks for the root dir

set SYNAPSE_DIR=..

rem Execute

java -DSYNAPSE_DIR=%SYNAPSE_DIR% -cp %SYNAPSE_dir%/classes synapse.server.ui.ServerStopper