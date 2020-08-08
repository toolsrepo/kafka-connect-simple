package com.nathd.kafka.connect.source;

import com.nathd.kafka.connect.validators.BatchSizeValidator;
import com.nathd.kafka.connect.validators.TimestampValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.Importance.HIGH;
import static org.apache.kafka.common.config.ConfigDef.Importance.LOW;
import static org.apache.kafka.common.config.ConfigDef.Type.INT;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;


public class GitHubSourceConnectorConfig extends AbstractConfig {

  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_DOC = "Topic to write to";

  public static final String REPO_CONFIG = "github.repo";
  private static final String REPO_DOC = "Repository you'd like to follow";

  public static final String OWNER_CONFIG = "github.owner";
  private static final String OWNER_DOC = "Owner of the repository you'd like to follow";

  public static final String SINCE_CONFIG = "since.timestamp";
  private static final String SINCE_DOC =
          "Only issues updated at or after this time are returned.\n"
                  + "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.\n"
                  + "Defaults to a year from first launch.";

  public static final String BATCH_SIZE_CONFIG = "batch.size";
  private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

  public static final String AUTH_USERNAME_CONFIG = "auth.username";
  private static final String AUTH_USERNAME_DOC = "Optional Username to authenticate calls";

  private static final String AUTH_PASSWORD_DOC = "Optional Password to authenticate calls";
  public static final String AUTH_PASSWORD_CONFIG = "auth.password";

  public GitHubSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public GitHubSourceConnectorConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
            .define(TOPIC_CONFIG, STRING, HIGH, TOPIC_DOC)
            .define(OWNER_CONFIG, STRING, HIGH, OWNER_DOC)
            .define(REPO_CONFIG, STRING, HIGH, REPO_DOC)
            .define(BATCH_SIZE_CONFIG, INT, 100, new BatchSizeValidator(), LOW, BATCH_SIZE_DOC)
            .define(SINCE_CONFIG, STRING, ZonedDateTime.now().minusYears(1).toInstant().toString(), new TimestampValidator(), HIGH, SINCE_DOC)
            .define(AUTH_USERNAME_CONFIG, STRING, "", HIGH, AUTH_USERNAME_DOC)
            .define(AUTH_PASSWORD_CONFIG, STRING, "", HIGH, AUTH_PASSWORD_DOC);
  }

  public String getOwnerConfig(){
    return this.getString(OWNER_CONFIG);
  }
  public String getRepoConfig() { return this.getString(REPO_CONFIG); }
  public Integer getBatchSizeConfig() { return this.getInt(BATCH_SIZE_CONFIG); }
  public Instant getSince() { return Instant.parse(this.getString(SINCE_CONFIG)); }
  public String getTopicConfig() { return this.getString(TOPIC_CONFIG); }
  public String getAuthUsernameConfig() { return this.getString(AUTH_USERNAME_CONFIG); }
  public String getAuthPasswordConfig() { return this.getString(AUTH_PASSWORD_CONFIG); }

}
