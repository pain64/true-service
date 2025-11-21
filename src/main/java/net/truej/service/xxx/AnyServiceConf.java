package net.truej.service.xxx;

import net.truej.service.Exchange;

public sealed interface AnyServiceConf<E extends Exchange> extends AnyConf<E> permits ContextConf, InterceptorConf, MethodConf, ParametersReader, ResultWriter { }
