package main;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tzachs on 24/10/2017.
 */
public class DebugProxy implements java.lang.reflect.InvocationHandler{
    private Object obj;
    private final Object lock = new Object();

    public static Object newInstance(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new DebugProxy(obj));
    }

    private DebugProxy(Object obj) {
        this.obj = obj;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable
    {
        synchronized (lock) { // <-- THIS IS BECAUSE OF THE SINGLETON
            Object result;
            try {
                m.setAccessible(true);
                result = m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " +
                        e.getMessage());
            } finally {
                //System.out.println("after method " + m.getName());
                Annotation[] annotations = obj.getClass().getDeclaredMethod(m.getName(), m.getParameterTypes()).getDeclaredAnnotations();
                if (annotations.length > 0) {
                    Annotation a = annotations[0];
                    if (a.annotationType() == TrackThis.class) {
                        String tag = ((TrackThis) a).tag();
                        String status = ((TrackThis) a).status();
                        long uniqueID = ((TrackThis) a).uniqueID();
                        System.out.println(
                                String.format("%s, %s, %s",
                                        tag, status, uniqueID));
                    }
                } else {
                    System.out.println("No annotations!");
                }
            }
            return result;
        }
    }
}
