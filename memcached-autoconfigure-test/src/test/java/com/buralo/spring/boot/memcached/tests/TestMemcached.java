package com.buralo.spring.boot.memcached.tests;

import net.spy.memcached.MemcachedClientIF;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMemcached {

    private String key;

    private String value;

    @Autowired
    private MemcachedClientIF memcachedClient;

    @Before
    public void setup() {
        key = UUID.randomUUID().toString();
        value = UUID.randomUUID().toString();
    }

    @Test
    public void verifyItemNotFound() {
        Object item = memcachedClient.get(key);
        assertNull(item);
    }

    @Test
    public void verifyItemFound() {
        memcachedClient.set(key, 1, value);
        assertEquals(value, memcachedClient.get(key));
    }

    @Test
    public void verifyItemGetsExpired() throws Exception {
        memcachedClient.set(key, 1, value);
        Thread.sleep(2000);
        assertNull(memcachedClient.get(key));
    }
}
