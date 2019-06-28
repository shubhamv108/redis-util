package client.redis.store.impl;

import client.redis.exception.RedisCacheException;
import client.redis.exception.enums.RedisCacheErrorCodes;
import client.redis.factory.JedisFactory;
import client.redis.store.RedisStore;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Implementation class for Redis Store Util
 *
 * @see RedisStore
 * @see client.redis.factory.JedisFactory
 */
@Slf4j
public class RedisStoreImpl implements RedisStore {

    private JedisFactory jedisFactory;

    public RedisStoreImpl() {
        this(null);
    }

    public RedisStoreImpl(String dbName) {
        if (dbName == null || dbName.isEmpty())
            this.jedisFactory = JedisFactory.get();
        else
            this.jedisFactory = JedisFactory.get(dbName);
    }


    @Override
    public void save (byte[] key, byte[] value) {

        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("RedisStore:save | Error Cache Save | Key: {} | Value: {}", key, value, e);
            throw new RedisCacheException(RedisCacheErrorCodes.SAVE, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }

    @Override
    public void save (String key, String value) {

        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("RedisStore:save | Error Cache Save | Key: {} | Value: {}", key, value, e);
            throw new RedisCacheException(RedisCacheErrorCodes.SAVE, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }


    @Override
    public String get (String key) {

        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            String value = jedis.get(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("RedisStore:save | Error Cache Get | Key: {} | Value: {}", key, e);
            throw new RedisCacheException(RedisCacheErrorCodes.GET, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }


    public void delete (String... keys) {
        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            jedis.del(keys);
        } catch (Exception e) {
            log.error("RedisStore:delete | Error Deleting Pair | Keys: {}", keys.toString(), e);
            throw new RedisCacheException(RedisCacheErrorCodes.DELETE, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }


    @Override
    public Double incVal (String key) {

        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            double inc = 1.0;
            double value = jedis.incrByFloat(key, inc);
            return value;
        } catch (Exception e) {
            throw new RedisCacheException(e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }

    @Override
    public void saveList (String key, List<Object> values) {
        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            jedis.del(key);
            for (Object val : values)
                jedis.sadd(key, (String) val);
        } catch (Exception e) {
            log.error("RedisStore:delete | Error Saving List | Key: {}", key, e);
            throw new RedisCacheException(e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }

    @Override
    public List<Object> getList (String key) {
        List<Object> list = null;
        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            Set<String> set = jedis.smembers(key);
            list = new ArrayList<>(set);
            return list;
        } catch (Exception e) {
            log.error("RedisStore:getList | Error getting List | Key: {}", key, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return list;
    }

    @Override
    public Long waitReplicas (int replicas, long timeout) {
        Long replicasReached = 0L;
        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            replicasReached = jedis.waitReplicas(replicas, timeout);
        } catch (Exception e) {
            log.error("RedisStore:waitReplicas | Error waiting replicas | Replicas: {} | Timeout: {}",
                    replicas, timeout, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return replicasReached;
    }

    @Override
    public Object execute (Function<Object[], Object> f, Object[] args) {
        Object result = null;
        Jedis jedis = jedisFactory.getRedisConncetion();
        try {
            result = f.apply(args);
        } catch (Exception e) {
            log.error("RedisStore:execute | Error executing redis operation", e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return result;
    }
}
