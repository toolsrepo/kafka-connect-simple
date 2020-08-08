package com.nathd.kafka.connect.source;

import java.util.List;
import java.util.Map;

import com.nathd.kafka.connect.VersionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import static java.util.Collections.singletonList;

@Slf4j
public class GitHubSourceConnector extends SourceConnector {
  private GitHubSourceConnectorConfig config;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) { config = new GitHubSourceConnectorConfig(map); }

  @Override
  public Class<? extends Task> taskClass() { return GitHubSourceTask.class; }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    return singletonList(config.originalsStrings());
  }

  @Override
  public void stop() {
    //TODO: Do things that are necessary to stop your connector.
  }

  @Override
  public ConfigDef config() {
    return GitHubSourceConnectorConfig.conf();
  }
}
