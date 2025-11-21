package net.truej.service.xxx;

import lombok.Data;

import java.util.List;


@Data public class Type {
    public final boolean isNullable;
    public final Class<?> aClass;
    public final List<Type> genericArguments;
}

