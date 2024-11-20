package Server;

import java.util.Optional;
import java.util.concurrent.*;

public class CommunicationUtils {

    public static <T> Optional<T> timeout(Callable<T> task, int timeoutMs) {
        ExecutorService executor = createDaemonExecutor();
        Future<T> future = executor.submit(task);
        try {
            T result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            return Optional.of(result);
        } catch (TimeoutException | ExecutionException | InterruptedException ex) {
            future.cancel(true);
        } finally {
            future.cancel(true);
            executor.shutdownNow();
        }
        return Optional.empty();
    }

    public static ExecutorService createDaemonExecutor() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }
}
