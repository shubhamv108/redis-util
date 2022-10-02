package client.redis.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JedisFactoryTest {

    @Test
    public void get() {
        assertNotNull(JedisFactory.get());
    }

    @Test
    public void get1() {
        String dbName = "default";
        assertNotNull(JedisFactory.get(dbName));
    }
}