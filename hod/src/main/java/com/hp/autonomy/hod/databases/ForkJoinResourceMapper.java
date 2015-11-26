/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.hod.databases;

import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import com.hp.autonomy.hod.fields.IndexFieldsService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ResourceMapper that uses a {@link ForkJoinPool} to perform mapping in parallel. If using the internal ForkJoinPool,
 * the {@link #destroy} method should be called when the ResourceMapper is finished with.
 */
@Slf4j
public class ForkJoinResourceMapper extends AbstractResourceMapper {
    private final ForkJoinPool forkJoinPool;

    /**
     * Constructs a new ForkJoinResourceMapper with the given IndexFieldsService. The {@link #destroy} method should be
     * called when using this constructor when the ForkJoinResourceMapper is finished with.
     * @param indexFieldsService The IndexFieldsService to use
     */
    public ForkJoinResourceMapper(final IndexFieldsService indexFieldsService) {
        this(indexFieldsService, new ForkJoinPool());
    }

    /**
     * Constructs a new ForkJoinResourceMapper with the given IndexFieldsService and ForkJoinPool.
     * @param indexFieldsService The IndexFieldsService to use
     * @param forkJoinPool The ForkJoinPool to use
     */
    public ForkJoinResourceMapper(final IndexFieldsService indexFieldsService, final ForkJoinPool forkJoinPool) {
        super(indexFieldsService);

        this.forkJoinPool = forkJoinPool;
    }

    /**
     * Shuts down the ForkJoinPool.
     */
    public void destroy() {
        forkJoinPool.shutdown();
    }

    @Override
    public Set<Database> map(final TokenProxy<?, TokenType.Simple> tokenProxy, final Set<String> resourceNames, final String domain) throws HodErrorException {
        final DatabaseTask privateTask = new DatabaseTask(tokenProxy, new ArrayList<>(resourceNames), domain);

        forkJoinPool.submit(privateTask);

        try {
            return privateTask.get();
        } catch (final InterruptedException e) {
            // preserve interrupted status
            Thread.currentThread().interrupt();
            // anything we return may be incomplete
            throw new IllegalStateException("Interrupted while waiting for parametric fields", e);
        } catch (final ExecutionException e) {
            Throwable currentException = e;

            // look for HodErrorException in the cause chain so we can throw it
            while (currentException != null && currentException != currentException.getCause()) {
                if (currentException instanceof HodErrorException) {
                    throw (HodErrorException) currentException;
                }

                currentException = currentException.getCause();
            }

            throw new IllegalStateException("Error occurred executing task", e);
        }
    }

    private class DatabaseTask extends RecursiveTask<Set<Database>> {

        private static final long serialVersionUID = -8514152774998360171L;
        private final TokenProxy<?, TokenType.Simple> tokenProxy;
        private final List<String> input;
        private final String domain;

        private DatabaseTask(final TokenProxy<?, TokenType.Simple> tokenProxy, final List<String> input, final String domain) {
            this.tokenProxy = tokenProxy;
            this.input = input;
            this.domain = domain;
        }

        @Override
        protected Set<Database> compute() {
            if (input.isEmpty()) {
                return Collections.emptySet();
            }
            else if(input.size() == 1) {
                final String resourceName = input.get(0);

                final Database database;

                try {
                    database = databaseForResource(tokenProxy, resourceName, domain);
                } catch (final HodErrorException e) {
                    completeExceptionally(e);
                    // this value is irrelevant as completeExceptionally will throw a RuntimeException
                    return null;
                }

                return Collections.singleton(database);
            }
            else {
                final int middle = (input.size() / 2); // truncation is OK

                final DatabaseTask left =  new DatabaseTask(tokenProxy, input.subList(0, middle), domain);
                left.fork();

                final DatabaseTask right = new DatabaseTask(tokenProxy, input.subList(middle, input.size()), domain);
                right.fork();

                final Set<Database> result = new HashSet<>();
                result.addAll(left.join());
                result.addAll(right.join());

                return result;
            }
        }
    }

}
