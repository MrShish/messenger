package com.mrshish.messenger;

import static spark.Spark.exception;
import static spark.Spark.init;
import static spark.Spark.port;

import com.mrshish.messenger.json.JsonTransformer;
import com.mrshish.messenger.resources.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerService {
    private static final JsonTransformer transformer = new JsonTransformer();
    private static final Logger LOG = LoggerFactory.getLogger(MessengerService.class);


    public static void main(String[] args) {
        port(3030);


        MessageResource onBoardingResource = new MessageResource();
        //final DBI dbi = initiateDbi();
        init();
    }



    private static void exceptionHandling() {
        exception(RuntimeException.class, (exception, request, response) -> {
            LOG.error("Unexpected Exception", exception);
            response.status(500);
            response.body("Internal server error");
        });
    }
}
