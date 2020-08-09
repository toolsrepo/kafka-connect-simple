package com.nathd.kafka.connect.sink;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;


public class ElasticSinkConnectorConfig extends AbstractConfig {

  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_CONFIG_DOC = "Topic from where data will be taken";

  public static final String INDEX_CONFIG = "index";
  private static final String INDEX_CONFIG_DOC = "Index of Elastic search";

  public static final String CONNECTION_URL_CONFIG = "connectionUrl";
  private static final String CONNECTION_URL_CONFIG_DOC = "Connection URL of Elastic search";

  public ElasticSinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public ElasticSinkConnectorConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
        .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_CONFIG_DOC)
        .define(CONNECTION_URL_CONFIG, Type.STRING, Importance.HIGH, CONNECTION_URL_CONFIG_DOC)
        .define(INDEX_CONFIG, Type.STRING, Importance.HIGH, INDEX_CONFIG_DOC);
  }

  public String getTopicConfig(){
    return this.getString(TOPIC_CONFIG);
  }
  public String getIndexConfig(){
    return this.getString(INDEX_CONFIG);
  }

  public String getConnectionUrlConfig(){
    return this.getString(CONNECTION_URL_CONFIG);
  }
}
