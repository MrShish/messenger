package com.mrshish.messenger.json;

import java.util.Set;

public class JsonValidatinException extends RuntimeException{
    private final Set<ViolationMessage> errors;

    public JsonValidatinException(Set<ViolationMessage> errors) {
        this.errors = errors;
    }

    public Set<ViolationMessage> getVioalations() {
        return errors;
    }
}

