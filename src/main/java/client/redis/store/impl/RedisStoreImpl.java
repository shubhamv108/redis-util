package client.redis.store.impl;

import client.redis.exception.RedisCacheException;
import client.redis.exception.enums.RedisCacheErrorCodes;
import client.redis.factory.JedisFactory;
import client.redis.store.RedisStore;
import client.redis.store.scripts.Constants;
import client.redis.store.scripts.LuaScript;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisNoScriptException;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementation class for Redis Store Util
 *
 * @see RedisStore
 * @see client.redis.factory.JedisFactory
 */
@Slf4j
public class RedisStoreImpl implements RedisStore {


    private Map<String, String> luaScriptHashes = new HashMap<>();

    private JedisFactory jedisFactory;

    public RedisStoreImpl() {
        this(null);
    }

    public RedisStoreImpl(String dbName) {
        if (dbName == null || dbName.isEmpty())
            this.jedisFactory = JedisFactory.get();
        else
            this.jedisFactory = JedisFactory.get(dbName);
        this.loadLuaScripts();
    }

    private void loadLuaScripts() {
        this.luaScriptHashes.put(
                Constants.RELEASE_LOCK_WITH_VALUE_TAKEN_IN_SINGLE_INSTANCE_HASH_KEY,
                this.loadLuaScript(LuaScript.RELEASE_LOCK_FOR_VALUE_TAKEN_IN_SINGLE_INSTANCE));
    }


    @Override
    public void save(byte[] key, byte[] value) {
        Jedis jedis = jedisFactory.getRedisConnection();
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
        Jedis jedis = jedisFactory.getRedisConnection();
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

        Jedis jedis = jedisFactory.getRedisConnection();
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
        Jedis jedis = jedisFactory.getRedisConnection();
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

        Jedis jedis = jedisFactory.getRedisConnection();
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
        Jedis jedis = jedisFactory.getRedisConnection();
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
        Jedis jedis = jedisFactory.getRedisConnection();
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
        Jedis jedis = jedisFactory.getRedisConnection();
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
    public Object execute (BiFunction<Object[], Jedis, Object> f, Object[] args) {
        Object result = null;
        Jedis jedis = jedisFactory.getRedisConnection();
        try {
            result = f.apply(args, jedis);
        } catch (Exception e) {
            log.error("RedisStore:execute | Error executing redis operation", e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return result;
    }

    @Override
    public String loadLuaScript(String luaScript) {
        String result = null;
        Jedis jedis = jedisFactory.getRedisConnection();
        try {
            result = jedis.scriptLoad(luaScript);
        } catch (Exception e) {
            log.error("RedisStore:execute | Error executing redis operation", e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return result;
    }

    @Override
    public Object executeLuaScript(String luaScriptHash,
                                   String luaScript) {
        return this.executeLuaScript(luaScriptHash, Collections.emptyList(), Collections.emptyList(), luaScript);
    }
    @Override
    public Object executeLuaScript(String luaScriptHash,
                                   List<String> keys,
                                   List<String> args,
                                   String luaScript) {
        Object result = null;
        Jedis jedis = jedisFactory.getRedisConnection();
        try {
            result = jedis.evalsha(luaScriptHash, keys, args);
        } catch (JedisNoScriptException noScriptException) {
            log.error("Redis:evalSha | No Script for hash: {}", luaScriptHash, noScriptException);
            luaScriptHash = this.loadLuaScript(luaScript);
            return this.executeLuaScript(luaScriptHash, luaScript);
        } catch (Exception e) {
            log.error("Redis:evalSha | Error executing redis operation", e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return result;
    }

    @Override
    public List<Object> executeTransaction(List<Function<Transaction, Object>> operations) {
        List<Object> result = null;
        Jedis jedis = jedisFactory.getRedisConnection();
        try {
            Transaction transaction = jedis.multi();
            operations.forEach(function -> function.apply(transaction));
            result = transaction.exec();
        } catch (Exception e) {
            log.error("Redis:multi/exec | Error executing redis operation", e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
        return result;
    }

    @Override
    public String lockWithoutVersionIfNotExistsInSingleRedisInstance(
            String resourceName, String ownerName, Long timeToLiveInMilliseconds) {
        Jedis jedis = jedisFactory.getRedisConnection();
        try {
            return jedis.set(resourceName, ownerName, SetParams.setParams().nx().px(timeToLiveInMilliseconds));
        } catch (Exception e) {
            log.error("Redis:set | Error Lock set | Key: {} | Value: {}", resourceName, ownerName, e);
            throw new RedisCacheException(RedisCacheErrorCodes.SAVE, e);
        } finally {
            jedisFactory.returnConnection(jedis);
        }
    }

    @Override
    public Long releaseLockInSingleRedisInstance(String resourceName, String ownerName) {
        String luaScriptHash =
                this.luaScriptHashes.get(Constants.RELEASE_LOCK_WITH_VALUE_TAKEN_IN_SINGLE_INSTANCE_HASH_KEY);
        Object response = this.executeLuaScript(
                luaScriptHash,
                Arrays.asList(resourceName),
                Arrays.asList(ownerName),
                LuaScript.RELEASE_LOCK_FOR_VALUE_TAKEN_IN_SINGLE_INSTANCE);
        if (String.valueOf(response) == "0") {
            throw new RuntimeException(String.format("Lock could not be released for {Key:%s, Val:%s}", resourceName, ownerName));
        }
        return (Long) response;
    }

    /**
     * ToDo: use reddisson
     * @param resourceName
     * @param ownerName
     * @return
     */
    @Override
    public Long redLock(String resourceName, String ownerName) {
        return null;
    }


}
