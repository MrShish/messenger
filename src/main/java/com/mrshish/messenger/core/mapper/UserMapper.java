package com.mrshish.messenger.core.mapper;

import com.mrshish.messenger.core.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserMapper implements ResultSetMapper<User> {

    @Override
    public User map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
        return new User(
            r.getObject("uuid", UUID.class),
            r.getString("email"),
            r.getString("password_hash"),
            r.getTimestamp("created").toInstant()
        );
    }
}
