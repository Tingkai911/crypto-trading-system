package com.aquariux.crypto.controller;

import com.aquariux.crypto.model.Response;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.service.impl.UserWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserWalletController {
    private final UserWalletService userService;

    @GetMapping("/v1.0/{username}")
    public Response<UserWallet> getUser(@PathVariable String username) throws Exception {
        Response<UserWallet> response = new Response<>();
        UserWallet userWallet = userService.getUserByUsername(username);
        response.setCode(200);
        response.setMessage("User wallet found");
        response.setData(userWallet);
        return response;
    }
}
