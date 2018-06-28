package co.paikama.stats;

import co.paikama.stats.models.BaseSummary;
import co.paikama.stats.models.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatsApplicationIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private long timestamp;
    private Transaction transaction;

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
    public void testCreateTransactionSuccessfully() throws JsonProcessingException {
        final ResponseEntity<Void> response = restTemplate.postForEntity("/transactions", createRequest(transaction), Void.class);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertFalse(response.hasBody());
    }

    @Test
    public void testCreateTransactionFailed() throws JsonProcessingException {
        transaction.setTimestamp(timestamp - Transaction.STALE_THRESHOLD);
        final ResponseEntity<Void> response = restTemplate.postForEntity("/transactions", createRequest(transaction), Void.class);

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertFalse(response.hasBody());
    }

    @Test
    public void testEmptyStatistics() {
        final ResponseEntity<BaseSummary> response = restTemplate.getForEntity("/statistics", BaseSummary.class);
        final BaseSummary summary = response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(response.hasBody());
        assertEquals(summary.getCount(), 0L);
        assertEquals(summary.getSum(), 0.0D, 1e-5);
        assertEquals(summary.getAvg(), 0.0D, 1e-5);
        assertEquals(summary.getMax(), 0.0D, 1e-5);
        assertEquals(summary.getMin(), 0.0D, 1e-5);
    }

    @Test
    public void testStatisticsSingleTransaction() throws JsonProcessingException {
        restTemplate.postForEntity("/transactions", createRequest(transaction), Void.class);

        final ResponseEntity<BaseSummary> response = restTemplate.getForEntity("/statistics", BaseSummary.class);
        final BaseSummary summary = response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(response.hasBody());
        assertEquals(summary.getCount(), 1L);
        assertEquals(summary.getSum(), 12.3D, 1e-5);
        assertEquals(summary.getAvg(), 12.3D, 1e-5);
        assertEquals(summary.getMax(), 12.3D, 1e-5);
        assertEquals(summary.getMin(), 12.3D, 1e-5);
    }

    @Test
    public void testStatistics() throws JsonProcessingException {
        restTemplate.postForEntity("/transactions", createRequest(transaction), Void.class);

        transaction.setAmount(5.7);
        restTemplate.postForEntity("/transactions", createRequest(transaction), Void.class);

        final ResponseEntity<BaseSummary> response = restTemplate.getForEntity("/statistics", BaseSummary.class);
        final BaseSummary summary = response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(response.hasBody());
        assertEquals(summary.getCount(), 2L);
        assertEquals(summary.getSum(), 18.0D, 1e-5);
        assertEquals(summary.getAvg(), 9.0D, 1e-5);
        assertEquals(summary.getMax(), 12.3D, 1e-5);
        assertEquals(summary.getMin(), 5.7D, 1e-5);
    }

    private HttpEntity<String> createRequest(Transaction transaction) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        final String payload = objectMapper.writeValueAsString(transaction);
        return new HttpEntity<>(payload, headers);
    }

}
