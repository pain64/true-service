package net.truej.service.servlet;

import net.truej.service.xxx.ResultWriter;

public class RedirectResultWriter implements ResultWriter<HttpServletExchange> {
    @Override public void write(HttpServletExchange exchange, Object result) {
        exchange.response.setStatus(302);
        exchange.response.setHeader("Location", (String) result);
    }
}
