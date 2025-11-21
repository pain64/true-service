package net.truej.service.xxx;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.truej.service.ContextResolver;
import net.truej.service.Exchange;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ContextConf<E extends Exchange, T>
    implements AnyServerConf<E>, AnyServiceConf<E> {

    final Class<T> toClass;
    final ContextResolver<E, T> extractor;
}
