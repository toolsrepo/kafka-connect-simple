package com.nathd.kafka.connect.sink;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ElasticSinkConnectorConfigTest {

  public Map<String, String> initialConfig() {
    Map<String, String> baseProps = new HashMap<>();
    baseProps.put("topic", "myTopic");
    baseProps.put("index", "owner");
    return baseProps;
  }

  @Test
  public void doc() {
      ElasticSinkConnectorConfig config = new ElasticSinkConnectorConfig(initialConfig());

      assertEquals("myTopic", config.getTopicConfig());
      assertEquals("owner", config.getIndexConfig());
  }
}
