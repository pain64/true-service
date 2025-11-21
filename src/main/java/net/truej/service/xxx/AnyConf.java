package net.truej.service.xxx;

import net.truej.service.Exchange;

public sealed interface AnyConf<E extends Exchange> permits AnyServerConf, AnyServiceConf, AnyMethodConf { }
