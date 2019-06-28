package client.redis.utils;

import client.redis.factory.JedisFactory;
import client.redis.publisher.Publisher;
import client.redis.store.RedisStore;
import client.redis.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static org.junit.Assert.*;

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
                        .getRedisConncetion()
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