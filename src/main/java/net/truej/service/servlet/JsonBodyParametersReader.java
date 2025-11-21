package net.truej.service.servlet;

import jakarta.annotation.Nullable;
import net.truej.service.xxx.ParametersReader;
import net.truej.service.xxx.ParameterMetadata;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonBodyParametersReader implements ParametersReader<HttpServletExchange> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override public @Nullable Object[] read(
        HttpServletExchange exchange,
        List<ParameterMetadata> parameters
    ) {
        if (!exchange.request.getMethod().equals("POST")) {
            exchange.response.setStatus(400);
            try (var writer = exchange.response.getWriter()) {
                writer.println("expected POST request");
                writer.flush();
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        var arguments = new Object[parameters.size()];
        final JsonNode tree;
        try {
            tree = objectMapper.readTree(exchange.request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var i = 0; i < parameters.size(); i++) {
            var parameter = parameters.get(i);
            arguments[i] = objectMapper.treeToValue(
                tree.get(parameter.name), parameter.type.aClass
            );
        }

        return arguments;
    }
}
