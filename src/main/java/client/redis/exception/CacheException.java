package client.redis.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CacheException extends RuntimeException {

    public CacheException(Throwable e) {
        super(e);
    }

    public CacheException(String message, Throwable e) {
        super(message, e);
    }

}
