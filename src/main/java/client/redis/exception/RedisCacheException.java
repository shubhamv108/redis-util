package client.redis.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisCacheException extends CacheException {

    public RedisCacheException(Throwable e) {
        super(e);
    }

    public RedisCacheException(String message, Throwable e) {
        super(message, e);
    }

}
