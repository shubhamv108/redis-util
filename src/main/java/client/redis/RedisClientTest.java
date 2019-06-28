package client.redis;

import client.redis.store.RedisStore;
import client.redis.store.impl.RedisStoreImpl;

public class RedisClientTest {

    public static void main(String[] args) {
        RedisStore redisStore = new RedisStoreImpl();
        redisStore.save("key", "val");
        if ("val".equals(redisStore.get("key")))
            System.out.println("SUCCESS");
        else
            System.out.println("FAILURE");

    }

}
