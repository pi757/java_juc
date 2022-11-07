package cn.pjs.java_juc.atomic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/11/2 22:48
 */
public class AtomicIntegerTest {

    @Test
    public void intSyncTest() throws InterruptedException {
        MyInteger myInteger = new MyInteger();
        int i = 50;
        CountDownLatch countDownLatch = new CountDownLatch(i);
        for (int j = 0; j < i; j++) {
            new Thread(() -> {
                for (int k = 0; k < 1000; k++) {
                    myInteger.add();
                }
               countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(myInteger.num.get());
    }

}
class MyInteger {
    AtomicInteger num = new AtomicInteger();

    public void add() {
        num.incrementAndGet();
    }
}
