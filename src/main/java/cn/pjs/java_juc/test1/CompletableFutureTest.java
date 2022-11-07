package cn.pjs.java_juc.test1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class CompletableFutureTest {

    static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

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

    /**
     * 测试then对比
     */
    @Test
    public void testThen() {
        CompletableFuture.supplyAsync(() -> {
                    return 1 + 1;
                }).thenApplyAsync((v) -> {
                    v = 1 + 1;
                    return "s";
                })
                .thenAccept((v) -> {
                    String s = v + "1";
                    System.out.println(s);
                })
//                .thenAcceptBoth()
//                .thenCombine()
//                .thenCompose()
                .thenRun(() -> {

                });

    }

    @Test
    public void testEither() throws InterruptedException {
        CompletableFuture.supplyAsync(() -> {
                    log.info("开始1，start");
                    try {
                        TimeUnit.SECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("开始1，end:{}", 1);
                    return 1;
                }).applyToEitherAsync(CompletableFuture.supplyAsync(() -> {
                    log.info("开始2，start");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("开始2，end:{}", 2);
                    return 2;
                }, pool), v -> {
                    log.info("进入apply, 结果：{}", v);
                    return v;
                }, pool).join();
//                .acceptEither(CompletableFuture.supplyAsync(() -> {
//                    System.out.println("进入开始3，start");
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println(3);
//                    return 3;
//                }), v -> {
//                    System.out.println("进入accept");
//                    try {
//                        TimeUnit.SECONDS.sleep(2);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println(v);
//                })
//                .runAfterEither(CompletableFuture.supplyAsync(() ->{
//           System.out.println("进入run1");
//           System.out.println(4);
//            return 1;}), () -> {
//           System.out.println("进入run2");
//           System.out.println(5);
//       }).join();
    }

    @Test
    public void testCombine() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 10;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 20;
        }), (r1, r2) -> {
            return r1 + r2;
        }).thenAcceptBoth(CompletableFuture.supplyAsync(() -> {
            return 30;
        }), (r3, r4) -> {
            log.info("{}", r3 + r4);
        }).join());
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 10;
        }).runAfterBoth(CompletableFuture.supplyAsync(() -> {
            return 20;
        }), () -> {
            System.out.println("1");
        }).join());
    }

    @Test
    public void TestOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        });

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+256G";
        });

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try { TimeUnit.SECONDS.sleep(3);  } catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("查询商品介绍");
            return "华为";
        });
        //需要全部完成
//        futureImg.get();
//        futureAttr.get();
//        futureDesc.get();
        CompletableFuture<Void> all = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        all.get();
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        anyOf.get();
        System.out.println(anyOf.get());
        System.out.println("main over.....");
    }
    @AllArgsConstructor
    @Data
    static class NetMall{
        private String name;

        public Double getPrice() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ThreadLocalRandom.current().nextDouble() * 2 + this.getName().charAt(0);
        }
    }

    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("pdd"),
            new NetMall("taobao"),
            new NetMall("dangdangwang"),
            new NetMall("tmall"));

    public static List<String> getPriceByStep(List<NetMall> list,String productName) {

        return list
                .stream()
                .map(netMall -> String.format(productName + " in %s price is %.2f", netMall.getName(),
                        netMall.getPrice()))
                .collect(Collectors.toList());
    }
    //异步 ,多箭齐发
    /**
     * List<NetMall>  ---->List<CompletableFuture<String>> --->   List<String>
     * @param list
     * @param productName
     * @return
     */
    public static List<String> getPriceByASync(List<NetMall> list,String productName) {
        return list
                .stream()
                .map(netMall ->
                        CompletableFuture.supplyAsync(() ->
                                String.format(productName + " is %s price is %.2f", netMall.getName(), netMall.getPrice()), pool)
                )
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
    @Test
    public void testPrice() {
        long startTime = System.currentTimeMillis();
        List<String> list1 = getPriceByStep(list, "mysql");
        for (String element : list1) {
            System.out.println(element);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime: "+(endTime - startTime) +" 毫秒");

        System.out.println();

        long startTime2 = System.currentTimeMillis();
        List<String> list2 = getPriceByASync(list, "mysql");
        for (String element : list2) {
            System.out.println(element);
        }
        long endTime2 = System.currentTimeMillis();
        System.out.println("----costTime: "+(endTime2 - startTime2) +" 毫秒");
    }
}
