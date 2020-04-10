package net.kaaass.kflight.eventhandle;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Modified from forge
 *
 * @author cpw
 */
@Slf4j
public class EventBus implements IEventExceptionHandler {
    private static int maxID = 0;

    /**
     * 维护处理类与若干子事件的映射
     */
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<>();

    /**
     * 维护插件事件监听映射
     */
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> pluginListeners = new ConcurrentHashMap<>();
    private final int busID = maxID++;
    private IEventExceptionHandler exceptionHandler;

    public EventBus() {
        ListenerList.resize(busID + 1);
        exceptionHandler = this;
    }

    public EventBus(IEventExceptionHandler handler) {
        this();
        // Preconditions.checkArgument(handler != null, "EventBus exception handler can not be null");
        exceptionHandler = handler;
    }

    public void register(Object target) {
        if (listeners.containsKey(target)) {
            return;
        }

        Set<? extends Class<?>> supers = getSuperAndMe(target.getClass());
        for (Method method : target.getClass().getMethods()) {
            for (Class<?> cls : supers) {
                try {
                    Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (real.isAnnotationPresent(SubscribeEvent.class)) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length != 1) {
                            throw new IllegalArgumentException(
                                    "Method " + method + " has @SubscribeEvent annotation, but requires " + parameterTypes.length +
                                            " arguments.  Event handler methods must require a single argument."
                            );
                        }

                        Class<?> eventType = parameterTypes[0];

                        if (!Event.class.isAssignableFrom(eventType)) {
                            throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but takes a argument that is not an Event " + eventType);
                        }

                        register(eventType, target, method);
                        break;
                    }
                } catch (NoSuchMethodException ignored) {
                    ;
                }
            }
        }
    }

    /**
     * Java语言事件注册
     *
     * @param eventType 事件类
     * @param target    事件处理器目标类
     * @param method    目标方法
     */
    private void register(Class<?> eventType, Object target, Method method) {
        try {
            Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            Event event = (Event) ctr.newInstance();
            ASMEventHandler listener = new ASMEventHandler(target, method);
            event.getListenerList().register(busID, listener.getPriority(), listener);

            ArrayList<IEventListener> others = listeners.computeIfAbsent(target, k -> new ArrayList<>());
            others.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregister(String pluginId) {
        var list = pluginListeners.remove(pluginId);
        if (list == null)
            return;
        for (var listener : list) {
            ListenerList.unregisterAll(busID, listener);
        }
    }

    public void unregister(Object object) {
        ArrayList<IEventListener> list = listeners.remove(object);
        if (list == null)
            return;
        for (IEventListener listener : list) {
            ListenerList.unregisterAll(busID, listener);
        }
    }

    public boolean post(Event event) {
        log.debug("触发事件：{}", event.getClass().getName());
        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        int index = 0;
        try {
            for (; index < listeners.length; index++) {
                listeners[index].invoke(event);
            }
        } catch (Throwable throwable) {
            exceptionHandler.handleException(this, event, listeners, index, throwable);
            throw throwable;
        }
        return (event.isCancelable() && event.isCanceled());
    }

    @Override
    public void handleException(EventBus bus, Event event, IEventListener[] listeners, int index, Throwable throwable) {
        log.error("Exception caught during firing event {}:", event, throwable);
        log.error("Index: {} Listeners:", index);
        for (int x = 0; x < listeners.length; x++) {
            log.error("{}: {}", x, listeners[x]);
        }
    }

    private static Set<Class<?>> getSuperAndMe(Class<?> clazz) {
        Set<Class<?>> clazzs = new HashSet<>() {{
            add(clazz);
        }};
        Class<?> curSuper = clazz.getSuperclass();
        while (curSuper != null) {
            clazzs.add(curSuper);
            curSuper = curSuper.getSuperclass();
        }
        return clazzs;
    }
}