package client.redis.exception.enums;

public interface RedisCacheErrorCodes extends CacheErrorCodes {

    String CACHE_PLATFORM = "REDIS";

    String CONNECTION = CACHE_PLATFORM + SPACE + CacheErrorCodes.CONNECTION;
    String SAVE       = CACHE_PLATFORM + SPACE + CacheErrorCodes.SAVE;
    String GET        = CACHE_PLATFORM + SPACE + CacheErrorCodes.GET;
    String DELETE     = CACHE_PLATFORM + SPACE + CacheErrorCodes.DELETE;

}
