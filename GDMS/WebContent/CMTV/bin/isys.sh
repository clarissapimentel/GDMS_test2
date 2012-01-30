#!/bin/sh
ISYS_HOME=..
CP="${ISYS_HOME}/lib/Brazil-for-isys.jar:\
${ISYS_HOME}/lib/jhbasic.jar:\
${ISYS_HOME}/lib/commons-discovery.jar:\
${ISYS_HOME}/lib/commons-logging.jar:\
${ISYS_HOME}/lib/jena.jar:\
${ISYS_HOME}/lib/xercesImpl.jar:\
${ISYS_HOME}/lib/icu4j.jar:\
${ISYS_HOME}/lib/arq.jar:\
${ISYS_HOME}/lib/concurrent.jar:\
${ISYS_HOME}/lib/isys.jar"

java -mx256m -classpath $CP org.ncgr.isys.ice.IceMain $*
