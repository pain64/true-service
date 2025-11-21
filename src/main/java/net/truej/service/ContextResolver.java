package net.truej.service;


public interface ContextResolver<E extends Exchange, T> {
    T resolve(E exchange);
}
