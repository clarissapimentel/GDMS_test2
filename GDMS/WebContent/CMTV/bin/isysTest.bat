@echo off

set ISYS_HOME=..

set CP=%ISYS_HOME%\lib\Brazil-for-isys.jar;%ISYS_HOME%\lib\jhbasic.jar;%ISYS_HOME%\lib\commons-discovery.jar;%ISYS_HOME%\lib\commons-logging.jar;%ISYS_HOME%\lib\isys.jar

java -mx256m -classpath %CP% org.ncgr.isys.ice.IceMain %*
