# ER Diagram and Relational Schema Notes

## ER Diagram

```mermaid
erDiagram
    BOOK_CATEGORY ||--o{ PRODUCT : classifies
    PRODUCT ||--o{ MARKET_BASKET : added_to
    CUSTOMER ||--o{ MARKET_BASKET : owns
    CUSTOMER ||--o{ SALES : places
    CUSTOMER ||--o{ CUSTOMER_PROFILE_HISTORY : has
    CUSTOMER_PROFILE_HISTORY ||--o{ SALES : referenced_at_sale
    SALES ||--o{ SALES_DETAIL : contains
    PRODUCT ||--o{ SALES_DETAIL : sold_as
    PRODUCT ||--|| TOTAL_SALES : summarized_by
    PRODUCT ||--o{ PRODUCT_PRICE_HISTORY : tracks_price
    CUSTOMER ||--o{ BOOK_REVIEW : writes
    PRODUCT ||--o{ BOOK_REVIEW : receives
    MANAGER ||--o{ MANAGER_ROLE_ASSIGNMENT : has
    MANAGER_ROLE ||--o{ MANAGER_ROLE_ASSIGNMENT : assigned_as

    BOOK_CATEGORY {
        int category_id PK
        varchar category_name UK
        varchar description
    }

    PRODUCT {
        int product_id PK
        int category_id FK
        varchar product_name
        varchar author
        varchar publisher
        decimal unit_price
        int stock_quantity
    }

    CUSTOMER {
        int customer_id PK
        varchar first_name
        varchar last_name
        varchar email UK
        varchar password
        varchar phone
        date birth_date
        date join_date
    }

    CUSTOMER_PROFILE_HISTORY {
        int profile_id PK
        int customer_id FK
        varchar city
        varchar membership_level
        timestamp start_date
        timestamp end_date
    }

    MARKET_BASKET {
        int basket_id PK
        int customer_id FK
        int product_id FK
        int quantity
        timestamp added_at
        decimal unit_price_at_added
    }

    SALES {
        int sales_id PK
        int customer_id FK
        timestamp sales_timestamp
        decimal total_amount
        int profile_id FK
        int age_at_sale
    }

    SALES_DETAIL {
        int sales_detail_id PK
        int sales_id FK
        int product_id FK
        int quantity
        decimal unit_price_at_sale
        decimal subtotal
    }

    TOTAL_SALES {
        int total_sales_id PK
        int product_id FK_UK
        int total_quantity
        decimal total_revenue
    }

    PRODUCT_PRICE_HISTORY {
        int price_history_id PK
        int product_id FK
        decimal unit_price
        timestamp start_date
        timestamp end_date
    }

    BOOK_REVIEW {
        int review_id PK
        int customer_id FK
        int product_id FK
        int rating
        varchar review_text
        date review_date
    }

    MANAGER {
        int manager_id PK
        varchar manager_name
        varchar email UK
        varchar password
    }

    MANAGER_ROLE {
        int role_id PK
        varchar role_name UK
        varchar description
    }

    MANAGER_ROLE_ASSIGNMENT {
        int manager_id PK_FK
        int role_id PK_FK
    }
```

## Relational Schema Summary

BOOK_CATEGORY(category_id PK, category_name UNIQUE, description)

PRODUCT(product_id PK, category_id FK -> BOOK_CATEGORY.category_id, product_name, author, publisher, unit_price, stock_quantity)

CUSTOMER(customer_id PK, first_name, last_name, email UNIQUE, password, phone, birth_date, join_date)

CUSTOMER_PROFILE_HISTORY(profile_id PK, customer_id FK -> CUSTOMER.customer_id, city, membership_level, start_date, end_date)

MARKET_BASKET(basket_id PK, customer_id FK -> CUSTOMER.customer_id, product_id FK -> PRODUCT.product_id, quantity, added_at, unit_price_at_added)

SALES(sales_id PK, customer_id FK -> CUSTOMER.customer_id, sales_timestamp, total_amount, profile_id FK -> CUSTOMER_PROFILE_HISTORY.profile_id, age_at_sale)

SALES_DETAIL(sales_detail_id PK, sales_id FK -> SALES.sales_id, product_id FK -> PRODUCT.product_id, quantity, unit_price_at_sale, subtotal)

TOTAL_SALES(total_sales_id PK, product_id UNIQUE FK -> PRODUCT.product_id, total_quantity, total_revenue)

PRODUCT_PRICE_HISTORY(price_history_id PK, product_id FK -> PRODUCT.product_id, unit_price, start_date, end_date)

BOOK_REVIEW(review_id PK, customer_id FK -> CUSTOMER.customer_id, product_id FK -> PRODUCT.product_id, rating, review_text, review_date)

MANAGER(manager_id PK, manager_name, email UNIQUE, password)

MANAGER_ROLE(role_id PK, role_name UNIQUE, description)

MANAGER_ROLE_ASSIGNMENT(manager_id PK/FK -> MANAGER.manager_id, role_id PK/FK -> MANAGER_ROLE.role_id)

## Views

- v_product_catalog: product and category information for book search.
- v_customer_purchase_history: customer purchase history with sales, product, category, and sales detail data.
- v_inventory_status: current product inventory with category information.
- v_sales_analysis_summary: product sales summary with current price and stock.

## Indexes

- idx_product_category on product(category_id)
- idx_market_basket_customer on market_basket(customer_id)
- idx_sales_customer_timestamp on sales(customer_id, sales_timestamp)
- idx_sales_detail_product on sales_detail(product_id)
- idx_book_review_product on book_review(product_id)

## Historical Design

- Product price changes are stored in PRODUCT_PRICE_HISTORY. Past sale prices are preserved in SALES_DETAIL.unit_price_at_sale.
- Customer profile changes are stored in CUSTOMER_PROFILE_HISTORY. Each sale references the active profile through SALES.profile_id and stores age_at_sale.
- Manager authorization uses MANAGER_ROLE_ASSIGNMENT so that one manager can have multiple roles.
