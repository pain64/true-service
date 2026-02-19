package net.truej.service.servlet;

import net.truej.service.xxx.ResultWriter;

public class RedirectWriter implements ResultWriter<HttpServletExchange> {
    @Override public void write(HttpServletExchange exchange, Object result) {
        exchange.response.setStatus(302);
        exchange.response.setHeader("Location", (String) result);
    }
}
