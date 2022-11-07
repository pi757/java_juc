package cn.pjs.java_juc.jvm;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/7/14 23:03
 */
public class Decompilation {
    Object o = new Object();
    public void a1() {
        synchronized (o) {
            System.out.println("1111111");
        }
    }

    public synchronized void a2() {
        System.out.println("22222222222222");
    }

    public synchronized static void a3() {
        System.out.println("3333333333333333");
    }



}
