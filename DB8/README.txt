Online Bookstore JDBC Application

Main class:
Main

Required environment:
- Java 21
- Maven
- MySQL Connector/J 8.x
- Oracle MySQL HeatWave or MySQL-compatible database

Database setup:
1. Create or select the target database outside these scripts.
2. Run DB8/sql/createschema.sql.
3. Run DB8/sql/views.sql.
4. Run DB8/sql/initdata.sql.

Do not add a USE DATABASE command to the submitted SQL scripts.

Database cleanup:
Run DB8/sql/dropschema.sql to drop views and tables created for this project.

Connection setup:
Create DB8/src/main/resources/db.properties from db.properties.example and set the database URL, username, and password.

Example for SSH tunnel access:
db.url=jdbc:mysql://127.0.0.1:3307/db8_bookstore
db.user=dbadmin
db.password=YOUR_PASSWORD

Run:
1. Open DB8 as the Maven project in IntelliJ IDEA, or run through Maven/Java after compiling.
2. Execute the Main class.

Application menus:
- Main menu supports customer login, customer registration, manager login, and exit.
- Customer menu supports book search, basket, purchase, purchase history, profile analysis, profile viewing, profile update, review write, and review delete.
- Manager menu supports book search, price update, price-change analysis, sales summary, inventory view, stock update, manager role management, manager registration, and manager dismissal.

Demo accounts:
Manager MASTER:
email: mlee051201@gmail.com
password: passwd4mst

Customer:
email: linusT@linux.com
password: linux4u
