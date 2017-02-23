package com.mrshish.messenger.cassandra.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageLog {
    public enum Operation {
        CREATE,
        DELETE
    }
    private UUID index;
    private UUID messageUuid;
    private String channel;
    private Instant created;
    private String message;
    private Operation operation;
}
