package co.paikama.stats.models;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionTests {

    private Transaction transaction;

    @Before
    public void setup() {
        transaction = new Transaction(12.3D, Instant.now().toEpochMilli());
    }

    @Test
    public void testTransactionIsValid() {
        assertTrue(transaction.isValid());
    }

    @Test
    public void testTransactionIsInvalid() {
        transaction.setTimestamp(transaction.getTimestamp() - Transaction.STALE_THRESHOLD);
        assertFalse(transaction.isValid());
    }


}
