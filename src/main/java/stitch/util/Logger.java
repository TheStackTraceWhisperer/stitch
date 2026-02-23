package stitch.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class Logger {
  private static final LinkedBlockingQueue<LogMessage> QUEUE = new LinkedBlockingQueue<>();
  private static final Thread PRINTER_THREAD;
  private static final AtomicInteger pendingTasks = new AtomicInteger(0);
  private static final AtomicInteger completedTasks = new AtomicInteger(0);
  private static volatile boolean trackingProgress = false;

  static {
    PRINTER_THREAD = new Thread(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          if (trackingProgress) {
            renderProgress();
            Thread.sleep(50);
            continue;
          }
          LogMessage msg = QUEUE.take();
          if (msg.isError()) {
            System.err.println(msg.text());
            if (msg.cause() != null) msg.cause().printStackTrace();
          } else {
            System.out.println(msg.text());
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    PRINTER_THREAD.setDaemon(true);
    PRINTER_THREAD.start();
  }

  public static void info(String message) {
    QUEUE.offer(new LogMessage(message, false, null));
  }

  public static void error(String message) {
    QUEUE.offer(new LogMessage(message, true, null));
  }

  public static void error(String message, Throwable cause) {
    QUEUE.offer(new LogMessage(message, true, cause));
  }

  public static void flush() {
    while (!QUEUE.isEmpty()) {
      try {
        Thread.sleep(10); // Yield to the printer thread to let it catch up
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  public static void startProgress() {
    trackingProgress = true;
    pendingTasks.set(0);
    completedTasks.set(0);
  }

  public static void incrementTotal() {
    pendingTasks.incrementAndGet();
  }

  public static void markCompleted() {
    completedTasks.incrementAndGet();
  }

  public static void stopProgress() {
    trackingProgress = false;
    System.out.print("\r" + " ".repeat(80) + "\r");
  }

  private static void renderProgress() {
    int total = pendingTasks.get();
    int done = completedTasks.get();
    System.out.print("\r⏳ Resolving modules... [" + done + " / " + total + " traversed]");
  }

  private record LogMessage(String text, boolean isError, Throwable cause) {
  }
}
