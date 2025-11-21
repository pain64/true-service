package net.truej.service.xxx;

import jakarta.annotation.Nullable;
import net.truej.service.Exchange;

import java.util.List;

public non-sealed interface ParametersReader<E2 extends Exchange>
    extends AnyServerConf<E2>, AnyServiceConf<E2>, AnyMethodConf<E2>
{
    @Nullable Object[] read(E2 exchange, List<ParameterMetadata> parameters);
}
