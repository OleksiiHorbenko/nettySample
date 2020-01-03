package o.horbenko.nnettysample.utils;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class SimpleMeasureUtils {


    public static <T>
    long measure(int threadsCount,
                 int executionsPerThreadCount,
                 Supplier<T> preparationAction,
                 Consumer<T> toCall) {

        Thread[] threads = new Thread[threadsCount];
        ConcurrentLinkedQueue<Long> executions = new ConcurrentLinkedQueue<>();

        // prepare threads and start
        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < executionsPerThreadCount; j++) {
                    try {

                        // prepare
                        T prepared = preparationAction.get();

                        // execute
                        long start = System.currentTimeMillis();
                        toCall.accept(prepared);
                        executions.add(System.currentTimeMillis() - start);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            threads[i].start();
        }

        // join all workers
        for (int i = 0; i < threadsCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // calculate average execution time
        BigInteger bi = BigInteger.ZERO;
        int totalCount = executions.size();

        // sum all
        for (int i = 0; i < executions.size(); i++) {
            bi = bi.add(BigInteger.valueOf(executions.poll()));
        }

        // divide on count
        long averageExecutionTime = bi.divide(BigInteger.valueOf(totalCount)).longValue();

        return averageExecutionTime;
    }

}
