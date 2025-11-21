package net.truej.service.xxx;

import net.truej.service.Exchange;

public non-sealed interface ResultWriter<E extends Exchange>
    extends AnyServerConf<E>, AnyServiceConf<E>, AnyMethodConf<E> {

    void write(E exchange, Object result);
}
