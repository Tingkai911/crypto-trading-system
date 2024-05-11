package com.aquariux.crypto.controller;

import com.aquariux.crypto.model.Response;
import com.aquariux.crypto.model.TradeRequest;
import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final ITransactionService transactionService;

    @GetMapping("/v1.0/all/{username}")
    public Response<List<Transaction>> getAllTransactions(@PathVariable String username) throws Exception {
        Response<List<Transaction>> response = new Response<>();
        List<Transaction> transactions = transactionService.getTransactionsByUsername(username);
        response.setCode(200);
        response.setMessage("All transactions for username: " + username);
        response.setData(transactions);
        return response;
    }

    @PostMapping("/v1.0/create")
    public Response<Transaction> createTransaction(@Valid @RequestBody TradeRequest tradeRequest) throws Exception {

        Response<Transaction> response = new Response<>();
        // TODO
        response.setCode(200);
        response.setMessage("Transaction Successful");

        return response;
    }
}
