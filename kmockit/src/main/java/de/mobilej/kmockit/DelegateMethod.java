package de.mobilej.kmockit;


import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;

import kotlin.jvm.functions.Function1;

// this is Java since it didn't worked when using Kotlin
public class DelegateMethod {

    private static HashMap<String,HashMap<String,Function1<? super Object[], ? extends Object>>>  methodToFunction = new HashMap<>();

    @RuntimeType
    public static Object perform(@Origin Method origin, @This Object thisRef, @AllArguments Object... args){
        HashMap<String, Function1<? super Object[], ? extends Object>> map = methodToFunction.get(origin.getDeclaringClass().getName());
        if(map!=null){
            Function1<? super Object[], ?> function = map.get(createSignature(origin));
            if(function!=null){
                return function.invoke(args);
            }
        }

        return 0;
    }

    public static void add(String clzName, @Nullable Method javaMethod, @NotNull Function1<? super Object[], ? extends Object> function) {
        HashMap<String, Function1<? super Object[], ? extends Object>> map = methodToFunction.get(clzName);
        if(map==null){
            map = new HashMap<>();
            methodToFunction.put(clzName, map);
        }

        map.put(createSignature(javaMethod), function);
    }

    private static String createSignature(@Nullable Method javaMethod) {
        // TODO also the param types need to be in the name
        if(javaMethod==null){
            return "null";
        }
        return javaMethod.getName()+javaMethod.getReturnType().toString();
    }

}
