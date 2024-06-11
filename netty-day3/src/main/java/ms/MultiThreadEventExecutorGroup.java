package ms;

import java.util.concurrent.TimeUnit;

/**
 * @Author: juggle
 * @Date: 2024/6/10 23:15
 * @Version:
 * @Description:
 **/
public abstract class MultiThreadEventExecutorGroup implements EventExecutorGroup {
    private EventExecutor[] eventExecutor;

    private int index = 0;

    public MultiThreadEventExecutorGroup(int threads) {
        this.eventExecutor = new EventExecutor[threads];
        for (int i = 0; i < threads; i++) {
            eventExecutor[i] = newChild();
        }
    }

    protected abstract EventLoop newChild();

    @Override
    public EventExecutor next() {
        int id = index % eventExecutor.length;
        index++;
        return eventExecutor[id];
    }

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        next().shutdownGracefully(quietPeriod, timeout, unit);
    }
}
