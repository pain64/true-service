package net.truej.service.header;

public record Headers2<
    H1 extends HttpHeader, H2 extends HttpHeader>
    (H1 h1, H2 h2) { }
