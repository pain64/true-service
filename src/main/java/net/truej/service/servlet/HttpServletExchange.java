package net.truej.service.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.truej.service.Exchange;

@RequiredArgsConstructor
public class HttpServletExchange implements Exchange {
    public final HttpServletRequest request;
    public final HttpServletResponse response;
}
