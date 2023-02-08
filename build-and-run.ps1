docker build --build-arg db_name=$env:db_name --build-arg db_username=$env:db_username --build-arg db_password=$env:db_password -t demeter-image .
docker run -p 8080:8080 demeter-image .