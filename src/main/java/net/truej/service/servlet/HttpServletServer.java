package net.truej.service.servlet;

import net.truej.service.xxx.AnyServerConf;
import net.truej.service.xxx.ParametersReader;
import net.truej.service.xxx.ResultWriter;
import net.truej.service.xxx.Server.Endpoint;
import net.truej.service.xxx.Server.State;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.truej.service.xxx.Server.apply;

public class HttpServletServer {

    public static final ParametersReader<HttpServletExchange>
        DEFAULT_PARAMETERS_READER = new JsonBodyParametersReader();

    public static final ResultWriter<HttpServletExchange>
        DEFAULT_RESULT_WRITER = new JsonBodyResultWriter();

    private final Map<String, Endpoint<HttpServletExchange>> routes;

    @SafeVarargs public HttpServletServer(
        AnyServerConf<HttpServletExchange>... configurations
    ) {
        var endpoints = new ArrayList<Endpoint<HttpServletExchange>>();
        apply(
            endpoints, new State<>(Map.of(), List.of(), null, null),
            Arrays.asList(configurations)
        );

        routes = endpoints.stream().collect(
            Collectors.toMap(
                e -> "/" + e.serviceInstance.getClass().getSimpleName() +
                     "." + e.serviceMethod.getName(), e -> e
            )
        );

        System.out.println(routes);
    }

    public void serve(HttpServletExchange exchange) {
        var route = routes.get(exchange.request.getPathInfo());
        if (route == null) {
            exchange.response.setStatus(404);
            try (var writer = exchange.response.getWriter()) {
                writer.println("Not found");
                writer.flush();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        var reader = route.configuration.parametersReader;
        if (reader == null) reader = DEFAULT_PARAMETERS_READER;

        var arguments = reader.read(exchange, route.parameters);
        final Object result;
        try {
            result = route.serviceMethod.invoke(route.serviceInstance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        var writer = route.configuration.resultWriter;
        if (writer == null) writer = DEFAULT_RESULT_WRITER;
        writer.write(exchange, result);
    }
}
