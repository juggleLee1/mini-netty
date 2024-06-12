package com.ms.mode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: juggle
 * @Date: 2024/6/12 22:22
 * @Version:
 * @Description:
 **/
public class Son {
    private List<Listener> listenerList = new ArrayList<>();

    public Son addListener(Listener listener) {
        listenerList.add(listener);
        return this;
    }

    public void doWork() {
        System.out.println("上学");
        for (Listener listener : listenerList) {
            listener.doSomeThing();
        }
    }

    public static void main(String[] args) {
        Son son = new Son();
        son.addListener(new Father()).addListener(new Mother());

        son.doWork();
    }
}
