package cz.edu.upce.fei.datacollector;

import cz.edu.upce.fei.datacollector.tasks.ConnectionHandlerTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private final static int THREAD_COUNT = 1;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        CompletableFuture<Void> myTask = CompletableFuture.runAsync(new ConnectionHandlerTask(), executorService)
                .exceptionally(t -> {
                            System.out.println("Chybka");
                            return null;
                        }
                );

        myTask.join();

        System.out.println(myTask.isDone());

        executorService.shutdown();
    }
}
