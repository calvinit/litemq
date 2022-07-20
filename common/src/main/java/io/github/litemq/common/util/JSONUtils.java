package io.github.litemq.common.util;

import com.google.gson.Gson;

/**
 * JSON 操作工具类
 *
 * @author calvinit
 * @since 0.0.1
 */
public class JSONUtils {

    private static final Gson GSON = new Gson();

    private JSONUtils() {}

    public static String toJSONString(Object src) {
        return GSON.toJson(src);
    }

    public static <T> T parse(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
