package com.ms.util;

/**
 * 该接口是AttributeMap中存储的value的实现类的接口
 * @param <T>
 */
public interface Attribute<T> {

    AttributeKey<T> key();

    T get();

    void set(T value);

    T getAndSet(T value);

    T setIfAbsent(T value);

    @Deprecated
    T getAndRemove();

    boolean compareAndSet(T oldValue, T newValue);

    @Deprecated
    void remove();
}
