package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.IUserWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserWalletServiceTest {

    @Mock
    private IUserWalletRepository userWalletRepository;

    @InjectMocks
    private UserWalletService userWalletService;

    private UserWallet testUserWallet;

    @BeforeEach
    void setUp() {
        // Example user wallet data
        testUserWallet = new UserWallet();
        testUserWallet.setUsername("testUser");
        testUserWallet.setBtc(1.0);
        testUserWallet.setEth(10.0);
        testUserWallet.setUsdt(100.0);
    }

    @Test
    void testGetUserByUsername_UserFound() throws Exception {
        when(userWalletRepository.findByUsername("testUser")).thenReturn(Optional.of(testUserWallet));

        UserWallet result = userWalletService.getUserByUsername("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals(1.0, result.getBtc());
        assertEquals(10.0, result.getEth());
        assertEquals(100.0, result.getUsdt());
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        when(userWalletRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UserWalletNotFoundException.class, () -> userWalletService.getUserByUsername("unknownUser"));
    }

}
