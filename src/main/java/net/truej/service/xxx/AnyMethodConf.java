package net.truej.service.xxx;

import net.truej.service.Exchange;

public sealed interface AnyMethodConf<E extends Exchange> extends AnyConf<E> permits ParametersReader, ResultWriter { }
