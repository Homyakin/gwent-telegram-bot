mvn package spring-boot:repackage &&
docker-compose build || true &&
docker-compose stop || true &&
docker-compose up