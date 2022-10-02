package client.redis.utils;

import client.redis.factory.JedisFactory;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPubSub;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisUtilsTest {

    @Test
    public void publish() {

        String channelName = "Test_Channel";
        String publishedMessage = "TestPublisherMessage";

        JedisPubSub subscriber = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                assertEquals(publishedMessage, message);
            }
        };

        new Thread(() ->
                JedisFactory.get()
                        .getRedisConnection()
                        .subscribe(subscriber, channelName))
                .start();

        RedisUtils.publish(channelName, publishedMessage);

        subscriber.unsubscribe();
    }

    @Test
    public void publish1() {
    }

    @Test
    public void publish2() {
    }
}