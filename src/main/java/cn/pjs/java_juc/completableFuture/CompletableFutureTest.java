package cn.pjs.java_juc.completableFuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureTest {

    ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        //发起一个没有入参，又返回得线程
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }, pool).thenApplyAsync(v -> {
            //上一个发生异常后不会进入
            //上一个的结果
            //处理后返回新的
            try {
                log.info("原结果{}， 新结果：{}", v, v + 1);
                return v + 1;
            } catch (Exception e) {
                log.info("捕获异常");
            }
            return 99;
        }, pool).handleAsync((v, e) -> {
            //有返回值
            return v + 1;
        }).whenCompleteAsync((v, e) -> {
            //上一个的结果和异常
            //没有返回值
            log.info("v:{}", v);
            if (e != null) {
                log.error("{}, 发生异常", v, e);
            }
        }, pool).exceptionally(e -> {
            //发生异常是进入
            //返回默认值
            log.info("进入exceptionally");
            if (e != null) {
                log.error("发生异常");
            }
            return 0;
        });
        log.info("end:{}", future.join());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
