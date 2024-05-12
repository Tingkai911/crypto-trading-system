package com.aquariux.crypto.controller;

import com.aquariux.crypto.model.Response;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.service.IUserWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserWalletController {
    private final IUserWalletService userService;

    @GetMapping("/v1.0/{username}")
    public Response<UserWallet> getUser(@PathVariable String username) throws Exception {
        log.info("getUser {}", username);
        Response<UserWallet> response = new Response<>();
        UserWallet userWallet = userService.getUserByUsername(username);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("User wallet found");
        response.setData(userWallet);
        log.info(response.toString());
        return response;
    }
}
