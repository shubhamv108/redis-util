package client.redis.factory;

import client.redis.config.Env;
import client.redis.exception.RedisCacheException;
import client.redis.exception.enums.RedisCacheErrorCodes;
import com.typesafe.config.ConfigValue;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Redis Connection Factory
 *
 * @see JedisPool
 */
@Slf4j
public class JedisFactory {

    private static Map<String, JedisFactory> dbs = new HashMap<>();

    private JedisPool jedisPool;

    private int maxConnections = 128;
    private String host = "localhost";
    private int port = 6379;
    private int index = 0;

    static {
        initializeDBConnections();
    }

    private static void initializeDBConnections() {
        if (Env.config.hasPath("redis.dbCons")) {
            Env.config.getList("redis.dbCons")
                    .stream()
                    .map(ConfigValue::unwrapped)
                    .map(String::valueOf)
                    .forEach(JedisFactory::initializeDBConnection);
        }
        if (Env.config.hasPath("redis.default.host"))
            initializeDBConnection("default");
    }

    private static void initializeDBConnection(final String s) {
        String host = null;
        int port = -1, maxConnections = -1, index = -1;
        String dbName = "redis." + s + ".";
        if (Env.config.hasPath(dbName + "host")) host = Env.config.getString(dbName + "host");
        if (Env.config.hasPath(dbName + "port")) port = Env.config.getInt(dbName + "port");
        if (Env.config.hasPath(dbName + "maxConnections")) maxConnections = Env.config.getInt(dbName + "maxConnections");
        if (Env.config.hasPath(dbName + "dbIndex")) index = Env.config.getInt(dbName + "dbIndex");

        if (validateFactoryInputs(dbName, maxConnections, host, port, index))
            dbs.put(s, new JedisFactory(maxConnections, host, port, index));
    }

    public static JedisFactory get() {
        return get("default");
    }

    public static JedisFactory get(final String dbName) {
        JedisFactory factory = dbs.get(dbName);
        if (Objects.isNull(factory))
            initializeDBConnection(dbName);
        return dbs.get(dbName);
    }

    private JedisFactory(final int maxConnections, final String host, final int port, final int index) {
        this.maxConnections = maxConnections;
        this.host = host;
        this.port = port;
        this.index = index;
        initializePool();
    }

    private void initializePool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setBlockWhenExhausted(true);
        jedisPool = new JedisPool(config, host, port);
    }

    /**
     * Validator for Redis Config
     *
     * @param dbName
     * @param maxConnections
     * @param host
     * @param port
     * @param index
     * @return
     */
    private static boolean validateFactoryInputs(final String dbName,
                                                 final int maxConnections,
                                                 final String host,
                                                 final int port,
                                                 final int index) {
        boolean isValid = true;
        if (host == null || host.isEmpty()) {
            log.debug("Invalid Host for Redis" + " | DB name: " + dbName);
            isValid = false;
        }
        if (maxConnections == -1) {
            log.debug("Invalid maxConnections for Redis" + " | DB name: " + dbName);
            isValid = false;
        }

        if (port == -1) {
            log.debug("Invalid port for Redis" + " | DB name: " + dbName);
            isValid = false;
        }
        if (index == -1) {
            log.debug("Invalid index for Redis" + " | DB name: " + dbName);
            isValid = false;
        }
        return isValid;
    }

    /**
     * @return Jedis
 *              the connection from pool
     */
    public Jedis getRedisConnection() {
        try {
            Jedis jedis = jedisPool.getResource();
            if (index > 0)
                jedis.select(index);
            return jedis;
        } catch (Exception e) {
            throw new RedisCacheException(RedisCacheErrorCodes.CONNECTION, e);
        }
    }

    /**
     * Return connection to pool
     *
     * @param jedis
     */
    public void returnConnection(Jedis jedis) {
        try {
            if (null != jedis && jedis.isConnected())
                jedis.close();
        } catch (Exception e) {
            throw new RedisCacheException(RedisCacheErrorCodes.CONNECTION, e);
        }
    }

}
