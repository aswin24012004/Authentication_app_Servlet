package com.atm.util;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TokenUtil {

    // Simulating Async Token Generation
    public static CompletableFuture<String> generateTokenAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate delay
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return UUID.randomUUID().toString();
        });
    }
}
