package net.truej.service.servlet;

import net.truej.service.xxx.ResultWriter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonBodyResultWriter implements ResultWriter<HttpServletExchange> {
    final ObjectMapper objectMapper = new ObjectMapper();

    @Override public void write(HttpServletExchange exchange, Object result) {
        exchange.response.setStatus(200);
        exchange.response.setCharacterEncoding("UTF-8");

        try (var out = exchange.response.getOutputStream()) {
            objectMapper.writeValue(out, result);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
