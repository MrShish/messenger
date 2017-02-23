package com.mrshish.messenger.core.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    private UUID uuid;
    private String email;
    private String passwordHash;
    private Instant created;
}
