# Expense Tracker (Java + SQLite)

A console-based Expense Tracker application built using **Java**, **SQLite**, and **JDBC**.  
This project helps users record, view, update, and delete daily expenses.

---

## Features

- Add an expense with title, category, amount, and date
- View all expenses stored in the database
- Filter expenses by date
- View category-wise total expenses
- Update an existing expense by ID
- Delete an expense by ID

---

## Technologies Used

- Java
- SQLite
- JDBC (Java Database Connectivity)

---

## Database Design

The application uses a single table named `expenses` with the following columns:

- `id` (INTEGER, Primary Key, Auto Increment)
- `title` (TEXT)
- `category` (TEXT)
- `amount` (REAL)
- `date` (TEXT)

---

## How to Run the Project

1. Make sure Java is installed.
2. Download the SQLite JDBC driver (`sqlite-jdbc-x.x.x.jar`).
3. Place the JAR file in the project folder.
4. Compile the program:
   ```bash
   javac -cp ".;sqlite-jdbc.jar" ExpenseTracker.java
5. Run the program:
    ```bash
    java -cp ".;sqlite-jdbc.jar" ExpenseTracker