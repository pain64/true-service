package net.truej.service.xxx;

import net.truej.service.Exchange;

public final class RealmConf<E extends Exchange>
    implements AnyServerConf<E> {

    public final AnyServerConf<E>[] configurations;

    @SafeVarargs public RealmConf(AnyServerConf<E>... configurations) {
        this.configurations = configurations;
    }
}
