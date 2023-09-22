package Test_A;

import framework.http.JdkHttpClient;
import framework.observer.Handler;
import framework.observer.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestHttpClient {

    public static void main(String[] args) {
        TestHttpClient test = new TestHttpClient();
        test.test_fn();
    }

    private void test_fn() {
        ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
        Future<?> future = worker.submit(() -> {
            JdkHttpClient htt_client = new JdkHttpClient.Builder()
                    .setUrl("https://google.com")
                    .build();
            htt_client.get(new Handler(){
                @Override
                public void handleMessage(Message m) {
                    super.handleMessage(m);
                    System.out.println(m.getData().toString().length());
                }
            });
        });
        {
            for(int i = 0, len = 10; i < len; i++) {
                System.out.println(i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
