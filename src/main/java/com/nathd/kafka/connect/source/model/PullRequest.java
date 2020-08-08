package com.nathd.kafka.connect.source.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PullRequest {
    private String url;
    private String htmlUrl;
    private String diffUrl;
    private String patchUrl;
}
