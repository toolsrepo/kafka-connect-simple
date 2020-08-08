package com.nathd.kafka.connect.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathd.kafka.connect.source.api.GitHubConnectorApi;
import com.nathd.kafka.connect.source.model.Issue;
import com.nathd.kafka.connect.util.JacksonConfiguration;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nathd.kafka.connect.source.GitHubSourceConnectorConfig.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GitHubSourceTaskTest {

  private Integer batchSize = 10;
  private GitHubSourceTask sourceTask = new GitHubSourceTask();
  private ObjectMapper objectMapper = JacksonConfiguration.objectMapper();

  private Map<String, String> initialConfig() {
    Map<String, String> baseProps = new HashMap<>();
    baseProps.put(OWNER_CONFIG, "apache");
    baseProps.put(REPO_CONFIG, "kafka");
    baseProps.put(SINCE_CONFIG, "2017-04-26T01:23:44Z");
    baseProps.put(BATCH_SIZE_CONFIG, batchSize.toString());
    baseProps.put(TOPIC_CONFIG, "github-issues");
    return baseProps;
  }

  @Test
  public void testTask() throws JsonProcessingException {
    Integer pageToVisit = 1;
    Instant nextQuerySince = Instant.parse("2018-01-01T00:00:00Z");
    sourceTask.config = new GitHubSourceConnectorConfig(initialConfig());
    sourceTask.nextPageToVisit = pageToVisit;
    sourceTask.nextQuerySince = nextQuerySince;
    sourceTask.gitHubConnectorApi = new GitHubConnectorApi(sourceTask.config);

    Response response = sourceTask.gitHubConnectorApi.getNextIssuesAPI(pageToVisit, nextQuerySince);

    assertThat(response.getStatus(), is(200));
    MultivaluedMap<String, Object> headers = response.getHeaders();
    assertTrue(headers.containsKey("ETag"));
    assertTrue(headers.containsKey("X-RateLimit-Limit"));
    assertTrue(headers.containsKey("X-RateLimit-Remaining"));
    assertTrue(headers.containsKey("X-RateLimit-Reset"));

    List<Issue> issues = objectMapper.readValue(response.readEntity(String.class), new TypeReference<List<Issue>>() {
    });

    assertEquals(batchSize.intValue(), issues.size());
    assertEquals(4307, issues.get(0).getNumber().intValue());

  }


  @Test
  public void test() {
    // Congrats on a passing test!
  }
}