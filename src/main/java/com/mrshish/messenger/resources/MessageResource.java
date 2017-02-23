package com.mrshish.messenger.resources;

import static java.util.stream.Collectors.toList;

import com.fasterxml.uuid.Generators;
import com.mrshish.messenger.cassandra.dao.MessageDao;
import com.mrshish.messenger.cassandra.model.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class MessageResource {

    private final MessageDao messageDao;

    public MessageResource(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public Message createMessage(String channel, String messageString) {
        UUID uuid = Generators.timeBasedGenerator().generate();
        Message message = new Message(
            uuid,
            channel,
            Instant.now(),
            messageString,
            false
        );
        return messageDao.store(message);
    }

    public DeletedMessagesResponse deleteMessages(String channel, String[] uuidsToDelete) {
        List<UUID> uuids = Stream.of(uuidsToDelete).map(UUID::fromString).collect(toList());
        List<Message> deletedMessages =
            messageDao
            .getMessagesByUuids(channel, uuids)
            .stream()
            .filter(m -> !m.isDeleted())
            .map(m -> new Message(
                m.getUuid(),
                m.getChannel(),
                m.getCreated(),
                m.getMessage(),
                true
            ))
            .map(messageDao::store)
            .collect(toList());
        return new DeletedMessagesResponse(deletedMessages);
    }
}
