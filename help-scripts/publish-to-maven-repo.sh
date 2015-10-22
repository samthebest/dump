#!/bin/bash

set -e

source ./scripts/utils.sh

version=`get-version`

maven_creds=TODO

user=`get-value-from-file "user=" $maven_creds`
password=`get-value-from-file "password=" $maven_creds`

echo "INFO: Publishing to maven repo!"
curl -v \
    -F "r=releases" \
    -F "g=com.mendeley.altmetrics" \
    -F "a=altmetrics" \
    -F "v=${version}" \
    -F "p=jar" \
    -F "file=@./target/scala-2.10/altmetrics-assembly-${version}.jar" \
    -u "${user}:${password}" \
    http://your-domain:80/nexus/content/repositories/releases/ 2>&1 | tee curl-publish.log

unauthorized="401 Unauthorized"
status=`cat curl-publish.log | grep -o "${unauthorized}"`

if [ "${status}" = "${unauthorized}" ]; then
	echo "ERROR: not authorized to publish to maven"
	exit 1
fi

echo "INFO: Probably published to maven OK"

rm curl-publish.log
