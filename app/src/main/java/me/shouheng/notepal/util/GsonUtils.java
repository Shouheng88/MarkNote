package me.shouheng.notepal.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by WngShhng on 2017/12/11. */
public class GsonUtils {

    private static Gson gson = new Gson();

    private GsonUtils() {}

    /**
     * transfer object to json
     *
     * @param object object
     * @return string of object */
    public static String toString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * transfer string to object
     *
     * @param gsonString gson string
     * @param cls class of object
     * @return the object
     */
    public static <T> T toObject(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * transfer string to list
     *
     * @param gsonString gson string
     * @param cls cls of list item
     * @return the list string
     */
    public static <T> List<T> toList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * transfer list contained with map
     *
     * @param gsonString gson string
     * @return the list
     */
    public static <T> List<Map<String, T>> toListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {}.getType());
        }
        return list;
    }

    /**
     * transfer string to map
     *
     * @param gsonString gson string
     * @return map
     */
    public static <T> Map<String, T> toMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }
}
