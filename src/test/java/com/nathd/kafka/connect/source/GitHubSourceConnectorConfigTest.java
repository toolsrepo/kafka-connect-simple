package com.nathd.kafka.connect.source;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nathd.kafka.connect.source.GitHubSourceConnectorConfig.BATCH_SIZE_CONFIG;
import static com.nathd.kafka.connect.source.GitHubSourceConnectorConfig.SINCE_CONFIG;
import static org.junit.Assert.assertTrue;

@Slf4j
public class GitHubSourceConnectorConfigTest {

  private final ConfigDef configDef = GitHubSourceConnectorConfig.conf();

  private Map<String, String> initialConfig() {
    Map<String, String> baseProps = new HashMap<>();
    baseProps.put("github.owner", "owner");
    baseProps.put("github.repo", "repo");
    baseProps.put("topic", "topic");
    return baseProps;
  }

  @Test
  public void initialConfigIsValid() {
    List<ConfigValue> results = configDef.validate(initialConfig());
    assertSuccess(results);
  }

  @Test
  public void testInvalidDateEntries() {
    Map<String, String> config = initialConfig();
    config.put(SINCE_CONFIG, "a-date");
    assertTrue(configDef.validateAll(config).get(SINCE_CONFIG).errorMessages().size() > 0);
  }

  @Test
  public void testInvalidBatchSize() {
    Map<String, String> config = initialConfig();
    config.put(BATCH_SIZE_CONFIG, "-1");
    assertTrue(configDef.validateAll(config).get(BATCH_SIZE_CONFIG).errorMessages().size() > 0);

    config = initialConfig();
    config.put(BATCH_SIZE_CONFIG, "101");
    assertTrue(configDef.validateAll(config).get(BATCH_SIZE_CONFIG).errorMessages().size() > 0);
  }

  private void assertSuccess(List<ConfigValue> results) {
    String errors = results.stream().flatMap(configValue -> configValue.errorMessages().stream())
            .collect(Collectors.joining(","));
    assertTrue(errors, errors.length() <= 0);
  }
}