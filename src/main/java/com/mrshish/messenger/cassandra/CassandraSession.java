package com.mrshish.messenger.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraSession {

    private final Logger log = LoggerFactory.getLogger(CassandraSession.class);
    private final Cluster cluster;
    private ListenableFuture<Session> session;

    private static final String KEYSPACE = "messenger";

    private static final String cassandraHost = "localhost";

    public CassandraSession() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL,  3, 5)
            .setConnectionsPerHost(HostDistance.REMOTE, 1, 2);
        cluster = Cluster
            .builder()
            .withCredentials("cassandra", "cassandra")
            .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM))
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withReconnectionPolicy(new ExponentialReconnectionPolicy(10L, 1000L))
            .withLoadBalancingPolicy(new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
            .addContactPoints(cassandraHost)
            .withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(1000, 2))
            .withPoolingOptions(poolingOptions)
            .build();
        cluster.getConfiguration().getCodecRegistry()
            .register(InstantCodec.instance);
    }

    public void start() {
        log.info("Starting CassandraSession for keyspace {}", KEYSPACE);
        session = cluster.connectAsync(KEYSPACE);

        Futures.addCallback(session, new FutureCallback<Session>() {
            @Override
            public void onSuccess(Session result) {

            }

            @Override
            public void onFailure(Throwable t) {
                log.error("Failed to start CassandraSession for keyspace {}", KEYSPACE, t);
            }
        });
    }


    public void stop() throws Exception {
        log.info("Stopping CassandraSession for keyspace {}", KEYSPACE);
        cluster.closeAsync();
    }

    public Session getSession() {
        try {
            return session.get();
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            throw new IllegalStateException(
                "Cannot get session before CassandraSession is started. "
                    + "Make sure you call start() on " + this.getClass().getSimpleName()
                    + " before calling getSession()"
            );
        }
    }
}
