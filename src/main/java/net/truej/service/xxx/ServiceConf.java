package net.truej.service.xxx;

import net.truej.service.Exchange;

public final class ServiceConf<E extends Exchange>
    implements AnyServerConf<E> {

    public final Object instance;
    public final AnyServiceConf<E>[] configurations;

    @SafeVarargs ServiceConf(
        Object instance,
        AnyServiceConf<E>... configurations
    ) {
        this.instance = instance;
        this.configurations = configurations;
    }
}
