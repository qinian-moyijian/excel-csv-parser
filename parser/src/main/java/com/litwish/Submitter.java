package com.litwish;

import com.litwish.parser.Parser;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Date: 2022/2/21 15:30
 * @Authror: Xiaoming Zhang
 */
public class Submitter {
    private static final  ExecutorService TASK_POOL = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            1L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public static void submit(ArrayList<? extends Parser> list){
        for (Parser parser : list) {
            TASK_POOL.submit(()->{
                try {
                    parser.process();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
