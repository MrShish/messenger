package com.mrshish.messenger;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.post;

import com.datastax.driver.core.Cluster;
import com.mrshish.messenger.cassandra.CassandraSession;
import com.mrshish.messenger.cassandra.dao.MessageDao;
import com.mrshish.messenger.cassandra.dao.MessageLogDao;
import com.mrshish.messenger.json.JsonTransformer;
import com.mrshish.messenger.resources.MessageLogResource;
import com.mrshish.messenger.resources.MessageResource;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerService {
    private static final JsonTransformer transformer = new JsonTransformer();
    private static final Logger LOG = LoggerFactory.getLogger(MessengerService.class);
    private Cluster cluster;


    public static void main(String[] args) {
        port(3030);
        exceptionHandling();
        CassandraSession session;
        try {
            session = startSession();
        }catch (InterruptedException e) {
            throw new RuntimeException("Could not initiate cassandra", e);
        }

        MessageLogDao messageLogDao = new MessageLogDao(session);
        MessageDao messageDao = new MessageDao(session, messageLogDao);
        MessageResource messageResource = new MessageResource(messageDao);
        MessageLogResource messageLogResource = new MessageLogResource(messageLogDao);

        post("/messages/:channel", (req, res) -> messageResource.createMessage(req.params(":channel"), req.queryParams("message")), transformer);

        delete("/messages/:channel", (req, res) ->
            messageResource.deleteMessages(req.params(":channel"), req.queryParamsValues("messageUuid")), transformer);


        get("/messages/:channel", (req, res) -> messageLogResource.getMessages(
            req.params(":channel"),
            null != req.queryParams("lastLogIndex") ? UUID.fromString(req.queryParams("lastLogIndex")) : null,
            null != req.queryParams("limit") ? Integer.parseInt(req.queryParams("limit")) : null
        ), transformer);

        init();
    }

    private static CassandraSession startSession() throws InterruptedException {
        try {
            CassandraSession session = new CassandraSession();
            session.start();
            LOG.info("Connected to cassandra");
            return session;
        } catch (Exception e) {
            LOG.warn("Could not initiate Cassandra connection, trying again in 20 sec", e);
            Thread.sleep(20000);
            startSession();
        }
        return null;
    }

    private static void exceptionHandling() {
        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.status(422);
            response.body("Bad request");
        });
        exception(RuntimeException.class, (exception, request, response) -> {
            LOG.error("Unexpected Exception", exception);
            response.status(500);
            response.body("Internal server error");
        });

    }
}
