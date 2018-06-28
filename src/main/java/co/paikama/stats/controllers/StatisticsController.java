package co.paikama.stats.controllers;

import co.paikama.stats.models.BaseSummary;
import co.paikama.stats.models.Transaction;
import co.paikama.stats.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/")
@RestController
public class StatisticsController {

    private final StatisticsService service;

    @Autowired
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @PostMapping(value = "transactions", consumes = "application/json")
    public ResponseEntity transaction(@Valid @RequestBody Transaction transaction) {
        if (transaction.isValid()) {
            service.add(transaction);
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(value = "statistics", produces = "application/json")
    public BaseSummary statistics() {
        return service.latest(60);
    }

}
