package ru.tinkoff.kora.cache.annotation.processor.aop;

import com.squareup.javapoet.CodeBlock;
import ru.tinkoff.kora.annotation.processor.common.MethodUtils;
import ru.tinkoff.kora.cache.annotation.CacheInvalidate;
import ru.tinkoff.kora.cache.annotation.CacheInvalidates;
import ru.tinkoff.kora.cache.annotation.processor.CacheMeta;
import ru.tinkoff.kora.cache.annotation.processor.CacheOperation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.Set;

import static ru.tinkoff.kora.cache.annotation.processor.CacheOperationManager.getCacheOperation;

public class CacheInvalidateAopKoraAspect extends AbstractAopCacheAspect {

    private final ProcessingEnvironment env;

    public CacheInvalidateAopKoraAspect(ProcessingEnvironment env) {
        this.env = env;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CacheInvalidate.class.getCanonicalName(), CacheInvalidates.class.getCanonicalName());
    }

    @Override
    public ApplyResult apply(ExecutableElement method, String superCall, AspectContext aspectContext) {
        final CacheOperation operation = getCacheOperation(method, env);
        final CacheMirrors cacheMirrors = getCacheMirrors(operation, method, env);

        final List<String> cacheFields = getCacheFields(operation, cacheMirrors, aspectContext);
        final CodeBlock body;
        if (MethodUtils.isMono(method, env)) {
            if (operation.meta().type() == CacheMeta.Type.EVICT_ALL) {
                body = buildBodyMonoAll(method, operation, superCall, cacheFields);
            } else {
                body = buildBodyMono(method, operation, superCall, cacheFields);
            }
        } else {
            if (operation.meta().type() == CacheMeta.Type.EVICT_ALL) {
                body = buildBodySyncAll(method, operation, superCall, cacheFields);
            } else {
                body = buildBodySync(method, operation, superCall, cacheFields);
            }
        }

        return new ApplyResult.MethodBody(body);
    }

    private CodeBlock buildBodySync(ExecutableElement method,
                                    CacheOperation operation,
                                    String superCall,
                                    List<String> cacheFields) {
        final String recordParameters = getKeyRecordParameters(operation, method);
        final String superMethod = getSuperMethod(method, superCall);

        // cache variables
        final StringBuilder builder = new StringBuilder();

        // cache super method
        if (MethodUtils.isVoid(method)) {
            builder.append(superMethod).append(";\n");
        } else {
            builder.append("var value = ").append(superMethod).append(";\n");
        }

        // cache invalidate
        for (final String cache : cacheFields) {
            builder.append(cache).append(".invalidate(_key);\n");
        }

        if (MethodUtils.isVoid(method)) {
            builder.append("return;");
        } else {
            builder.append("return value;");
        }

        return CodeBlock.builder()
            .add("""
                    var _key = new $L($L);
                    """,
                operation.key().simpleName(), recordParameters)
            .add(builder.toString())
            .build();
    }

    private CodeBlock buildBodySyncAll(ExecutableElement method,
                                       CacheOperation operation,
                                       String superCall,
                                       List<String> cacheFields) {
        final String superMethod = getSuperMethod(method, superCall);

        // cache variables
        final StringBuilder builder = new StringBuilder();

        // cache super method
        if (MethodUtils.isVoid(method)) {
            builder.append(superMethod).append(";\n");
        } else {
            builder.append("var _value = ").append(superMethod).append(";\n");
        }

        // cache invalidate
        for (final String cache : cacheFields) {
            builder.append(cache).append(".invalidateAll();\n");
        }

        if (MethodUtils.isVoid(method)) {
            builder.append("return;");
        } else {
            builder.append("return _value;");
        }

        return CodeBlock.builder()
            .add(builder.toString())
            .build();
    }

    private CodeBlock buildBodyMono(ExecutableElement method,
                                    CacheOperation operation,
                                    String superCall,
                                    List<String> cacheFields) {
        final String recordParameters = getKeyRecordParameters(operation, method);
        final String superMethod = getSuperMethod(method, superCall);

        // cache variables
        final StringBuilder builder = new StringBuilder();

        // cache super method
        builder.append("return ").append(superMethod);

        if (cacheFields.size() > 1) {
            builder.append(".publishOn(reactor.core.scheduler.Schedulers.boundedElastic()).doOnSuccess(_result -> reactor.core.publisher.Flux.merge(java.util.List.of(\n");

            // cache put
            for (int i = 0; i < cacheFields.size(); i++) {
                final String cache = cacheFields.get(i);
                final String suffix = (i == cacheFields.size() - 1)
                    ? ".invalidateAsync(_key)\n"
                    : ".invalidateAsync(_key),\n";
                builder.append("\t").append(cache).append(suffix);
            }
            builder.append(")).then().block());");
        } else {
            builder.append(".doOnSuccess(_result -> ").append(cacheFields.get(0)).append(".invalidate(_key));\n");
        }

        return CodeBlock.builder()
            .add("""
                    var _key = new $L($L);
                    """,
                operation.key().simpleName(), recordParameters)
            .add(builder.toString())
            .build();

    }

    private CodeBlock buildBodyMonoAll(ExecutableElement method,
                                       CacheOperation operation,
                                       String superCall,
                                       List<String> cacheFields) {
        final String superMethod = getSuperMethod(method, superCall);

        // cache variables
        final StringBuilder builder = new StringBuilder();

        // cache super method
        builder.append("return ").append(superMethod);

        if (cacheFields.size() > 1) {
            builder.append(".publishOn(reactor.core.scheduler.Schedulers.boundedElastic()).doOnSuccess(_result -> reactor.core.publisher.Flux.merge(java.util.List.of(\n");

            // cache put
            for (int i = 0; i < cacheFields.size(); i++) {
                final String cache = cacheFields.get(i);
                final String suffix = (i == cacheFields.size() - 1)
                    ? ".invalidateAllAsync()\n"
                    : ".invalidateAllAsync(),\n";
                builder.append("\t").append(cache).append(suffix);
            }
            builder.append(")).then().block());");
        } else {
            builder.append(".doOnSuccess(_result -> ").append(cacheFields.get(0)).append(".invalidateAll());\n");
        }

        return CodeBlock.builder()
            .add(builder.toString())
            .build();
    }
}
