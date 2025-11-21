package net.truej.service.xxx;

import net.truej.service.Exchange;

public sealed interface AnyServerConf<E extends Exchange> extends AnyConf<E> permits ContextConf, InterceptorConf, ParametersReader, RealmConf, ResultWriter, ServiceConf { }
