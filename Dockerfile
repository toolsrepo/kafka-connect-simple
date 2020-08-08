FROM confluentinc/cp-kafka-connect:3.2.0

WORKDIR /kafka-connect-simple
COPY config config
COPY target target

VOLUME /kafka-connect-simple/config
VOLUME /kafka-connect-simple/offsets

CMD CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')" connect-standalone config/worker.properties config/GitHubSourceConnector.properties