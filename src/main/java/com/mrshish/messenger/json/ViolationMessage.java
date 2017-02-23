package com.mrshish.messenger.json;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViolationMessage {
    private String propertyName;
    private String message;
}
