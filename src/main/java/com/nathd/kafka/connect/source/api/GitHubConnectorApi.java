package com.nathd.kafka.connect.source.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.nathd.kafka.connect.source.GitHubSourceConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.errors.ConnectException;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class GitHubConnectorApi {

    // for efficient http requests
    private Integer XRateLimit = 9999;
    private Integer XRateRemaining = 9999;
    private long XRateReset = Instant.MAX.getEpochSecond();

    private GitHubSourceConnectorConfig config;
    private RestClient restClient;

    public GitHubConnectorApi(GitHubSourceConnectorConfig config) {
        this.config = config;
        this.restClient = new RestClient(config.getAuthUsernameConfig(), config.getAuthPasswordConfig());
    }

    public JsonNode getNextIssues(Integer page, Instant since) throws InterruptedException {
        Response jsonResponse;

        jsonResponse = getNextIssuesAPI(page, since);
        MultivaluedMap<String, Object> headers = jsonResponse.getHeaders();

        XRateLimit = Integer.parseInt(headers.getFirst("X-RateLimit-Limit").toString());
        XRateRemaining = Integer.parseInt(headers.getFirst("X-RateLimit-Remaining").toString());
        XRateReset = Long.parseLong(headers.getFirst("X-RateLimit-Reset").toString());

        switch (jsonResponse.getStatus()) {
            case 200:
                return jsonResponse.readEntity(JsonNode.class);
            case 403:
                // we have issues too many requests.
                log.info(jsonResponse.readEntity(JsonNode.class).get("message").asText());
                log.info(String.format("Your rate limit is %s", XRateLimit));
                log.info(String.format("Your remaining calls is %s", XRateRemaining));
                log.info(String.format("The limit will reset at %s",
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(XRateReset), ZoneOffset.systemDefault())));
                long sleepTime = XRateReset - Instant.now().getEpochSecond();
                log.info(String.format("Sleeping for %s seconds", sleepTime ));
                Thread.sleep(1000 * sleepTime);
                return getNextIssues(page, since);
            case 401:
                throw new ConnectException("Bad GitHub credentials provided, please edit your config");
            default:
                log.error(constructUrl(page, since));
                log.error(String.valueOf(jsonResponse.getStatus()));
                log.error(jsonResponse.readEntity(JsonNode.class).asText());
                log.error(jsonResponse.getHeaders().toString());
                log.error("Unknown error: Sleeping 5 seconds " +
                        "before re-trying");
                Thread.sleep(5000L);
                return getNextIssues(page, since);
        }
    }

    protected Response getNextIssuesAPI(Integer page, Instant since) {
        return restClient.getData(constructUrl(page, since));
    }

    protected String constructUrl(Integer page, Instant since){
        return String.format(
                "https://api.github.com/repos/%s/%s/issues?page=%s&per_page=%d&since=%s&state=all&direction=asc&sort=updated",
                config.getOwnerConfig(),
                config.getRepoConfig(),
                page,
                config.getBatchSizeConfig(),
                since.toString());
    }

    public void sleep() throws InterruptedException {
        long sleepTime = (long) Math.ceil(
                (double) (XRateReset - Instant.now().getEpochSecond()) / XRateRemaining);
        log.debug(String.format("Sleeping for %s seconds", sleepTime ));
        Thread.sleep(1000 * sleepTime);
    }

    public void sleepIfNeed() throws InterruptedException {
        // Sleep if needed
        if (XRateRemaining <= 10 && XRateRemaining > 0) {
            log.info(String.format("Approaching limit soon, you have %s requests left", XRateRemaining));
            sleep();
        }
    }
}
