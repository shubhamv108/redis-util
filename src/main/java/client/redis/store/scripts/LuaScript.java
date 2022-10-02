package client.redis.store.scripts;

public interface LuaScript {

    String RELEASE_LOCK_FOR_VALUE_TAKEN_IN_SINGLE_INSTANCE =
            "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "return redis.call(\"del\",KEYS[1]) " +
            "else " +
                "return 0 " +
            "end ";
}
