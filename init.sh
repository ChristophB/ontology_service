#!/bin/bash

set -e

if [ "$ROOT_PATH" ]; then
    sed -i "s|rootPath: /ontology-service|rootPath: $ROOT_PATH|g" config.yml
fi

if [ "$WEBPROTEGE" ]; then
    sed -i "s|webprotegeRelativeToWebroot: /webprotege|webprotegeRelativeToWebroot: $WEBPROTEGE|g" config.yml
fi

java -jar service.jar server config.yml