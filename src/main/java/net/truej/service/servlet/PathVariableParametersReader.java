package net.truej.service.servlet;

import net.truej.service.xxx.ParametersReader;
import net.truej.service.xxx.ParameterMetadata;

import java.util.List;

public class PathVariableParametersReader implements ParametersReader<HttpServletExchange> {

    Object decodeArgument(String value, Class<?> toClass) {
        if (toClass == String.class)
            return value;
        if (toClass == Integer.class || toClass == int.class)
            return Integer.parseInt(value);

        throw new RuntimeException("cannot decode parameter as " + toClass.getName());
    }

    @Override public Object[] read(
        HttpServletExchange exchange, List<ParameterMetadata> parameters
    ) {

        var arguments = new Object[parameters.size()];
        var paths = exchange.request.getServletPath().split("/");

        for (var i = 0; i < parameters.size(); i++)
            arguments[i] = decodeArgument(paths[i], parameters.get(i).type.aClass);

        return arguments;
    }
}
