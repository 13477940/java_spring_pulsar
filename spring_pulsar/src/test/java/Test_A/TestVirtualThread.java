package Test_A;

import framework.observer.Bundle;
import framework.observer.Handler;
import framework.observer.Message;

import java.util.*;
import java.util.concurrent.*;

public class TestVirtualThread {

    public static void main(String[] args) {
        TestVirtualThread test = new TestVirtualThread();
        // test.test_fn();
        // test.test_handler_fn();
        // test.test_fn3();
        // test.test_complete_future();
        // test.test_vector_calculator();
        test.test_async_with_future();
    }

    private void test_fn() {
        System.out.println("--- future, callable");
        ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
        HashMap<String, Integer> hash_map = new HashMap<>();
        // callable - work task
        Callable<String> callable = () -> {
            if(null == hash_map.get("count")) {
                hash_map.put("count", 1);
            } else {
                int tmp = hash_map.get("count");
                hash_map.put("count", (tmp + 1));
            }
            return String.valueOf(hash_map.get("count"));
        };
        for(int i = 0, len = 100; i < len; i++) {
            Future<String> future = worker.submit(callable);
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        worker.shutdown();
    }

    private void test_executor() {
        try ( ExecutorService worker_watch = Executors.newCachedThreadPool() ) {
            // do something...
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void test_vector_calculator() {
        {
            Vector<Integer> vector = new Vector<>();
            vector.add(1);
            vector.add(2);
            vector.add(3);
            int sum = vector.stream()
                    .mapToInt(Integer::valueOf) // or .map(i -> i)
                    .sum();
            System.out.println(sum); // 6
        }
        {
            Vector<Double> vec_double = new Vector<>();
            vec_double.add(100.55);
            vec_double.add(333.881);
            vec_double.add(444.5566);
            double total = vec_double.stream()
                    .mapToDouble(Double::valueOf) // or .map(i -> i)
                    .sum();
            System.out.println(Math.round(total));
        }
        {
            // 參數為指定 "" 符號作為分隔符號
            StringJoiner s_join = new StringJoiner("");
            s_join.add("aaa");
            s_join.add("bbb");
            s_join.add("ccc");
            System.out.println(s_join);
        }
    }

    /**
     * 以 VirtualThread 配合 Handler 會造成主執行緒無法察覺尚未完成的工作，
     * 所以需要做一些動作去延長主執行緒的壽命等待接收結果或是讓其可以察覺工作尚未完成
     */
    private void test_handler_fn() {
        System.out.println("--- use android handler");
        // 實際工作內容運行於協程中
        ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
        // 讓主執行緒維持一個觀察者執行緒
        ExecutorService worker_watch = Executors.newCachedThreadPool();
        // 儲存處理階段資料內容
        final HashMap<String, String> data_map = new HashMap<>();
        // 用於快速判斷是否已完成所有工作的特徵值
        StringBuilder sbd = new StringBuilder();
        // 處理協程間回傳的結果
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message m) {
                super.handleMessage(m);
                String status = m.getData().getString("status");
                switch (status) {
                    // 等待偵測器循環
                    case "waiting" -> {
                        // data_map.put("status", "waiting");
                        // System.out.println("handler waiting.");
                    }
                    // 所有事件完成時
                    case "done" -> {
                        sbd.append("1");
                        if("1".equalsIgnoreCase(sbd.toString())) {
                            data_map.put("status", "done");
                            System.out.println("event done.");
                            worker.shutdown();
                            worker_watch.shutdown();
                        }
                        sbd.append("2");
                    }
                    // 事件內容
                    case "event" -> {
                        data_map.put("status", "event");
                        {
                            if(null == data_map.get("count")) {
                                data_map.put("count", "1");
                            } else {
                                int i_tmp = Integer.parseInt(data_map.get("count"));
                                data_map.put("count", String.valueOf(i_tmp+1));
                            }
                        }
                        System.out.println(data_map.get("count"));
                        if("8".equalsIgnoreCase(data_map.get("count"))) {
                            Bundle b = new Bundle();
                            b.put("status", "done");
                            Message _m = this.obtainMessage();
                            _m.setData(b);
                            _m.sendToTarget();
                        }
                    }
                }
            }
        };
        {
            // use virtual thread for async process
            worker_watch.execute(() -> {
                while (true) {
                    if("done".equalsIgnoreCase(data_map.get("status"))) break;
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    {
                        Bundle b = new Bundle();
                        b.put("status", "waiting");
                        Message m = handler.obtainMessage();
                        m.setData(b);
                        m.sendToTarget();
                    }
                }
            });
        }
        for(int i = 0, len = 8; i < len; i++) {
            worker.execute(() -> {
                {
                    Bundle b = new Bundle();
                    b.put("status", "event");
                    b.put("count", data_map.get("count"));
                    Message m = handler.obtainMessage();
                    m.setData(b);
                    m.sendToTarget();
                }
            });
            // 以 sleep 作為每個 virtual thread 的 duration
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void test_fn3() {
        System.out.println("--- future task list test");
        Callable<Long> callable = () -> {
            long start = System.currentTimeMillis();
            Thread.sleep(100);
            long end = System.currentTimeMillis();
            long seed = end - start;
            System.out.println("seed=" + seed);
            return seed;
        };

        List<Callable<Long>> tasks = new ArrayList<>();
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);
        tasks.add(callable);

        int poolSize = Runtime.getRuntime().availableProcessors();
        System.out.println("poolSize=" + poolSize);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        try {
            List<Future<Long>> futures = executorService.invokeAll(tasks);

            long result = 0;
            for (Future<Long> future : futures) {
                result += future.get();
            }
            System.out.println("result=" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(tasks.size());
        executorService.shutdown();
    }

    private void test_complete_future() {
        // TODO example 1
        CompletableFuture.runAsync(() -> {
            // runnable
            System.out.println("do something...");
        }).whenComplete((r, ex) -> {
            // complete
            System.out.println("do something done...");
        });
        // TODO example 2
        CompletableFuture.runAsync(() -> {
            // runnable 1
            System.out.println("A");
        }).thenRun(() -> {
            // runnable 2
            System.out.println("B");
        }).thenRun(() -> {
            // runnable 3
            System.out.println("C");
        }).whenComplete((r, ex) -> {
            // complete
            System.out.println("done");
        });
    }

    private void test_async_with_future() {
        ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
        Future<?> future = worker.submit(() -> {
            run_exec_fn();
        });
        // 等待 run_exec_fn() 方法完成
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        worker.shutdown();
    }

    synchronized private void run_exec_fn() {
        System.out.println("test");
    }

}
