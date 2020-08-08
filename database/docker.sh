docker build -f Dockerfile -t gwent-db-img . &&
docker stop gwent-db || true &&
docker rm gwent-db || true &&
docker run --name gwent-db -p 3307:3306  -v $(pwd)/gwent-db:/var/lib/mysql -d gwent-db-img
