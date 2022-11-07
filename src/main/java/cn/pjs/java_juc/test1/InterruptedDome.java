package cn.pjs.java_juc.test1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/7/24 13:43
 */
@Slf4j
public class InterruptedDome {

    private static volatile boolean isInterrupted1 = false;

    private static AtomicBoolean isInterrupted2 = new AtomicBoolean(false);

    @Test
    public void test1() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (isInterrupted1) {
                    System.out.println("其他线程请求终止线程，同意!!");
                    break;
                }
                System.out.println("线程正常运行");
            }
        });
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isInterrupted1 = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test2() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (isInterrupted2.get()) {
                    System.out.println("其他线程请求终止线程，同意!!");
                    break;
                }
                System.out.println("线程正常运行");
            }
        });
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isInterrupted2.set(true);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("其他线程请求终止线程，同意!!");
                    break;
                }
                System.out.println("线程正常运行");
            }
        });
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4(){
        Thread t1 = new Thread(() -> {

        });
        boolean interrupted = Thread.interrupted();
        boolean interrupted1 = t1.isInterrupted();
    }

    @Test
    public void test5() {
        Thread t1 = new Thread(() -> {
            log.info("t1线程进入");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            log.info("t1已唤醒");
        });
        t1.start();
        new Thread(() -> {
            log.info("t2线程进入");

            LockSupport.unpark(t1);
            log.info("t2运行完毕");
        }).start();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
