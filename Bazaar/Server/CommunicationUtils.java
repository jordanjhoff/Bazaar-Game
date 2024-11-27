package Server;

import java.util.Optional;
import java.util.concurrent.*;

public class CommunicationUtils {

    /**
     * Abstract wrapper to limit the time a function has to run.
     * Returns Optional.empty if time was exceeded.
     * -==--==--==--==--==--==--==--==--==--==--==-
     * Call with type Callable<Void> & return null for fire & forget functions.
     * @param task Anonymous callable function to be executed.
     * @param timeoutMs maximum allowed runtime for this function. May return early!
     * @return Optional<T> if the function returned, Optional.empty if it ran out of time.
     */
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

    /**
     * Creates an Executor Service that utilizes safer (background) Daemon Threads
     */
    public static ExecutorService createDaemonExecutor() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setUncaughtExceptionHandler((thread, e) -> {
                System.err.println("Thread shutting down due to exception: " + e.getMessage());
            });
            return t;
        });
    }
}
