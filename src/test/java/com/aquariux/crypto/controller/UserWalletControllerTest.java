package com.aquariux.crypto.controller;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.service.IUserWalletService;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserWalletController.class)
@ContextConfiguration(classes = {UserWalletController.class})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Import(ErrorControllerAdvice.class)
public class UserWalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserWalletService userWalletService;

    @Test
    public void getUser_ReturnsUserWallet() throws Exception {
        String username = "testuser";
        UserWallet userWallet = new UserWallet();
        userWallet.setUsername(username);

        when(userWalletService.getUserByUsername(username)).thenReturn(userWallet);

        mockMvc.perform(get("/user/v1.0/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("User wallet found"))
                .andExpect(jsonPath("$.data.username").value(username));
    }

    @Test
    public void getUser_UserWalletNotFound() throws Exception {
        String username = "unknownuser";
        when(userWalletService.getUserByUsername(username)).thenThrow(new UserWalletNotFoundException("User wallet for username '" + username + "' is not found"));

        mockMvc.perform(get("/user/v1.0/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User wallet for username '" + username + "' is not found"));
    }
}
