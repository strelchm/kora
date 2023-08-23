package ru.tinkoff.kora.cache.annotation.processor.testdata.reactive.mono;

import reactor.core.publisher.Mono;
import ru.tinkoff.kora.cache.annotation.CacheInvalidate;
import ru.tinkoff.kora.cache.annotation.CachePut;
import ru.tinkoff.kora.cache.annotation.Cacheable;
import ru.tinkoff.kora.cache.annotation.processor.testcache.DummyCache1;
import ru.tinkoff.kora.cache.annotation.processor.testcache.DummyCache12;

import java.math.BigDecimal;

public class CacheableMonoOneMany {

    public String value = "1";

    @Cacheable(DummyCache1.class)
    @Cacheable(DummyCache12.class)
    public Mono<String> getValue(String arg1) {
        return Mono.just(value);
    }

    @CachePut(value = DummyCache1.class, parameters = {"arg1"})
    @CachePut(value = DummyCache12.class, parameters = {"arg1"})
    public Mono<String> putValue(BigDecimal arg2, String arg3, String arg1) {
        return Mono.just(value);
    }

    @CacheInvalidate(DummyCache1.class)
    @CacheInvalidate(DummyCache12.class)
    public Mono<Void> evictValue(String arg1) {
        return Mono.empty();
    }

    @CacheInvalidate(value = DummyCache1.class, invalidateAll = true)
    @CacheInvalidate(value = DummyCache12.class, invalidateAll = true)
    public Mono<Void> evictAll() {
        return Mono.empty();
    }
}
