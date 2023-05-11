# Demeter
This is a REST api for the MyKitchen app. It provides all the user/data functionalities of the MyKitchen Application. It
only accepts JSON data. For populating the database see the Consumer: https://github.com/jaronritter01/adephagia

### Local Setup
1. Environment Setup
   1. Set a permanent environment variable `db_name` to the name of the database you'd like to use
   2. Set a permanent environment variable `db_username` to your database's username
   3. Set a permanent environment variable `db_password` to your database's password
2. Run the app locally using IntelliJ or the gradle commands

### Deployment Notes
- Env Vars have to be set permanently
- This requires that you have Postgres install and running locally