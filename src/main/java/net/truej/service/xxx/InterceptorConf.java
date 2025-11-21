package net.truej.service.xxx;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.truej.service.Exchange;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class InterceptorConf<E2 extends Exchange>
    implements AnyServerConf<E2>, AnyServiceConf<E2> {

    final Interceptor<E2> interceptor;
}
