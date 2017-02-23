package com.mrshish.messenger.cassandra.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private final UUID uuid;
    private final String channel;
    private final Instant created;
    private final String message;
    private final boolean deleted;
}
