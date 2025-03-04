package client.redis.utils;

import client.redis.factory.JedisFactory;
import redis.clients.jedis.Jedis;

/**
 * Redis Utils class
 */
public class RedisUtils {

    public static Long publish (String channel, String message) {

        return publish (JedisFactory.get().getRedisConnection(), channel, message);
    }

    public static Long publish (Jedis jedis, String channel, String message) {
        return jedis.publish(channel, message);
    }

    public static Long publish (String dbName, String channel, String message) {
        return publish(JedisFactory.get(dbName).getRedisConnection(), channel, message);
    }

}
