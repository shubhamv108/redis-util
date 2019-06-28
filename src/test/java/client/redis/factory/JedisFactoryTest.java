package client.redis.factory;

import org.junit.Test;

import static org.junit.Assert.*;

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