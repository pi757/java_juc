package cn.pjs.java_juc.atomic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/11/3 22:53
 */
public class AtomicIntegerFieldUpdateTest {

    @Test
    public void transfer() throws InterruptedException {
        MyBank myBank = new MyBank();
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myBank.add();
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(myBank.amount);
    }

    @Test
    public void initTest() throws InterruptedException {
        MyBank myBank = new MyBank();
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    myBank.init();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
    }

}

class MyBank {
    private String name;

    public volatile int amount = 0;

    public volatile Boolean isInit = false;

    AtomicIntegerFieldUpdater<MyBank> field = AtomicIntegerFieldUpdater.newUpdater(MyBank.class, "amount");

    public void add() {
        field.getAndAdd(this, 1);
//        amount++;
    }

    AtomicReferenceFieldUpdater<MyBank, Boolean> init = AtomicReferenceFieldUpdater.newUpdater(MyBank.class, Boolean.class, "isInit");

    public void init() throws InterruptedException {
        if (init.compareAndSet(this, Boolean.FALSE, Boolean.TRUE)) {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(Thread.currentThread().getName() + "初始化完成");
        } else {
            System.out.println(Thread.currentThread().getName() + "不需要初始化");
        }

    }

}
