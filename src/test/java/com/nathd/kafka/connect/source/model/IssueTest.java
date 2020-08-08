package com.nathd.kafka.connect.source.model;

import com.nathd.kafka.connect.util.BaseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class IssueTest extends BaseTest {

    @Test
    public void testIssue() throws IOException {
        Issue issue = loadJson("data/issue.json", Issue.class);
        assertNotNull(issue.getAssignee());
        assertNotNull(issue.getMilestone());
        assertNotNull(issue.getMilestone().getCreator());
        assertNotNull(issue.getLabels());
        assertNotNull(issue.getPullRequest());
    }
}
