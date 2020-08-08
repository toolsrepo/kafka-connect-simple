package com.nathd.kafka.connect.source.api;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
    private Client client = null;

    public RestClient(String userName, String password) {
        this.client = ClientBuilder.newClient()
                .register(HttpAuthenticationFeature.basic(userName, password));
    }

    public Response getData(String url) {
        return this.client.target(url).request(MediaType.APPLICATION_JSON).get();
    }
}
