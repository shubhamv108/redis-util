package client.redis.store;

import java.util.List;
import java.util.function.Function;

/**
 * Redis Store
 *
 * @see client.redis.store.impl.RedisStoreImpl
 */
public interface RedisStore {

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
    Object execute (Function<Object[], Object> f, Object[] args);

}
