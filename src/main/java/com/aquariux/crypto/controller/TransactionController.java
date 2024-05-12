package com.aquariux.crypto.controller;

import com.aquariux.crypto.exception.TransactionException;
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
        log.info("getAllTransactions {}", username);
        Response<List<Transaction>> response = new Response<>();
        List<Transaction> transactions = transactionService.getTransactionsByUsername(username);
        response.setCode(200);
        response.setMessage("All transactions for username: " + username);
        response.setData(transactions);
        log.info(response.toString());
        return response;
    }

    @PostMapping("/v1.0/create")
    public Response<Transaction> createTransaction(@Valid @RequestBody TradeRequest tradeRequest) throws Exception {
        log.info("createTransaction {}", tradeRequest);
        Response<Transaction> response = new Response<>();

        Transaction transaction;
        // Sell/Bid
        if (tradeRequest.getType().equals("BID")) {
            transaction = transactionService.bid(tradeRequest.getUsername(), tradeRequest.getSymbol(), tradeRequest.getAmount());
        }
        // Buy/Ask
        else if (tradeRequest.getType().equals("ASK")) {
            transaction = transactionService.ask(tradeRequest.getUsername(), tradeRequest.getSymbol(), tradeRequest.getAmount());
        }
        else {
            throw new TransactionException("Invalid transaction type");
        }

        response.setCode(200);
        response.setMessage("Transaction Successful");
        response.setData(transaction);
        log.info(response.toString());
        return response;
    }
}
