package com.nathd.kafka.connect.source.api;

import com.nathd.kafka.connect.source.GitHubSourceConnectorConfig;
import com.nathd.kafka.connect.source.model.Issue;
import com.nathd.kafka.connect.util.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class GitHubConnectorApiTest extends BaseTest {

    private GitHubSourceConnectorConfig config;
    private GitHubConnectorApi api;

    @Before
    public void setup() {
        this.config = new GitHubSourceConnectorConfig(initialConfig());
        this.api = new GitHubConnectorApi(config);
    }

    @Test
    public void testGetIssuesAPI() throws InterruptedException, IOException {
        List<Issue> node = this.api.getNextIssues(0, ZonedDateTime.now().minusYears(2).toInstant());
        assertTrue(node.size() > 0);
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
