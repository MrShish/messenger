package com.mrshish.messenger.cassandra.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.fasterxml.uuid.Generators;
import com.mrshish.messenger.cassandra.CassandraSession;
import com.mrshish.messenger.cassandra.model.Message;
import com.mrshish.messenger.cassandra.model.MessageLog;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessageDao {
    private final CassandraSession cassandraSession;
    private final MessageLogDao messageLogDao;
    private static final String TABLE_MESSAGE = "message";
    private static final String COLUMN_MESSAGE_UUID = "uuid";
    private static final String COLUMN_CHANNEL = "channel";
    private static final String COLUMN_LOG_UUID = "log_uuid";
    private static final String COLUMN_CREATED = "created";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IS_DELETED = "is_deleted";

    public MessageDao(
        CassandraSession cassandraSession,
        MessageLogDao messageLogDao) {
        this.cassandraSession = cassandraSession;
        this.messageLogDao = messageLogDao;
    }

    public Message store(Message message) {
        UUID newLogIndex = Generators.timeBasedGenerator().generate();
        final Statement insertMessage  = QueryBuilder
            .insertInto(TABLE_MESSAGE)
            .value(COLUMN_CHANNEL, message.getChannel())
            .value(COLUMN_MESSAGE_UUID, message.getUuid())
            .value(COLUMN_LOG_UUID, newLogIndex)
            .value(COLUMN_CREATED, message.getCreated())
            .value(COLUMN_CONTENT, message.getMessage())
            .value(COLUMN_IS_DELETED, message.isDeleted());
        execute(insertMessage);

        MessageLog.Operation operation =
            message.isDeleted() ? MessageLog.Operation.DELETE : MessageLog.Operation.CREATE;

        messageLogDao.log(operation, newLogIndex, message);
        return message;
    }

    public List<Message> getMessagesByUuids(
        final String channel,
        final List<UUID> uuids
    ) {
        if (uuids.isEmpty()) {
            return Collections.emptyList();
        }
        final Statement stmt = QueryBuilder
            .select()
            .from(TABLE_MESSAGE)
            .where(eq(COLUMN_CHANNEL, channel))
            .and(in(COLUMN_MESSAGE_UUID, uuids));

        return execute(stmt)
            .all()
            .stream()
            .map(MessageDao::rowToMessage)
            .collect(Collectors.toList());
    }

    private ResultSet execute(Statement stmt) {
        return cassandraSession.getSession().execute(stmt);
    }

    private static Message rowToMessage(Row row) {
        return new Message(
            row.getUUID(COLUMN_MESSAGE_UUID),
            row.getString(COLUMN_CHANNEL),
            row.getTimestamp(COLUMN_CREATED).toInstant(),
            row.getString(COLUMN_CONTENT),
            row.getBool(COLUMN_IS_DELETED)
        );
    }

}
