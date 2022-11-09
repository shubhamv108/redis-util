package client.redis.store;

import client.redis.store.impl.JedisStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RedisStoreTest {

    private String key = "Test Key";
    private String val = "Test Value";
    private RedisStore redisStore = null;

    @BeforeEach
    public void initTest() {
        redisStore = new JedisStore();
    }

    @Test
    public void save() {
        redisStore.save(key, val);
        assertEquals(val, redisStore.get(key));
    }

    @Test
    public void get() {
        redisStore.save(key, val);
        String result = redisStore.get(key);
        assertEquals(val, result);
    }

    @Test
    public void delete() {
        redisStore.save(key, val);
        redisStore.delete(key);
        assertNull(redisStore.get(key));
    }

    @Test
    public void incVal() {
    }

    @Test
    public void saveList() {
    }

    @Test
    public void getList() {
    }

    @Test
    public void waitReplicas() {
    }

    @Test
    public void execute() {
    }
}