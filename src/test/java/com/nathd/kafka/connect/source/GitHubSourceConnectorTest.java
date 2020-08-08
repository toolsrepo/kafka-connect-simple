package com.nathd.kafka.connect.source;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.nathd.kafka.connect.source.GitHubSourceConnectorConfig.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GitHubSourceConnectorTest {

  private Map<String, String> initialConfig() {
    Map<String, String> baseConfigs = new HashMap<>();
    baseConfigs.put(OWNER_CONFIG, "foo");
    baseConfigs.put(REPO_CONFIG, "bar");
    baseConfigs.put(TOPIC_CONFIG, "github-issues");
    baseConfigs.put(SINCE_CONFIG, "2018-07-21T01:22:25Z");
    baseConfigs.put(BATCH_SIZE_CONFIG, "100");
    return baseConfigs;
  }

  @Test
  public void taskNumbers() {
    GitHubSourceConnector gitHubSourceConnector = new GitHubSourceConnector();
    gitHubSourceConnector.start(initialConfig());

    assertEquals(gitHubSourceConnector.taskConfigs(1).size(), 1);
    assertEquals(gitHubSourceConnector.taskConfigs(5).size(), 1);
  }
}
