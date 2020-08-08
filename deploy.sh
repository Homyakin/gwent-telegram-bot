mvn package spring-boot:repackage &&
docker build -f Dockerfile -t gwent-bot-img . || true &&
docker stop gwent-bot || true &&
docker rm gwent-bot || true &&
docker run --name gwent-bot -d gwent-bot-img