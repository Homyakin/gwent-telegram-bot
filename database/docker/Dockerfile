FROM mysql:8.0.21

ENV MYSQL_ROOT_PASSWORD=12345
ENV MYSQL_DATABASE=gwent

COPY sql/create-db.sql /docker-entrypoint-initdb.d/

EXPOSE 3306

VOLUME /var/lib/mysql
