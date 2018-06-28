package co.paikama.stats.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RunningSummaryTests {

    private RunningSummary runningSummary;

    @Before
    public void setup() {
        runningSummary = new RunningSummary();
    }

    @Test
    public void testSingleTransaction() {
        assertEquals(runningSummary.getCount(), 0L);

        runningSummary.updateAndReturn(new Transaction(12.3D, 1478192204000L));

        assertEquals(runningSummary.getCount(), 1L);
        assertEquals(runningSummary.getSum(), 12.3D, 1e-5);
        assertEquals(runningSummary.getAvg(), 12.3D, 1e-5);
        assertEquals(runningSummary.getMax(), 12.3D, 1e-5);
        assertEquals(runningSummary.getMin(), 12.3D, 1e-5);
    }

    @Test
    public void testMultipleTransaction() {
        assertEquals(runningSummary.getCount(), 0L);

        runningSummary.updateAndReturn(new Transaction(12.3D, 1478192204000L));
        runningSummary.updateAndReturn(new Transaction(5.7D, 1478192204000L));

        assertEquals(runningSummary.getCount(), 2L);
        assertEquals(runningSummary.getSum(), 18.0D, 1e-5);
        assertEquals(runningSummary.getAvg(), 9.0D, 1e-5);
        assertEquals(runningSummary.getMax(), 12.3D, 1e-5);
        assertEquals(runningSummary.getMin(), 5.7D, 1e-5);
    }



}
