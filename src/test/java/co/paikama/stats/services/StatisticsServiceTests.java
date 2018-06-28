package co.paikama.stats.services;

import co.paikama.stats.TestHelper;
import co.paikama.stats.models.BaseSummary;
import co.paikama.stats.models.RunningSummary;
import co.paikama.stats.models.Transaction;
import co.paikama.stats.repositories.DefaultRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class StatisticsServiceTests {

    @TestConfiguration
    static class StatisticsServiceTestContextConfiguration {

        @Bean
        public StatisticsService statisticsService() {
            return new StatisticsService();
        }
    }

    @Autowired
    private StatisticsService statisticsService;

    private long timestamp;
    private Transaction transaction;
    private DoubleSummaryStatistics statistics;

    @Before
    public void setup() {
        timestamp = Instant.now().toEpochMilli();
        transaction = new Transaction(12.3D, timestamp);
    }

    @After
    public void teardown() {
        TestHelper.resetSingleton();
    }

    @Test
    public void testAddTransactionSuccessfully() {
        final long key = transaction.epochSecond();

        RunningSummary found = DefaultRepository.INSTANCE.get(key);
        assertNull(found);

        statisticsService.add(transaction);

        found = DefaultRepository.INSTANCE.get(key);
        assertNotNull(found);
        assertEquals(found.getCount(), 1L);
        assertEquals(found.getSum(), 12.3D, 1e-5);

        statisticsService.add(this.transaction);
        found = DefaultRepository.INSTANCE.get(key);
        assertEquals(found.getCount(), 2L);
        assertEquals(found.getSum(), 24.6D, 1e-5);
    }

    @Test
    public void testGetLatestStatistics() {
        bulkInsert();
        final BaseSummary latest = statisticsService.latest(60);

        assertEquals(latest.getCount(), statistics.getCount());
        assertEquals(latest.getSum(), statistics.getSum(), 1e-5);
        assertEquals(latest.getAvg(), statistics.getAverage(), 1e-5);
        assertEquals(latest.getMax(), statistics.getMax(), 1e-5);
        assertEquals(latest.getMin(), statistics.getMin(), 1e-5);
    }

    private void bulkInsert() {
        final List<Double> amounts = new ArrayList<>();
        amounts.add(15.7D);
        amounts.add(23.1D);
        amounts.add(18.3D);

        // Populate with valid transaction (within 60 seconds)
        for (Double amount : amounts) {
            final long ts = timestamp - (long) (Math.random() * 40 * 1000);
            DefaultRepository.INSTANCE.add(new Transaction(amount, ts));
        }

        final Transaction staleTransaction = new Transaction(1000.0D, timestamp - Transaction.STALE_THRESHOLD);
        DefaultRepository.INSTANCE.add(staleTransaction);

        statistics = amounts.stream().collect(Collectors.summarizingDouble(v -> v));
    }

}
