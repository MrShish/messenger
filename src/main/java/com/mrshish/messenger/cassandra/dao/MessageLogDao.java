package com.mrshish.messenger.cassandra.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.asc;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.mrshish.messenger.cassandra.CassandraSession;
import com.mrshish.messenger.cassandra.model.Message;
import com.mrshish.messenger.cassandra.model.MessageLog;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessageLogDao {

    private final CassandraSession cassandraSession;
    private static final String TABLE_MESSAGE_LOG = "message_log";
    private static final String COULUMN_UUID = "uuid";
    private static final String COLUMN_CHANNEL = "channel";
    private static final String COLUMN_MESSAGE_UUID = "message_uuid";
    private static final String COLUMN_CREATED = "created";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_OPERATION = "operation";


    public MessageLogDao(
        final CassandraSession cassandraSession
    ) {
        this.cassandraSession = cassandraSession;
    }

    public void log(
        final MessageLog.Operation operation,
        final UUID index,
        final Message message) {
        final Statement insertMessageEvent = QueryBuilder
            .insertInto(TABLE_MESSAGE_LOG)
            .value(COLUMN_CHANNEL, message.getChannel())
            .value(COLUMN_MESSAGE_UUID, message.getUuid())
            .value(COULUMN_UUID, index)
            .value(COLUMN_CREATED, message.getCreated())
            .value(COLUMN_CONTENT, message.getMessage())
            .value(COLUMN_OPERATION, operation.toString());
        execute(insertMessageEvent);
    }

    public List<MessageLog> getMessageLog(
        final String channel,
        final int limit
    ) {
        Statement stmt = QueryBuilder
            .select()
            .from(TABLE_MESSAGE_LOG)
            .where(eq(COLUMN_CHANNEL, channel))
            .orderBy(asc(COULUMN_UUID))
            .limit(limit);

        return execute(stmt)
            .all()
            .stream()
            .map(MessageLogDao::rowToLog)
            .collect(Collectors.toList());
    }

    public List<MessageLog> getMessageLogFromIndex(
        final String channel,
        final UUID fromUuid,
        final int limit
    ) {
        Statement stmt = QueryBuilder
            .select()
            .from(TABLE_MESSAGE_LOG)
            .where(eq(COLUMN_CHANNEL, channel))
            .and(gt(COULUMN_UUID, fromUuid))
            .orderBy(asc(COULUMN_UUID))
            .limit(limit);

        return execute(stmt)
            .all()
            .stream()
            .map(MessageLogDao::rowToLog)
            .collect(Collectors.toList());
    }

    private ResultSet execute(Statement stmt) {
        return cassandraSession.getSession().execute(stmt);
    }

    private static MessageLog rowToLog(Row row) {
        return new MessageLog(
            row.getUUID(COULUMN_UUID),
            row.getUUID(COLUMN_MESSAGE_UUID),
            row.getString(COLUMN_CHANNEL),
            row.getTimestamp(COLUMN_CREATED).toInstant(),
            row.getString(COLUMN_CONTENT),
            MessageLog.Operation.valueOf(row.getString(COLUMN_OPERATION))
        );
    }
}
