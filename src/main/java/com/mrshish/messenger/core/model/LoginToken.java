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
public class LoginToken {
    private UUID uuid;
    private UUID userUuid;
    private Instant created;
    private boolean isValid;
}
