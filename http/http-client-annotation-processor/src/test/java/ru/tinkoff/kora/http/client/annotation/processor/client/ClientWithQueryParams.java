package ru.tinkoff.kora.http.client.annotation.processor.client;


import ru.tinkoff.kora.http.client.common.annotation.HttpClient;
import ru.tinkoff.kora.http.common.HttpMethod;
import ru.tinkoff.kora.http.common.annotation.Header;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.Query;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@HttpClient(configPath = "clientWithQueryParams")
public interface ClientWithQueryParams {
    @HttpRoute(method = HttpMethod.POST, path = "/test1?test=test")
    void test1(@Query String test1);

    @HttpRoute(method = HttpMethod.POST, path = "/test2?")
    void test2(@Query("test2") String test);

    @HttpRoute(method = HttpMethod.POST, path = "/test3")
    void test3(@Query("test3") String test);

    @HttpRoute(method = HttpMethod.POST, path = "/test4")
    void test4(@Query("test4") String test4, @Nullable @Query("test") String test);

    @HttpRoute(method = HttpMethod.POST, path = "/test5")
    void test5(@Query String test51, @Query String test52, @Query String test53, @Query String test54, @Nullable @Query String test55, @Nullable @Query String test56);

    @HttpRoute(method = HttpMethod.POST, path = "/test6")
    void test6(@Nullable @Query String test61, @Nullable @Query String test62, @Nullable @Query String test63);

    @HttpRoute(method = HttpMethod.POST, path = "/nonStringParams")
    void nonStringParams(@Query int query1, @Query Integer query2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleQueryParams")
    void multipleQueriesLists(@Query List<String> query1, @Nullable @Query List<Integer> query2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleQueryParams")
    void multipleQueriesSets(@Query Set<String> query1, @Nullable @Query Set<Integer> query2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleQueryParams")
    void multipleQueriesCollections(@Query Collection<String> query1, @Nullable @Query Collection<Integer> query2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleHeaders")
    void multipleHeadersLists(@Header List<String> headers1, @Nullable @Header List<Integer> headers2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleHeaders")
    void multipleHeadersSets(@Header Set<String> headers1, @Nullable @Header Set<Integer> headers2);

    @HttpRoute(method = HttpMethod.POST, path = "/multipleHeaders")
    void multipleHeadersCollections(@Header Collection<String> headers1, @Nullable @Header Collection<Integer> headers2);
}
