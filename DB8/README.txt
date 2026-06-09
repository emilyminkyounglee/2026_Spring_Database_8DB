Online Bookstore JDBC Application

Main class:
Main

Required environment:
- Java 21
- MySQL-compatible database
- MySQL Connector/J 8.x is already included in the submitted executable jar.
- OpenSSH client for SSH tunnel access

Submitted files:
- DB8-1.0-SNAPSHOT.jar
- createschema.sql
- views.sql
- initdata.sql
- dropschema.sql
- README.txt
- src/main/java source files
- src/main/resources/db.properties
- db8_dev_key

Database setup:
The submitted jar is configured to connect to the team demo database through an SSH tunnel.
The SQL scripts are also included so the database can be recreated if needed.

To recreate the database manually:
1. Create or select the target database outside these scripts.
2. Run createschema.sql.
3. Run views.sql.
4. Run initdata.sql.

Do not add a USE DATABASE command to the submitted SQL scripts.

Database cleanup:
Run dropschema.sql to drop views and tables created for this project.

Demo database connection information:
Database name: db8_bookstore
Database user: dbadmin
Database password: Thisisdbbookstore1230!

JDBC connection used by the submitted jar:
db.url=jdbc:mysql://127.0.0.1:3307/db8_bookstore
db.user=dbadmin
db.password=Thisisdbbookstore1230!

SSH tunnel setup on macOS or Linux:
The submitted key file is named db8_dev_key.

1. Open Terminal.
2. Move to the extracted TeamProject folder.
3. Copy the submitted key file to the SSH folder:

   mkdir -p ~/.ssh
   cp db8_dev_key ~/.ssh/db8_dev_key
   chmod 600 ~/.ssh/db8_dev_key

4. Open or create the SSH config file:

   nano ~/.ssh/config

5. Add the following SSH configuration:

   Host db8-tunnel
       HostName 134.185.99.179
       User ubuntu
       IdentityFile ~/.ssh/db8_dev_key
       LocalForward 3307 10.0.0.18:3306
       ServerAliveInterval 60

6. Save the file and start the SSH tunnel:

   ssh -fN db8-tunnel

If there is no error message, the tunnel is running.
If "Address already in use" appears, the tunnel is already running and the application can still be executed.

SSH tunnel setup on Windows:
The submitted key file is named db8_dev_key.
These commands can be run in PowerShell or Windows Terminal.

1. Open PowerShell or Windows Terminal.
2. Move to the extracted TeamProject folder.
3. Copy the submitted key file to the SSH folder:

   mkdir $env:USERPROFILE\.ssh
   copy db8_dev_key $env:USERPROFILE\.ssh\db8_dev_key

4. Open or create the SSH config file:

   notepad $env:USERPROFILE\.ssh\config

5. Add the following SSH configuration:

   Host db8-tunnel
       HostName 134.185.99.179
       User ubuntu
       IdentityFile ~/.ssh/db8_dev_key
       LocalForward 3307 10.0.0.18:3306
       ServerAliveInterval 60

6. Save the file and start the SSH tunnel:

   ssh -N db8-tunnel

Keep this terminal window open while running the Java application.

Run the executable jar:
1. Open Terminal.
2. Start the SSH tunnel if it is not already running.
   On macOS or Linux:

   ssh -fN db8-tunnel

   On Windows:

   ssh -N db8-tunnel

   Keep this Windows terminal open and run the jar in another terminal window.

3. Move to the extracted TeamProject folder in another terminal window.
4. Execute the jar file:

   java -jar DB8-1.0-SNAPSHOT.jar

Expected first output:
Connected to database: db8_bookstore

===== Online Book Store =====
1. Customer Login
2. Register new customer
3. Manager Login
0. Exit

Optional MySQL terminal access through the SSH tunnel:

mysql -h 127.0.0.1 -P 3307 -u dbadmin -p db8_bookstore

Password:
Thisisdbbookstore1230!

Application menus:
- Main menu supports customer login, customer registration, manager login, and exit.
- Customer menu supports book search, basket, purchase, purchase history, profile analysis, profile viewing, profile update, review write, and review delete.
- Manager menu supports book search, price update, price-change analysis, sales summary, inventory view, stock update, manager role management, manager registration, and manager dismissal.

Demo accounts:
Manager MASTER:
email: mlee051201@gmail.com
password: passwd4mst

Manager SALES_ANALYSIS:
email: acriste1890@gmail.com
password: agathaC

Customer:
email: linusT@linux.com
password: linux4u
