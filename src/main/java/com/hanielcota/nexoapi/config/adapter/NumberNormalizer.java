package com.hanielcota.nexoapi.config.adapter;

import lombok.NonNull;

public final class NumberNormalizer {

    public Object normalize(@NonNull Object raw, @NonNull Class<?> type) {
        if (!(raw instanceof Number)) {
            return raw;
        }

        return castNumber((Number) raw, type);
    }

    private Object castNumber(Number num, Class<?> type) {
        if (type == Integer.class) return num.intValue();
        if (type == Long.class) return num.longValue();
        if (type == Double.class) return num.doubleValue();
        if (type == Float.class) return num.floatValue();
        if (type == Short.class) return num.shortValue();
        if (type == Byte.class) return num.byteValue();

        return num;
    }
}