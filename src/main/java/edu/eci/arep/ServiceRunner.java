package edu.eci.arep;


import java.lang.reflect.Method;


public class ServiceRunner {

    public static void main(String[] args) throws Exception{
        for (String a : args){
            for (Method m : Class.forName(a).getMethods()) {
                if (m.isAnnotationPresent(Run.class)) {
                    try {
                        m.invoke(null);
                    } catch (Throwable ex) {
                        System.out.printf("Test %s failed: %s %n", m, ex.getCause());
                    }
                }
            }
        }
    }
}
