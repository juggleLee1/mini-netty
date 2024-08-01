package com.ms.util;


import java.util.concurrent.atomic.AtomicLong;


/**
 * 这里面的方法应该都很好理解，都是最基础的几个方法
 * T 必须是 AbstractConstant<T> 的一个子类，这样可以在子类中保留类型信息。
 * @param <T>
 */
public abstract class AbstractConstant<T extends AbstractConstant<T>> implements Constant<T> {

    /**
     * 这个long类型的id是用来比较常量大小的
     */
    private static final AtomicLong uniqueIdGenerator = new AtomicLong();
    private final int id;
    private final String name;
    private final long uniquifier;

    protected AbstractConstant(int id, String name) {
        this.id = id;
        this.name = name;
        this.uniquifier = uniqueIdGenerator.getAndIncrement();
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final String toString() {
        return name();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * 这就是 自限定泛型类型得到意义，不要对 o 再进行类型转换
     * @param o the object to be compared.
     * @return
     */
    @Override
    public final int compareTo(T o) {
        if (this == o) {
            return 0;
        }
        AbstractConstant<T> other = o;
        int returnCode;

        returnCode = hashCode() - other.hashCode();
        if (returnCode != 0) {
            return returnCode;
        }

        if (uniquifier < other.uniquifier) {
            return -1;
        }
        if (uniquifier > other.uniquifier) {
            return 1;
        }

        throw new Error("failed to compare two different constants");
    }
}
