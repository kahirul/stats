package co.paikama.stats.repositories;

import co.paikama.stats.models.RunningSummary;
import co.paikama.stats.models.Transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum DefaultRepository {

    INSTANCE;

    // Hack, to reset statistics during test
    private boolean isTest = false;

    private final ConcurrentMap<Long, RunningSummary> statistics = new ConcurrentHashMap<>();

    public void add(Transaction transaction) {
        // Store transaction in bucket of 1 second resolution
        // Summarize all transactions in each bucket into single RunningSummary
        final long key = transaction.epochSecond();

        // Make sure only single thread construct RunningSummary for new key
        statistics.putIfAbsent(key, new RunningSummary());

        statistics.computeIfPresent(key, (k, v) -> v.updateAndReturn(transaction));
    }

    public RunningSummary get(long key) {
        return statistics.get(key);
    }

    public void clearEntry() {
        if (isTest) {
            statistics.clear();
        }
    }

}
