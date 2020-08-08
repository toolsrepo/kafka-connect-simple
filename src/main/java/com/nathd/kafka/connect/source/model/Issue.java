package com.nathd.kafka.connect.source.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Issue {
    private String url;
    private String repositoryUrl;
    private String commentsUrl;
    private String eventsUrl;
    private Long id;
    private String nodeId;
    private Integer number;
    private String title;
    private User user;
    private List<Label> labels;
    private String state;
    private Boolean locked;
    private User assignee;
    private List<User> assignees;
    private Milestone milestone;
    private Integer comments;
    private PullRequest pullRequest;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant closedAt;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
