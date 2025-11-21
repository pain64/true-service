package net.truej.service.xxx;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.With;
import net.truej.service.ContextResolver;
import net.truej.service.Exchange;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Server {

    @Data public static class Endpoint<E extends Exchange> {
        public final Object serviceInstance;
        public final Method serviceMethod;
        public final State<E> configuration;
        public final List<ParameterMetadata> parameters;
        public final Type resultType;
    }

    @Data @With public static class State<E extends Exchange> {
        public final Map<Class<?>, ContextResolver<E, ?>> contextResolvers;
        public final List<Interceptor<E>> interceptors;
        public final @Nullable ParametersReader<E> parametersReader;
        public final @Nullable ResultWriter<E> resultWriter;
    }

    public static <E extends Exchange> Consumer<E> joinInterceptors(
        List<Interceptor<E>> interceptors, int i, Runnable dest
    ) {
        return i < interceptors.size() ?
            xc -> interceptors.get(i).invoke(
                xc, () -> joinInterceptors(interceptors, i + 1, dest).accept(xc)
            ) :
            xc -> dest.run();
    }

    public static <E extends Exchange> State<E> applyConf(
        State<E> state, List<AnyConf<E>> configurations
    ) {
        var ns = state;
        var hasReaderConfig = false;
        var hasWriterConfig = false;

        for (var conf : configurations)
            ns = switch (conf) {
                case ContextConf<E, ?> c -> ns.withContextResolvers(
                    new HashMap<>(ns.contextResolvers) {{
                        if (put(c.toClass, c.extractor) != null)
                            throw new RuntimeException(
                                "context parameter resolver for class "
                                + c.toClass + " already configured"
                            );
                    }}
                );
                case InterceptorConf<E> c -> ns.withInterceptors(
                    new ArrayList<>(ns.interceptors) {{
                        add(c.interceptor);
                    }}
                );
                case ParametersReader<E> c -> {
                    if (hasReaderConfig) throw new RuntimeException(
                        "more than one `ParametersReader` cannot be defined at one level"
                    );
                    hasReaderConfig = true;

                    yield ns.withParametersReader(c);
                }
                case ResultWriter<E> c -> {
                    if (hasWriterConfig) throw new RuntimeException(
                        "more than one `ResultWriter` cannot be defined at one level"
                    );
                    hasWriterConfig = true;

                    yield ns.withResultWriter(c);
                }
                default -> ns;
            };

        return ns;
    }

    // readerFactory: Array<AnnotatedType> -> (InputStream -> Array<T>)
    // ??? Bytecode generation ???
    //   1. C = create class (DTO)
    //   2. dto = jackson.deserialize(C.class)
    //   3. return new Object[]{ dto.f1, dto.f2, dto.f3 }
    //
    //                Array<AnnotatedType> -> TypeScript client code ???
    //                                        Metadata ???

    public static <E extends Exchange> void apply(
        List<Endpoint<E>> result, State<E> state, List<AnyConf<E>> configurations
    ) {
        var ns = applyConf(state, configurations);
        for (var conf : configurations)
            switch (conf) {
                case RealmConf<E> c -> apply(result, ns, Arrays.asList(c.configurations));
                case ServiceConf<E> c -> {
                    var ns2 = applyConf(ns, Arrays.asList(c.configurations));

                    for (var method : c.instance.getClass().getDeclaredMethods())
                        if ((method.getModifiers() & Modifier.PUBLIC) != 0) {
                            var mConf = Stream.of(c.configurations)
                                .filter(p ->
                                    p instanceof MethodConf<E> mc &&
                                    mc.methodName.equals(method.getName())
                                )
                                .map(p -> (MethodConf<E>) p)
                                .findFirst().orElse(null);

                            var mc = mConf == null ? ns2 :
                                applyConf(ns2, Arrays.asList(mConf.configurations));


                            result.add(
                                new Endpoint<>(
                                    c.instance, method, mc,
                                    Stream.of(method.getParameters()).map(
                                        p -> new ParameterMetadata(p.getName(), null)
                                    ).toList(),
                                    null
                                )
                            );
                        }
                }
                default -> { }
            }
    }

    @SafeVarargs public static <E extends Exchange>
    ServiceConf<E> service(Object instance, AnyServiceConf<E>... configurations) {
        return new ServiceConf<>(instance, configurations);
    }

    @SafeVarargs public static <E extends Exchange>
    MethodConf<E> method(String methodName, AnyMethodConf<E>... configurations) {
        return new MethodConf<>(methodName, configurations);
    }

    public static <E2 extends Exchange>
    InterceptorConf<E2> interceptor(Interceptor<E2> interceptor) {
        return new InterceptorConf<>(interceptor);
    }

    public static <E extends Exchange, T>
    ContextConf<E, T> context(Class<T> toClass, ContextResolver<E, T> extractor) {
        return new ContextConf<>(toClass, extractor);
    }

    public static <E extends Exchange>
    ResultWriter<E> resultWriter(ResultWriter<E> writer) {
        return writer;
    }

    @SafeVarargs public static <E extends Exchange>
    RealmConf<E> realm(AnyServerConf<E>... configurations) {
        return new RealmConf<>(configurations);
    }

    public static <E3 extends Exchange>
    ParametersReader<E3> parametersReader(ParametersReader<E3> reader) {
        return reader;
    }
}
