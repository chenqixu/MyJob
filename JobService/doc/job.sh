#!/bin/sh
if [ $# -ne 2 ]; then
    echo "[ERROR] there is no enough args, you need input param,job_id.";
    exit -1;
fi
#param
v_param=$1
v_job_id=$2
########################
CURRENT_DATE=`date +%Y%m%d`
APP_HOME=`pwd`
APP_MAINCLASS=com.cqx.myjob.jobworker.JobWorker
CLASSPATH=$APP_HOME
for i in "$APP_HOME"/lib/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done

java -classpath $CLASSPATH -Dcurrent_date=$CURRENT_DATE -Djob_id=$v_job_id $APP_MAINCLASS $v_param