package com.mrshish.messenger.json;

import static com.mrshish.messenger.json.JsonMapper.read;
import static com.mrshish.messenger.json.JsonMapper.write;
import static spark.Spark.halt;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.validation.ValidationException;
import spark.Request;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    private final static ObjectMapper mapper = JsonMapper.get();

    @Override
    public String render(Object model) throws Exception {
        return write(model);
    }

    public static <T> T parse(Request req, final Class<T> t) {
        try {
            return JsonValidator.validate(read(req.body(), t));
        } catch (ValidationException e){
            halt(422, e.getMessage());
        }
        throw new RuntimeException("Could not provide a entity");
    }
}
