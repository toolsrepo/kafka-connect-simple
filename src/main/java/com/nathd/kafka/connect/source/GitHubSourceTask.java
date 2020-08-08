package com.nathd.kafka.connect.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nathd.kafka.connect.VersionUtil;
import com.nathd.kafka.connect.source.api.GitHubConnectorApi;
import com.nathd.kafka.connect.source.model.Issue;
import com.nathd.kafka.connect.source.model.PullRequest;
import com.nathd.kafka.connect.source.model.User;
import com.nathd.kafka.connect.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.time.Instant;
import java.util.*;

import static com.nathd.kafka.connect.source.GitHubSchemas.*;

@Slf4j
public class GitHubSourceTask extends SourceTask {

    protected Instant nextQuerySince;
    protected Integer lastIssueNumber;
    protected Integer nextPageToVisit = 1;
    protected Instant lastUpdatedAt;

    GitHubSourceConnectorConfig config;
    GitHubConnectorApi gitHubConnectorApi;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> configParams) {
        config = new GitHubSourceConnectorConfig(configParams);
        initializeLastVariables();
        gitHubConnectorApi = new GitHubConnectorApi(config);
    }

    private void initializeLastVariables() {
        Map<String, Object> lastSourceOffset = null;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());
        if (lastSourceOffset == null) {
            // we haven't fetched anything yet, so we initialize to 7 days ago
            nextQuerySince = config.getSince();
            lastIssueNumber = -1;
        } else {
            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            Object issueNumber = lastSourceOffset.get(NUMBER_FIELD);
            Object nextPage = lastSourceOffset.get(NEXT_PAGE_FIELD);
            if (updatedAt != null && (updatedAt instanceof String)) {
                nextQuerySince = Instant.parse((String) updatedAt);
            }
            if (issueNumber != null && (issueNumber instanceof String)) {
                lastIssueNumber = Integer.valueOf((String) issueNumber);
            }
            if (nextPage != null && (nextPage instanceof String)) {
                nextPageToVisit = Integer.valueOf((String) nextPage);
            }
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        gitHubConnectorApi.sleepIfNeed();
        List<SourceRecord> sourceRecords = new LinkedList<>();
        try {
            List<Issue> nextIssues = gitHubConnectorApi.getNextIssues(nextPageToVisit, nextQuerySince);
            nextIssues.forEach(issue -> {
                log.info("Received Issue={}", issue);
                sourceRecords.add(createSourceRecord(issue));
            });
            if (nextIssues.size() == 100) {
                nextPageToVisit += 1;
            } else {
                nextQuerySince = lastUpdatedAt.plusSeconds(1);
                nextPageToVisit = 1;
                gitHubConnectorApi.sleep();
            }
        } catch (JsonProcessingException e) {
            log.error("Error while getting Next Issues:" + e.getMessage(), e);
        }
        return sourceRecords;
    }

    private SourceRecord createSourceRecord(Issue issue) {
        return new SourceRecord(
                sourcePartition(), sourceOffset(issue.getUpdatedAt()),
                config.getTopicConfig(), null,
                KEY_SCHEMA,
                createRecordKey(issue),
                VALUE_SCHEMA,
                createRecordValue(issue),
                issue.getUpdatedAt().toEpochMilli()
        );
    }

    private Struct createRecordKey(Issue issue) {
        return new Struct(KEY_SCHEMA)
                .put(OWNER_FIELD, config.getOwnerConfig())
                .put(REPOSITORY_FIELD, config.getRepoConfig())
                .put(NUMBER_FIELD, issue.getNumber());
    }

    private Struct createRecordValue(Issue issue) {
        // Issue top level fields
        Struct valueStruct = new Struct(VALUE_SCHEMA)
                .put(URL_FIELD, issue.getUrl())
                .put(TITLE_FIELD, issue.getTitle())
                .put(CREATED_AT_FIELD, Date.from(issue.getCreatedAt()))
                .put(UPDATED_AT_FIELD, Date.from(issue.getUpdatedAt()))
                .put(NUMBER_FIELD, issue.getNumber())
                .put(STATE_FIELD, issue.getState());

        // User is mandatory
        User user = issue.getUser();
        Struct userStruct = new Struct(USER_SCHEMA)
                .put(USER_URL_FIELD, user.getUrl())
                .put(USER_ID_FIELD, user.getId())
                .put(USER_LOGIN_FIELD, user.getLogin());
        valueStruct.put(USER_FIELD, userStruct);

        // Pull request is optional
        PullRequest pullRequest = issue.getPullRequest();
        if (pullRequest != null) {
            Struct prStruct = new Struct(PR_SCHEMA)
                    .put(PR_URL_FIELD, pullRequest.getUrl())
                    .put(PR_HTML_URL_FIELD, pullRequest.getHtmlUrl());
            valueStruct.put(PR_FIELD, prStruct);
        }

        return valueStruct;

    }

    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(OWNER_FIELD, config.getOwnerConfig());
        map.put(REPOSITORY_FIELD, config.getRepoConfig());
        return map;
    }

    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, DateUtils.MaxInstant(updatedAt, nextQuerySince).toString());
        map.put(NEXT_PAGE_FIELD, nextPageToVisit.toString());
        return map;
    }

    @Override
    public void stop() {
        //TODO: Do whatever is required to stop your task.
    }

}