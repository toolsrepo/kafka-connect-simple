package com.nathd.kafka.connect.source.model;

import com.nathd.kafka.connect.util.BaseTest;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserTest extends BaseTest {

    @Test
    public void useTest() throws IOException {
        User user = loadJson("data/user.json", User.class);
        assertThat(user.getId(), is(35495686));
    }
}
