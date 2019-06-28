package client.redis.publisher;

import lombok.Data;
import redis.clients.jedis.Jedis;
import client.redis.utils.RedisUtils;

@Data
public class Publisher {

    private Jedis jedis;
    private String channel;

    public Publisher (Jedis jedis, String channel) {
        this.jedis = jedis;
        this.channel = channel;
    }

    public Long publish (String channel, String message) {
        return RedisUtils.publish(jedis, channel, message);
    }

}
