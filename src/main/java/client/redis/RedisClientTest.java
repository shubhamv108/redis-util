package client.redis;

import client.redis.store.RedisStore;
import client.redis.store.impl.JedisStore;
import redis.clients.jedis.Transaction;

import java.util.Arrays;

public class RedisClientTest {

    public static void main(String[] args) {
        RedisStore redisStore = new JedisStore();
        redisStore.save("key", "val");
        if ("val".equals(redisStore.get("key")))
            System.out.println("SUCCESS");
        else
            System.out.println("FAILURE");

        String luaScript = "return redis.call('SET', 'key2', 'val')";
        String scriptHash = redisStore.loadLuaScript(luaScript);
        Object response = redisStore.executeLuaScript(scriptHash, luaScript);
        System.out.println(response);
        System.out.println(redisStore.get("key2"));


        redisStore.executeTransaction(Arrays.asList(
                (Transaction transaction) -> transaction.set("uuid1", "{\"name\":\"shubham\"}"),
                (Transaction transaction) -> transaction.set("name:shubham", "uuid1")
        ));
        System.out.println(redisStore.get(redisStore.get("name:shubham")));

        redisStore.delete("testResource");

        String lockAcquiredStatus = redisStore.lockWithoutVersionIfNotExistsInSingleRedisInstance("testResource", "shubham:RandomString123213", 1000000000l);
        if ("OK".equals(lockAcquiredStatus))
            System.out.println("Lock Acquired");
        else
            System.out.println("Lock Could not be acquired");
        System.out.println(redisStore.get("testResource"));
        lockAcquiredStatus = redisStore.lockWithoutVersionIfNotExistsInSingleRedisInstance("testResource", "shubham:RandomString123213", 1000000000l);
        if ("OK".equals(lockAcquiredStatus))
            System.out.println("Lock Acquired");
        else
            System.out.println("Lock Could not be acquired");

        Long lockReleasedCount = redisStore.releaseLockInSingleRedisInstance("testResource", "shubham");
        if (lockReleasedCount == 1)
            System.out.println("Lock released");
        else
            System.out.println("Lock could not be released");
        lockReleasedCount = redisStore.releaseLockInSingleRedisInstance("testResource", "shubham:RandomString123213");
        if (lockReleasedCount == 1)
            System.out.println("Lock released");

    }

}
