package cn.pjs.java_juc.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/11/5 15:31
 */
@Slf4j
public class AccumulatorTest {

    @Test
    public void longTest() {
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for(int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        //具体的线程逻辑
                        longAccumulator.accumulate(1);
                    }
                } catch (Exception e) {
                    log.error("线程处理异常", e);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("线程处理等待异常", e);
        }
        System.out.println(longAccumulator.get());
    }

    @Test
    public void testComparison() {
        System.out.println("sycn++");
        ClickNum clickNum1 = new ClickNum();
        abstractMethods(clickNum1::syncAdd, clickNum1);
        System.out.println("accumulatorAdd++");
        ClickNum clickNum2 = new ClickNum();
        abstractMethods(clickNum2::accumulatorAdd, clickNum2);
        System.out.println("adderAdd++");
        ClickNum clickNum3 = new ClickNum();
        abstractMethods(clickNum3::adderAdd, clickNum3);
        System.out.println("atomicAdd++");
        ClickNum clickNum4 = new ClickNum();
        abstractMethods(clickNum4::atomicAdd, clickNum4);
    }


    private void abstractMethods(Supplier<Long> supplier, ClickNum clickNum) {
        int threadNum = 50;
        int forNmu = 100 * 10000;
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for(int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < forNmu; j++) {
                        //具体的线程逻辑
                        supplier.get();
                    }
                } catch (Exception e) {
                    log.error("线程处理异常", e);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("线程处理等待异常", e);
        }
        System.out.println("用时：" + (System.currentTimeMillis() - start) + "结果：" + clickNum.get());
    }

}

class ClickNum {
    protected long synchronizedInt = 0;

    public synchronized Long syncAdd() {
        synchronizedInt++;
        return 0L;
    }

    protected AtomicLong atomicLong = new AtomicLong(0);

    public Long atomicAdd() {
        atomicLong.incrementAndGet();
        return 0L;
    }

    protected LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);

    public Long accumulatorAdd() {
        longAccumulator.accumulate(1);
        return 0L;
    }

    protected LongAdder longAdder = new LongAdder();

    public Long adderAdd() {
        longAdder.increment();
        return 0L;
    }

    public Long get() {
        if (synchronizedInt != 0) {
            return synchronizedInt;
        }
        if (atomicLong.get() != 0) {
            return atomicLong.get();
        }
        if (longAccumulator.get() != 0) {
            return longAccumulator.get();
        }
        return longAdder.sum();
    }
}
