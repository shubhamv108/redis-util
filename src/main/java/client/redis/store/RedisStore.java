package client.redis.store;

import client.redis.store.impl.JedisStore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Redis Store
 *
 * @see JedisStore
 */
public interface RedisStore {

    void loadLuaScripts(String luaScriptHashKey, String luaScript);

    /**
     * @param key
     * @param value
     */
    void save (byte[] key, byte[] value);

    /**
     * @param key
     * @param value
     */
    void save (String key, String value);

    /**
     * @param key
     * @return
     */
    String get (String key);

    /**
     * @param keys
     */
    void delete (String... keys);

    /**
     * @param key
     * @return
     */
    Double incVal (String key);

    /**
     * @param key
     * @param values
     */
    void saveList (String key, List<Object> values);


    /**
     * @param key
     * @return
     */
    List<Object> getList (String key);

    /**
     * @param replicas
     * @param timeout
     * @return
     */
    Long waitReplicas (int replicas, long timeout);

    /**
     * @param f
     * @param args
     * @return
     */
    Object execute (BiFunction<Object[], Jedis, Object> f, Object[] args);

    void loadLuaScript(String luaScriptHashKey, String luaScript);

    String loadLuaScript(String luaScript);

    Object executeLuaScript(String luaScriptHash, String luaScript);

    Object executeLuaScript(String luaScriptHash,
                            List<String> keys,
                            List<String> args,
                            String luaScript);

    Object executeLuaScriptForHashKey(
            String luaScriptHashKey,
            List<String> keys,
            List<String> args,
            String luaScript);

    Object executeTransaction(List<Function<Transaction, Object>> operations);

    String lockWithoutVersionIfNotExistsInSingleRedisInstance(String resourceName, String ownerName, Long timeToLiveInMilliseconds);
    Long releaseLockInSingleRedisInstance(String resourceName, String ownerName);

    Long redLock(String resourceName, String ownerName);
}
