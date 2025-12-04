package net.truej.service;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.*;

public class TsDtoGenerator {
    interface WriteNext {
        Void write(Out out);
    }

    public static class Out {
        public final StringBuilder buffer;
        int offset = 0;
        int currentColumn = 0;

        public Out(StringBuilder buffer) {
            this.buffer = buffer;
        }

        private void writeChar(Character ch) {
            buffer.append(ch);
            if (ch == '\n') {
                buffer.repeat(' ', offset);
                currentColumn = offset;
            } else
                currentColumn++;
        }

        private void writePart(String part) {
            for (var j = 0; j < part.length(); j++)
                writeChar(part.charAt(j));
        }

        public Void w(Object... fragments) throws RuntimeException {
            for (var value : fragments) {
                if (value instanceof WriteNext next) {
                    var oldOffset = offset;
                    offset = currentColumn;
                    next.write(this);
                    offset = oldOffset;
                } else
                    writePart(value.toString());
            }

            return null;
        }

        interface EachNext<T> {
            Void write(Out out, Integer index, T element);
        }

        static <T> WriteNext each(List<T> list, String delimiter, EachNext<T> next) {
            return out -> {
                for (var i = 0; i < list.size(); i++) {
                    next.write(out, i, list.get(i));
                    if (i != list.size() - 1)
                        out.writePart(delimiter);
                }
                return null;
            };
        }
    }

    // Map класс - парсинг в TS
    static Map<Class<?>, String> ClassMap = new HashMap<>();
    // Map класс - местоположение для парсинга
    static Map<Class<?>, String> ClassLocation = new HashMap<>();
    static String currentParentClass = "generics";

    //Типы Java -> Типы TS
    private static String classify(String typeName){
        return switch (typeName) {
            case "int", "Integer", "short", "Short", "float", "Float", "double", "Double", "byte", "Byte" -> "number";
            case "long", "Long" -> "bigint";
            case "char", "Character", "String" -> "string";
            case "bool", "Boolean" -> "boolean";
            default -> typeName;
        };
    }

    private static void mapClass(Class<?> clazz){
        var newClass = new Out(new StringBuilder());
        newClass.w("type ", clazz.getName().replace(".", "_"));
        var typeParameters = clazz.getTypeParameters();
        if (typeParameters.length != 0){
            var typesList = Out.each(Arrays.stream(typeParameters).toList(), ", ", (o, __, typeParameter) ->
                    o.w(classify(typeParameter.getTypeName())));
            newClass.w("<", typesList, ">");
        }
        var constructorParameters =  Out.each(Arrays.stream(clazz.getDeclaredConstructors()[0].getParameters()).toList(),
                ",\n", (o, __, parameter) ->
                        o.w("   ", parameter.getName(), ": ", mapTypeToTS(parameter.getAnnotatedType())));
        newClass.w(" = {\n",
                constructorParameters,
                "\n}\n");
        ClassMap.put(clazz, newClass.buffer.toString());
        ClassLocation.put(clazz, currentParentClass);
    }

    //FIXME: Проверить параметризованные типы
    private static String mapTypeToTS(AnnotatedType annotatedType){
        var out = new Out(new StringBuilder());
        if(annotatedType instanceof AnnotatedArrayType){
            var type = ((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType();
            out.w("Array<", mapTypeToTS(type), ">");
        } else if(annotatedType instanceof AnnotatedParameterizedType){
            var type = annotatedType.getType();
            var rawType = ((ParameterizedType) type).getRawType();
            var parameterTypes = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
            var clazz = (Class<?>) rawType;
            if((rawType == List.class) || (rawType == ArrayList.class)){
                out.w("Array<", mapTypeToTS(parameterTypes[0]), ">");
            } else if(!ClassMap.containsKey(clazz)){
                mapClass(clazz);
                var parsedParameters = Out.each(Arrays.stream(parameterTypes).toList(), ", ", (o, __, parameterType) ->
                        o.w(mapTypeToTS(parameterType)));
                out.w(clazz.getName().replace(".", "_"), "<", parsedParameters, ">");
            } else if (!ClassLocation.get(clazz).equals(currentParentClass) && !ClassLocation.get(clazz).equals("generics")){
                ClassLocation.replace(clazz, "generics");
                var parsedParameters = Out.each(Arrays.stream(parameterTypes).toList(), ", ", (o, __, parameterType) ->
                        o.w(mapTypeToTS(parameterType)));
                out.w(clazz.getName().replace(".", "_"), "<", parsedParameters, ">");
            }
            else {
                var parsedParameters = Out.each(Arrays.stream(parameterTypes).toList(), ", ", (o, __, parameterType) ->
                        o.w(mapTypeToTS(parameterType)));
                out.w(clazz.getName().replace(".", "_"), "<", parsedParameters, ">");
            }
        } else if (annotatedType instanceof AnnotatedTypeVariable) {
            out.w(annotatedType.getType());
        } else if (annotatedType instanceof AnnotatedWildcardType) {
            throw new RuntimeException("found AnnotatedWildcardType");
        } else {
            var type = annotatedType.getType();
            var clazz = (Class<?>) type;
            switch(clazz.getSimpleName()) {
                case "int":
                case "Integer":
                case "short":
                case "Short":
                case "float":
                case "Float":
                case "double":
                case "Double":
                case "byte":
                case "Byte":
                    out.w("number");
                    break;
                case "long":
                case "Long":
                    out.w("bigint");
                    break;
                case "char":
                case "Character":
                case "String":
                    out.w("string");
                    break;
                case "bool":
                case "Boolean":
                    out.w("boolean");
                    break;
                default:
                    if(clazz.isEnum() && !ClassMap.containsKey(clazz)){
                        var newClass = new Out(new StringBuilder());
                        var enumConstants = Out.each(Arrays.stream(clazz.getEnumConstants()).toList(), ",\n", (o, __, enumConstant) ->
                                o.w("   ", enumConstant.toString()));
                        newClass.w(
                                "enum ", clazz.getName().replace(".", "_"), " {\n",
                                enumConstants,
                                "\n}\n"
                        );
                        ClassMap.put(clazz, newClass.buffer.toString());
                        ClassLocation.put(clazz, currentParentClass);
                    }
                    else if(clazz.isRecord() && !ClassMap.containsKey(clazz)){
                        var newClass = new Out(new StringBuilder());
                        var recordComponents = Out.each(Arrays.stream(clazz.getRecordComponents()).toList(), ",\n", (o, __, recordComponent) ->
                                o.w("   ", recordComponent.getName(), ": ", mapTypeToTS(recordComponent.getAnnotatedType())));
                        newClass.w(
                                "type ", clazz.getName().replace(".", "_"), " = {\n",
                                recordComponents,
                                "\n}\n"
                        );
                        ClassMap.put(clazz, newClass.buffer.toString());
                        ClassLocation.put(clazz, currentParentClass);
                    }
                    else if(!ClassMap.containsKey(clazz)){
                        mapClass(clazz);
                    }
                    else if(!ClassLocation.get(clazz).equals(currentParentClass) && !ClassLocation.get(clazz).equals("generics")){
                        ClassLocation.replace(clazz, "generics");
                    }
                    out.w(clazz.getName().replace(".", "_"));
                    break;
            }
        }
        if(annotatedType.isAnnotationPresent(Nullable.class)){
            out.w(" | null");
        }
        return out.buffer.toString();
    }

    //Перевод параметров метода в строку (name: type) для TypeScript
    private static String parameterTypeNameString(Method method){
        var out = new Out(new StringBuilder());
        out.w(Out.each(Arrays.stream(method.getParameters()).toList(), ", ", (o, __, parameter) ->
                o.w(parameter.getName(), ": ", mapTypeToTS(parameter.getAnnotatedType()))));
        return out.buffer.toString();
    }
    //Перевод параметров метода в строку их имён
    private static String parameterNameString(Method method){
        var out = new Out(new StringBuilder());
        out.w(Out.each(Arrays.stream(method.getParameters()).toList(), ", ", (o, __, parameter) ->
                o.w(parameter.getName())));
        return out.buffer.toString();
    }
    //Генерация TS файла для класса
    //FIXME: сделать обработку только методов с аннотациями, добавить import generics.ts, сделать настройку каталога
    private static void generateTs(Class<?> clazz){
        var out = new Out(new StringBuilder());
        currentParentClass = clazz.getSimpleName();
        var addressPlaceholder = clazz.getSimpleName() + ".ts";
        var transformed = Out.each(Arrays.stream(clazz.getDeclaredMethods()).toList(), "\n", (o, __, method) ->
                o.w(
                        "export function ", method.getName(), "(", parameterTypeNameString(method), "){\n",
                        "   apiCall('/", clazz.getSimpleName(), ".", method.getName(), "', {", parameterNameString(method) + "})\n",
                        "}\n"
                ));
        var customTypesList = new ArrayList<String>();
        for (Map.Entry<Class<?>, String> entry : ClassLocation.entrySet()){
            if(entry.getValue().equals(clazz.getSimpleName())){
                customTypesList.add(ClassMap.get(entry.getKey()));
            }
        }
        var customTypes = Out.each(customTypesList, "\n", (o, __, customType) -> o.w(customType));
        out.w(customTypes, "\n", transformed);
        try (PrintWriter writer = new PrintWriter("Frontend/" + addressPlaceholder)) {
            writer.println(out.buffer.toString());
            System.out.println("File '" + addressPlaceholder + "' created successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    //FIXME: сделать обработку только методов с аннотациями, сделать настройку каталога
    public static void generateFrontDTO(Class<?>... apis){
        var out = new Out(new StringBuilder());
        File frontendDirectory = new File("Frontend");
        if(frontendDirectory.mkdir()){
            System.out.println("Directory created: " + frontendDirectory.getAbsolutePath());
        }
        else {
            System.out.println("Failed to create nested directory: " + frontendDirectory.getAbsolutePath());
        }

        for(Class<?> api : apis){
            for(Method method: api.getDeclaredMethods()){
                currentParentClass = api.getSimpleName();
                parameterTypeNameString(method);
            }
        }
        for(Class<?> api : apis){
            generateTs(api);
        }

        var customTypesList = new ArrayList<String>();
        for (Map.Entry<Class<?>, String> entry : ClassLocation.entrySet()){
            if(entry.getValue().equals("generics")){
                customTypesList.add(ClassMap.get(entry.getKey()));
            }
        }
        var customTypes = Out.each(customTypesList, "\n", (o, __, customType) -> o.w(customType));
        out.w(customTypes);
        try (PrintWriter writer = new PrintWriter("Frontend/generics.ts")) {
            writer.println(out.buffer.toString());
            System.out.println("File 'generics.ts' created successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
