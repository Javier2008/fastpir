package com.pir.fastpir;

import com.pir.fastpir.common.AjaxResult;
import com.pir.fastpir.service.FastPirClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootTest
class PirApplicationTests {
    @Autowired
    FastPirClient fastPirClient;

    @Autowired
    ThreadPoolTaskExecutor executor;
    @Test
    void testClient() throws InterruptedException, ExecutionException {
        //id只是一个标识，同时运行的程序不能
        executor.setCorePoolSize(40);
        List<Future> res = new ArrayList<>();
        for (long i = 0; i< 40; i++){
            long id = (long)i;
            Future f = executor.submit(()->test(id));
            res.add(f);
        }

        long s = System.currentTimeMillis();
        for (Future f: res){
            f.get();
        }
        long e = System.currentTimeMillis();
        System.out.println(e-s);
    }

    @Test
    void testClient2() throws InterruptedException {
        //id只是一个标识，同时运行的程序不能

       test(1236L);

        Thread.sleep(100000);
    }
    void test(long id){
        //fastPirClient.init(id, 10000, "124.239.244.85:8080",
        //    "data.csv", "", 18L, 18L, 2, "[*:{0-9}]");
        fastPirClient.init(id, 10000, "127.0.0.1:8080",
            "data.csv", "", 18L, 18L, 2, "[*:{0-9}]");
        for (int i = 0; i< 10; i++) {
            AjaxResult<String> res = fastPirClient.SinglePirExec2(id, "450914196108070741");
            //System.out.println(res.getData());
        }


    }
}
