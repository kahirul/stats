package co.paikama.stats.models;

import java.util.concurrent.locks.ReentrantLock;

public class RunningSummary extends BaseSummary {

    private final ReentrantLock lock = new ReentrantLock(true);

    public RunningSummary updateAndReturn(Transaction transaction) {
        lock.lock();
        try {
            final double amount = transaction.getAmount();
            if (count == 0L) {
                max = amount;
                min = amount;
            } else {
                max = Math.max(max, amount);
                min = Math.min(min, amount);
            }
            count++;
            sum += amount;
            avg = sum / count;
        } finally {
            lock.unlock();
        }

        return this;
    }

}
