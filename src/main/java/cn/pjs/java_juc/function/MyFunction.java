package cn.pjs.java_juc.function;

import org.junit.jupiter.api.Test;

import java.util.function.*;

/**
 * @version 1.0
 * @Author jiaSong.pi
 * @Date 2022/11/5 17:03
 */
@FunctionalInterface
public interface MyFunction {

    int print(String s);

    /**
     * 属于这个接口的，子实现类没办法重写,没办法调用，只属于接口
     * @param x
     * @param y
     * @return
     */
    static int add(int x, int y) {
        return x + y;
    }

    static int Multiplying(int x, int y) {
        return x * y;
    }

    /**
     * 子实现类可以重写(不强制)。类似模板方法
     * @param x
     * @param y
     * @return
     */
    default int Subtraction(int x, int y) {
        return x -y;
    }

    default int Division(int x, int y) {
        return x/y;
    }

}

class MyFunctionImpl implements MyFunction {

    @Override
    public int print(String s) {
        System.out.println("实现类:"+ s);
        return 0;
    }

    @Override
    public int Division(int x, int y) {
        return x + y;
    }
}

class TestFunction{

    @Test
    public void testMyFunction() {
        MyFunction myFunction = param -> myMethod(param);
        interfaceParams(myFunction);
    }

    private void interfaceParams(MyFunction myFunction) {
        //函数式方法
        System.out.println(myFunction.print("1234"));
        //直接通过接口调用static方法
        myFunction.print("" + MyFunction.add(1, 3));
    }


    private int myMethod(String s) {
        System.out.println("传了参数我就是不用，嘿嘿!!");
        return 1;
    }

    @Test
    public void defaultTest() {
        MyFunction myFunction = new MyFunctionImpl();
        //子类强制实现的方法
        myFunction.print("123");
        //子类调用父类的没重写方法
        System.out.println(myFunction.Subtraction(100, 99));
        //子类调用重写方法
        System.out.println(myFunction.Division(1, 0));
    }

    @Test
    public void abstractTest() {
        abstractParams(new MyAbstractImpl());
    }

    private void abstractParams(MyAbstract myAbstract) {
        myAbstract.pri();
        myAbstract.prit();
    }
}

abstract class MyAbstract{

    abstract void pri();

    void prit() {
        System.out.println("模板方法");
    }
}

class MyAbstractImpl extends MyAbstract {

    @Override
    void pri() {
        System.out.println("继承模板方法");
    }
}
