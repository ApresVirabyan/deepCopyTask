import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DeepCopy {

    public static <T> T deepCopy(T object) {
        Map<Object, Object> map = new IdentityHashMap<>();
        try {
            return recursiveDeepCopy(object, map);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T recursiveDeepCopy(T object, Map<Object, Object> objectMap) throws IllegalAccessException {
        if (object == null) {
            return null;
        }

        if (objectMap.containsKey(object)) {
            return (T) objectMap.get(object);
        }

        Class<?> clazz = object.getClass();

        if (clazz.isPrimitive() || isWrapper(clazz) || clazz == String.class) {
            return object;
        }

        if (clazz.isArray()) {
            int length = Array.getLength(object);
            Object copyArr = Array.newInstance(clazz.getComponentType(), length);
            objectMap.put(object, copyArr);
            for (int i = 0; i < length; i++) {
                Array.set(copyArr, i, recursiveDeepCopy(Array.get(object, i), objectMap));
            }
            return (T) copyArr;
        }

        if (object instanceof Collection) {
            Collection<?> coll = (Collection<?>) object;
            Collection<Object> copyColl = coll instanceof Set ? new HashSet<>() : new ArrayList<>();
            objectMap.put(object, copyColl);
            for (Object obj : coll) {
                copyColl.add(recursiveDeepCopy(obj, objectMap));
            }
            return (T) copyColl;
        }

        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            Map<Object, Object> copyMap = new HashMap<>();
            objectMap.put(object, copyMap);
            for(Map.Entry<?, ?> entry : map.entrySet()){
                Object copyKey = recursiveDeepCopy(entry.getKey(), objectMap);
                Object copyValue = recursiveDeepCopy(entry.getValue(), objectMap);
                copyMap.put(copyKey, copyValue);
            }

            return (T) copyMap;
        }

        try{
            T copy = (T) clazz.getDeclaredConstructor().newInstance();
            objectMap.put(object, copy);
            for(Field field: clazz.getDeclaredFields()){
                field.setAccessible(true);
                Object objectField = field.get(object);
                field.set(copy, recursiveDeepCopy(objectField, objectMap));
            }
            return copy;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isWrapper(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Byte.class || clazz == Character.class ||
                clazz == Double.class || clazz == Float.class || clazz == Integer.class ||
                clazz == Long.class || clazz == Short.class;
    }
}
