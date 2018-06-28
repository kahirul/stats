package co.paikama.stats.services;

import co.paikama.stats.models.BaseSummary;
import co.paikama.stats.models.RunningSummary;
import co.paikama.stats.models.Transaction;
import co.paikama.stats.repositories.DefaultRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StatisticsService {

    public void add(Transaction transaction) {
        DefaultRepository.INSTANCE.add(transaction);
    }

    public BaseSummary latest(int n) {
        double sum = 0.0D;
        double avg = 0.0D;
        double max = 0.0D;
        double min = 0.0D;
        long count = 0L;

        final long current = Instant.now().getEpochSecond();
        RunningSummary runningSummary;

        for (int i = 0; i < n; i++) {
            runningSummary = DefaultRepository.INSTANCE.get(current - i);
            if (runningSummary != null) {
                if (count == 0L) {
                    max = runningSummary.getMax();
                    min = runningSummary.getMin();
                } else {
                    max = Math.max(max, runningSummary.getMax());
                    min = Math.min(min, runningSummary.getMin());
                }

                count += runningSummary.getCount();
                sum += runningSummary.getSum();
                avg = sum / count;
            }
        }

        return new BaseSummary(sum, avg, max, min, count);
    }

}
