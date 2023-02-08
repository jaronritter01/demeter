docker build --build-arg db_name=${db_name} --build-arg db_username=${db_username} --build-arg db_password=${db_password} -t demeter-image .
docker run -p 8080:8080 demeter-image .