#!/usr/bin/env bash
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
docker build . -t simplesteph/kafka-connect-source-github:1.0
docker run --net=host --rm -t \
       -v $(pwd)/offsets:/kafka-connect-source-github/offsets \
       simplesteph/kafka-connect-source-github:1.0