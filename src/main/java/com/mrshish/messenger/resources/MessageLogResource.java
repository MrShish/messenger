package com.mrshish.messenger.resources;

import com.mrshish.messenger.cassandra.dao.MessageLogDao;
import com.mrshish.messenger.cassandra.model.MessageLog;
import java.util.List;
import java.util.UUID;

public class MessageLogResource {

    private final MessageLogDao messageLogDao;

    public MessageLogResource(MessageLogDao messageLogDao) {
        this.messageLogDao = messageLogDao;
    }

    public MessageLogResponse getMessages(
        final String channel,
        final UUID fromLogIndex,
        final Integer limitParam
    ) {
        final List<MessageLog> messageList;
        int limit = null == limitParam ? 100 : limitParam;
        if (null == fromLogIndex) {
            messageList = messageLogDao.getMessageLog(channel, limit);
        } else {
            messageList = messageLogDao.getMessageLogFromIndex(channel, fromLogIndex, limit);
        }

        return new MessageLogResponse(
            messageList,
            messageList.isEmpty() ? fromLogIndex : messageList.get(messageList.size() - 1).getIndex()
        );
    }

}
