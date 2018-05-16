package de.mobilej.kmockit;


import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import kotlin.jvm.functions.Function1;

// this is Java since it didn't worked when using Kotlin
public class DelegateMethod {

    private static HashMap<String, HashMap<String, Function1<? super Object[], ? extends Object>>> methodToFunction = new HashMap<>();

    @RuntimeType
    public static Object perform(@Origin Method origin, @This Object thisRef, @AllArguments Object... args) {
        HashMap<String, Function1<? super Object[], ? extends Object>> map = methodToFunction.get(origin.getDeclaringClass().getName());
        if (map != null) {
            Function1<? super Object[], ?> function = map.get(createSignature(origin));
            if (function != null) {
                return function.invoke(args);
            }
        }

        return 0;
    }

    public static void add(String clzName, @Nullable Object javaMethod, @NotNull Function1<? super Object[], ? extends Object> function) {
        HashMap<String, Function1<? super Object[], ? extends Object>> map = methodToFunction.get(clzName);
        if (map == null) {
            map = new HashMap<>();
            methodToFunction.put(clzName, map);
        }

        map.put(createSignature(javaMethod), function);
    }

    private static String createSignature(@Nullable Object method) {
        if (method == null) {
            return "null";
        }

        if (method instanceof Method) {
            Method javaMethod = (Method) method;

            StringBuilder params = new StringBuilder();
            for (Class<?> paramType : javaMethod.getParameterTypes()) {
                params.append(paramType.getSimpleName());
            }

            String retType = javaMethod.getReturnType().toString();

            if (javaMethod.getName().equals("$init")) {
                retType = "_";
            }

            return javaMethod.getName() + retType + params.toString();
        }

        Constructor javaMethod = (Constructor) method;
        StringBuilder params = new StringBuilder();
        for (Class<?> paramType : javaMethod.getParameterTypes()) {
            params.append(paramType.getSimpleName());
        }
        return "$init_" + params.toString();
    }

}
