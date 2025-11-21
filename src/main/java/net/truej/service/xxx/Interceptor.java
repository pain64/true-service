package net.truej.service.xxx;

import net.truej.service.Exchange;

public interface Interceptor<E extends Exchange> {
    void invoke(E exchange, Runnable invocation);
}
