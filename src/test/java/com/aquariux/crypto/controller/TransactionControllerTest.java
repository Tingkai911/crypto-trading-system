package com.aquariux.crypto.controller;

import com.aquariux.crypto.exception.TransactionException;
import com.aquariux.crypto.exception.TransactionNotFoundException;
import com.aquariux.crypto.model.TradeRequest;
import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.service.ITransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = {TransactionController.class})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Import(ErrorControllerAdvice.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllTransactions_Success() throws Exception {
        String username = "testuser";
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());

        when(transactionService.getTransactionsByUsername(username)).thenReturn(transactions);

        mockMvc.perform(get("/transaction/v1.0/all/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("All transactions for username: " + username))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllTransactions_Failure() throws Exception {
        when(transactionService.getTransactionsByUsername("user1")).thenThrow(new TransactionNotFoundException("Error fetching transactions"));

        mockMvc.perform(get("/transaction/v1.0/all/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Error fetching transactions"));
    }

    @Test
    public void createTransaction_BidSuccess() throws Exception {
        TradeRequest tradeRequest = new TradeRequest("testuser", "AAPL", "BID", 10D);
        Transaction transaction = new Transaction();

        when(transactionService.bid(tradeRequest.getUsername(), tradeRequest.getSymbol(), tradeRequest.getAmount())).thenReturn(transaction);

        mockMvc.perform(post("/transaction/v1.0/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void createTransaction_AskSuccess() throws Exception {
        TradeRequest tradeRequest = new TradeRequest("testuser", "AAPL", "ASK", 5D);
        Transaction transaction = new Transaction();

        when(transactionService.ask(tradeRequest.getUsername(), tradeRequest.getSymbol(), tradeRequest.getAmount())).thenReturn(transaction);

        mockMvc.perform(post("/transaction/v1.0/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Transaction Successful"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void createTransaction_InvalidType() throws Exception {
        TradeRequest invalidTradeRequest = new TradeRequest("testuser", "AAPL", "INVALID_TYPE", 10D);

        mockMvc.perform(post("/transaction/v1.0/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidTradeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testGetPaginatedTransactions_Success() throws Exception {
        Transaction transaction = new Transaction(); // Replace with actual transaction fields
        List<Transaction> transactionsList = Arrays.asList(transaction);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionsPage = new PageImpl<>(transactionsList, pageable, 1);
        when(transactionService.getTransactionsByUsername("user1", pageable)).thenReturn(transactionsPage);

        mockMvc.perform(get("/transaction/v1.0/paginated/user1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Paginated transactions for username: user1"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0]").exists());
    }

    @Test
    void testGetPaginatedTransactions_Failure() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionService.getTransactionsByUsername("user1", pageable))
                .thenThrow(new TransactionNotFoundException("Error fetching transactions"));

        mockMvc.perform(get("/transaction/v1.0/paginated/user1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Error fetching transactions"));
    }
}

