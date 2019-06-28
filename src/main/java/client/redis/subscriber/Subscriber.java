package client.redis.subscriber;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class Subscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        log.info("Subscriber:onMessage | Channel: {} | Recieved Message : {}", channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("Subscriber:onSubscribe | Subscribed To | Channel: {} | Subscribed Channels: {}", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        log.info("Subscriber:onSubscribe | UnSubscribed To | Channel: {} | UnSubscribed Channels: {}", channel, subscribedChannels);
    }
}
