package com.mrshish.messenger.core.mapper;

import com.mrshish.messenger.core.model.LoginToken;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class LoginTokenMapper implements ResultSetMapper<LoginToken> {

    @Override
    public LoginToken map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
        return new LoginToken(
            r.getObject("uuid", UUID.class),
            r.getObject("user_uuid", UUID.class),
            r.getTimestamp("created").toInstant(),
            r.getBoolean("is_valid")
        );
    }
}
