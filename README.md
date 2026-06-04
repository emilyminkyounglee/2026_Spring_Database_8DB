# DB8

2026년 1학기 Prof Min Soo Lee Database 수업의 8데베팀 깃허브입니다.

# Clone Repo

```bash
git clone <repository-url>
```

# Development Environment

| Category | Tool |
|---|---|
| Java | Java 21 |
| IDE | IntelliJ IDEA |
| Build Tool | Maven |
| DBMS | Oracle MySQL HeatWave |
| JDBC Driver | MySQL Connector/J 8.x |

# Project Structure

```text
DB8/src/main/java
dao/       -> SQL queries
model/     -> Entity classes
service/   -> Business logic and transactions
menu/      -> Console UI
util/      -> DB connection, input helper, and menu printing
Main.java  -> Application entry point
```

# GitHub Rules

- Use feature branches
- Do not directly push to main
- Pull before push
- Use meaningful commit messages

Example:

```text
feat: add customer menu
fix: rollback transaction bug
docs: update README
```

# How to Run

## Database Setup

Create or select the target MySQL database first.

Do not include `USE DATABASE` commands inside submitted SQL scripts.

Run the SQL scripts in this order:

```text
DB8/sql/dropschema.sql
DB8/sql/createschema.sql
DB8/sql/views.sql
DB8/sql/initdata.sql
```

## DB Connection Setup

Create `db.properties` from `db.properties.example`.

```bash
cd DB8/src/main/resources
cp db.properties.example db.properties
```

Edit `db.properties`.

```properties
db.url=jdbc:mysql://127.0.0.1:3307/db8_bookstore
db.user=dbadmin
db.password=YOUR_PASSWORD
```

For SSH tunnel access, start the tunnel before running Java.

```bash
ssh -fN db8-tunnel
```

## Run Java Application

Open `DB8` as a Maven project in IntelliJ IDEA and run:

```text
Main.java
```

Main class:

```text
Main
```

# Application Menu Summary

## Main Menu

```text
1. Customer Login
2. Register new customer
3. Manager Login
0. Exit
```

## Customer Menu

```text
1. Search books by category or keyword
2. Add book to market basket
3. Remove book from market basket
4. Purchase books in market basket
5. View my purchase history
6. View purchases before/after my profile changes
7. View popular categories by age group
8. View my profile information
9. Update my profile information
10. Write book review
11. Delete my review
0. Logout
```

## Manager Menu

```text
1. Search books by category or keyword
2. Update product price
3. Analyze sales before/after product price changes
4. View product total sales summary
5. View sales analysis summary
6. View inventory status
7. Update stock quantity
8. Manage manager roles
9. Register new manager
10. Dismiss manager
0. Logout
```

# Demo Accounts

## Manager MASTER

```text
email: mlee051201@gmail.com
password: passwd4mst
```

## Customer

```text
email: linusT@linux.com
password: linux4u
```

# SQL Scripts

| File | Purpose |
|---|---|
| `DB8/sql/createschema.sql` | Creates tables, primary keys, foreign keys, and indexes |
| `DB8/sql/views.sql` | Creates database views |
| `DB8/sql/initdata.sql` | Inserts initial data |
| `DB8/sql/dropschema.sql` | Drops views and tables |

# Main Database Features

## Product Price History

The system stores the current product price in the `product` table and stores historical price changes in `product_price_history`.

When a product price is updated:

1. The previous price history row is closed by setting `end_date`.
2. A new price history row is inserted.
3. The current price in `product` is updated.

Past sales prices are preserved in:

```text
sales_detail.unit_price_at_sale
```

This allows sales analysis before and after product price changes.

## Customer Profile History

The system tracks customer demographic changes using:

```text
customer_profile_history
```

The `sales` table stores:

```text
profile_id
age_at_sale
```

This allows analysis of customer purchases before and after profile changes, such as city or membership level changes.

## Manager Role-Based Access

Managers are authenticated through the `manager` table.

Manager roles are stored in:

```text
manager_role
manager_role_assignment
```

A manager can have multiple roles through `manager_role_assignment`.

Example roles:

```text
MASTER
SALES_ANALYSIS
INVENTORY_MANAGER
PRICE_MANAGER
CATEGORY_MANAGER
```

`MASTER` can access all manager menus and can manage other managers' roles, register new managers, and dismiss managers.

# JDBC Implementation Notes

## PreparedStatement

The application uses `PreparedStatement` for user input queries.

Instead of concatenating user input directly into SQL strings, the code uses placeholders:

```sql
WHERE email = ?
AND password = ?
```

Then values are bound with methods such as:

```java
setString()
setInt()
setBigDecimal()
setDate()
```

This helps prevent SQL injection and handles input types safely.

## Transaction Handling

Important update operations are handled using transactions.

Examples:

- Purchase books in market basket
- Update product price
- Update customer profile
- Register manager
- Dismiss manager

If an error occurs during a transaction, rollback is executed so partial updates are not saved.

# Requirement Coverage Summary

| Requirement | How It Is Satisfied |
|---|---|
| REQ1 | Expanded the original schema into an online bookstore and sales analysis system |
| REQ2 | Contains more than 7 tables and more than 2 views |
| REQ3 | Uses primary keys, foreign keys, and indexes |
| REQ4 | Each table is populated with at least 10 tuples |
| REQ5 | Provides insert menus such as customer registration, basket insert, review insert, manager registration |
| REQ6 | Provides select menus using user input, joins, and views |
| REQ7 | Provides aggregation and `GROUP BY` analysis menus |
| REQ8 | Provides update menus such as price update, stock update, profile update, role update |
| REQ9 | Provides delete menus such as basket removal, review deletion, manager dismissal |
| REQ10 | Uses `PreparedStatement` for user input |
| REQ11 | Contains views and indexes |
| REQ12 | Uses transactions for important update operations |
| REQ13 | Supports product price history and sales analysis before/after price changes |
| REQ14 | Supports customer profile history and demographic sales analysis |
| REQ15 | Provides a text-based user interface |
| REQ16 | Provides schema, initial data, and drop scripts |
| REQ17 | Provides Java source code |
| REQ18 | Provides executable compiled jar file |
| REQ19 | Provides README with execution instructions |
| REQ20 | Final report includes ERD, schema diagram, Java structure, screenshots, requirements, and responsibilities |
