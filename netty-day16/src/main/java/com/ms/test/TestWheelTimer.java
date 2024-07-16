package com.ms.test;

import com.ms.util.HashedWheelTimer;
import com.ms.util.Timeout;
import com.ms.util.TimerTask;
import com.ms.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.TimeUnit;

public class TestWheelTimer {

    public static void main(String[] args) {
        HashedWheelTimer timer = new HashedWheelTimer(new DefaultThreadFactory("时间轮",false,5), 1, TimeUnit.SECONDS,8);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("1秒执行1次");
                timer.newTimeout(this, 0, TimeUnit.SECONDS);
            }
        };
        timer.newTimeout(timerTask, 0, TimeUnit.SECONDS);
    }
}
