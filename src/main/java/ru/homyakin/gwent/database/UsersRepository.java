package ru.homyakin.gwent.database;

import io.vavr.control.Either;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.homyakin.gwent.models.errors.DatabaseError;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.UserNotRegistered;

@Component
public class UsersRepository {
    private static final Logger logger = LoggerFactory.getLogger(UsersRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public UsersRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Either<EitherError, String> registerProfile(String gwentProfileName, Long userId) {
        String sql = "INSERT INTO gwent.users (user_id, gwent_profile_name)" +
            "VALUES (?, ?);";
        try {
            jdbcTemplate.update(
                sql,
                userId,
                gwentProfileName
            );
            return Either.right(String.format("Ваш профиль теперь привязан к %s", gwentProfileName));
        } catch (DuplicateKeyException e) {
            return updateProfile(gwentProfileName, userId);
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            return Either.left(new DatabaseError());
        }
    }

    public Either<EitherError, String> getProfileById(Long userId) {
        String sql = "SELECT gwent_profile_name FROM gwent.users WHERE user_id = ?";
        try {
            List<String> result = jdbcTemplate.query(
                sql,
                new Object[]{userId},
                (rs, rowNum) -> rs.getString("gwent_profile_name")
            );
            if (result.size() == 0) {
                return Either.left(new UserNotRegistered("Зарегистрируйся или добавь имя через пробел после команды"));
            }
            return Either.right(result.get(0));
        } catch (Exception e) {
            logger.error("Unexpected error selecting profile", e);
            return Either.left(new DatabaseError());
        }
    }

    private Either<EitherError, String> updateProfile(String gwentProfileName, Long userId) {
        String sql = "UPDATE gwent.users SET gwent_profile_name = ?" +
            "WHERE user_id = ?";
        try {
            jdbcTemplate.update(
                sql,
                gwentProfileName,
                userId
            );
            return Either.right(String.format("Ваш профиль был обновлён и теперь привязан к %s", gwentProfileName));
        } catch (Exception e) {
            logger.error("Unexpected error during updating registration", e);
            return Either.left(new DatabaseError());
        }
    }
}
