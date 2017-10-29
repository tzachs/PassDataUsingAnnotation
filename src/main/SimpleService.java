package main;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by tzachs on 24/10/2017.
 */
public class SimpleService implements ISimpleService {

    public static final ISimpleService INSTANCE = (ISimpleService) DebugProxy.newInstance(new SimpleService());

    private SimpleService(){

    }


    @Override
    @TrackThis(tag = "Mouse")
    public void setStatus(boolean newStatus) {
        changeAnnotationValue( "status", String.valueOf(newStatus));
    }

    private Object changeAnnotationValue(String key, Object newValue){
        Method[] methods = this.getClass().getDeclaredMethods();
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        Method runningMethod = null;
        for (Method method : methods) {
            if ( method.getName().contentEquals(stackTraceElement.getMethodName())){
                runningMethod = method;
            }
        }

        if ( runningMethod == null){
            throw new RuntimeException("Could not find method");
        }

        Annotation annotation = runningMethod.getAnnotations()[0];

        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key,newValue);
        memberValues.put("uniqueID", Thread.currentThread().getId());
        return oldValue;
    }



}
