package com.contoso.adviser.utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AsyncInvocationService {

    private final Logger log;

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CompletableFuture<Void> run(Runnable procedure) {
        try {
            procedure.run();
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.severe("Failed to execute asynchronous task: %s".formatted(e.getMessage()));
            throw new IllegalStateException("Failed to execute asynchronous task", e);
        }
    }
}
