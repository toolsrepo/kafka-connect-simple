package com.nathd.kafka.connect.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

@RunWith(JUnit4ClassRunner.class)
public class BaseTest {

    private static final ObjectMapper objectMapper = JacksonConfiguration.objectMapper();

    public <T> T loadJson(String jsonFilePath, Class<T> type) throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);
        return objectMapper.readValue(stream, type);
    }
}
