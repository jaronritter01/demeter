for /f "delims=[] tokens=2" %a in ('ping -4 -n 1 %ComputerName% ^| findstr [') do setx NetworkIP %a
docker build --build-arg db_name=$env:db_name --build-arg db_username=$env:db_username --build-arg db_password=$env:db_password -t demeter-image .
docker run -p 8080:8080 demeter-image .