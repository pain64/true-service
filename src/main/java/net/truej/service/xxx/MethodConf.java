package net.truej.service.xxx;

import net.truej.service.Exchange;

public final class MethodConf <E extends Exchange> implements AnyServiceConf<E> {
    public final String methodName;
    public final AnyMethodConf<E>[] configurations;

    @SafeVarargs MethodConf(
        String methodName,
        AnyMethodConf<E>... configurations
    ) {
        this.methodName = methodName;
        this.configurations = configurations;
    }
}