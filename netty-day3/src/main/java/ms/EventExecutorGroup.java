package ms;

import java.util.concurrent.TimeUnit;

/**
 * @Author: juggle
 * @Date: 2024/6/10 23:13
 * @Version:
 * @Description:
 **/
public interface EventExecutorGroup {
    EventExecutor next();
    void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);
}
