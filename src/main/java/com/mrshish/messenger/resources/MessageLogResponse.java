package com.mrshish.messenger.resources;

import com.mrshish.messenger.cassandra.model.MessageLog;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageLogResponse {
    private List<MessageLog> messageLog;
    private UUID lastLogIndex;
}
