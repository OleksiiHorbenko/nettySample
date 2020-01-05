package o.horbenko;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HttpClient {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        int requestByteArraySize = 1024;

        System.out.println("Max size is " + requestByteArraySize);

        byte[] requestPayload = generateRandomByteArray(requestByteArraySize);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(requestPayload);

        Request request = new Request.Builder()
                .url("http://localhost:8081/api")
                .addHeader("Content-Length", String.valueOf(requestPayload.length))
                .post(requestBody)
                .build();

        Call call = client.newCall(request);

        ServerResponseFutureImpl responseFuture = new ServerResponseFutureImpl(call);
        CallbackResponseFutureAdapter callback = new CallbackResponseFutureAdapter(responseFuture);

        long start = System.currentTimeMillis();

        // async call
        call.enqueue(callback);
        byte[] responseBodyByteArray = responseFuture.get(); // block
        long executionTime = System.currentTimeMillis() - start;

        boolean isRequestEqualsWithResponse = Arrays.equals(requestPayload, responseBodyByteArray);
        System.out.println("Is equals " + isRequestEqualsWithResponse);
        System.out.println("Executio time is " + executionTime + " ms");
    }


    private static byte[] generateRandomByteArray(int bytesCount) {
        byte[] res = new byte[bytesCount];
        new SecureRandom().nextBytes(res);
        return res;
    }


    interface ServerResponseFuture extends Future<byte[]> {
    }

    static class CallbackResponseFutureAdapter implements Callback {

        private final ServerResponseFutureImpl future;

        CallbackResponseFutureAdapter(ServerResponseFutureImpl future) {
            this.future = future;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            future.setFailure(e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if (response.isSuccessful()) {
                future.setResponse(response.body().bytes());
            } else {
                future.setFailure(new RuntimeException("Server response code was " + response.code()));
            }
        }
    }

    static class ServerResponseFutureImpl implements ServerResponseFuture {

        // thread
        private final Lock locker = new ReentrantLock();
        private final Condition condition = locker.newCondition();

        // call
        private final Call okHttp3Call;

        // result
        private byte[] maybeResponseBody;
        private Throwable maybeException;

        private boolean exceptionThrown = false;
        private boolean isDone = false;

        ServerResponseFutureImpl(Call okHttp3Call) {
            this.okHttp3Call = okHttp3Call;
        }

        public void setFailure(Throwable e) {
            locker.lock();
            try {

                this.maybeException = e;
                this.isDone = true;
                condition.signalAll();

            } finally {
                locker.unlock();
            }
        }

        public void setResponse(byte[] responseBodyBytes) {
            locker.lock();
            try {

                this.maybeResponseBody = responseBodyBytes;
                this.isDone = true;
                condition.signalAll();

            } finally {
                locker.unlock();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            okHttp3Call.cancel();
            return true;
        }

        @Override
        public boolean isCancelled() {
            return okHttp3Call.isCanceled();
        }

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public byte[] get() throws InterruptedException, ExecutionException {

            locker.lock();
            try {

                while (!isDone)
                    condition.await();

                if (exceptionThrown) {
                    throw new ExecutionException(maybeException);
                } else {
                    return maybeResponseBody;
                }


            } finally {
                locker.unlock();
            }
        }

        @Override
        @Deprecated
        public byte[] get(long timeout, @NotNull TimeUnit unit) throws
                InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }
    }

}
