package com.mrshish.messenger.resources;

import com.mrshish.messenger.cassandra.model.Message;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeletedMessagesResponse {
    private final List<Message> deletedMessages;
}
