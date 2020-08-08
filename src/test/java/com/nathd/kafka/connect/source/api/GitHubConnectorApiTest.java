package com.nathd.kafka.connect.source.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.nathd.kafka.connect.source.GitHubSourceConnectorConfig;
import com.nathd.kafka.connect.util.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class GitHubConnectorApiTest extends BaseTest {

    private GitHubSourceConnectorConfig config;
    private GitHubConnectorApi api;

    @Before
    public void setup() {
        this.config = new GitHubSourceConnectorConfig(initialConfig());
        this.api = new GitHubConnectorApi(config);
    }

    @Test
    public void testGetIssuesAPI() throws InterruptedException {
        JsonNode node = this.api.getNextIssues(0, ZonedDateTime.now().minusYears(2).toInstant());
        assertNotNull(node);
    }

    private Map<String, String> initialConfig() {
        Map<String, String> baseProps = new HashMap<>();
        baseProps.put("github.owner", "kubernetes");
        baseProps.put("github.repo", "kubernetes");
        baseProps.put("batch.size", "100");
        baseProps.put("topic", "topic");
        return baseProps;
    }
}
