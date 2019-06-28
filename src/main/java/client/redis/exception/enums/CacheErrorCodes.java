package client.redis.exception.enums;

public interface CacheErrorCodes {

    String ERR_CACHE = "ERR_CACHE";
    String UNDERSCORE = "_";
    String SPACE      = " ";

    String CONNECTION = ERR_CACHE + UNDERSCORE + StoreOperations.CONNECTION;
    String SAVE       = ERR_CACHE + UNDERSCORE + StoreOperations.SAVE;
    String DELETE     = ERR_CACHE + UNDERSCORE + StoreOperations.DELETE;
    String GET        = ERR_CACHE + UNDERSCORE + StoreOperations.GET;
}
