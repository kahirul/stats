package co.paikama.stats.controllers;

import co.paikama.stats.TestHelper;
import co.paikama.stats.models.BaseSummary;
import co.paikama.stats.models.Transaction;
import co.paikama.stats.services.StatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static co.paikama.stats.models.Transaction.STALE_THRESHOLD;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    private long timestamp;
    private Transaction transaction;
    private BaseSummary baseSummary;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        timestamp = Instant.now().toEpochMilli();
        transaction = new Transaction(12.3D, timestamp);
        baseSummary = new BaseSummary(1134.6D, 567.3D, 1122.3D, 12.3D, 2L);
    }

    @After
    public void teardown() {
        TestHelper.resetSingleton();
    }

    @Test
    public void testCreateTransactionSuccessfully() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateTransactionFailed() throws Exception {
        transaction.setTimestamp(timestamp - STALE_THRESHOLD);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetStatistics() throws Exception {
        given(statisticsService.latest(60)).willReturn(baseSummary);

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.sum", is(1134.6D)))
                .andExpect(jsonPath("$.avg", is(567.3D)))
                .andExpect(jsonPath("$.max", is(1122.3D)))
                .andExpect(jsonPath("$.min", is(12.3D)));
    }
}
