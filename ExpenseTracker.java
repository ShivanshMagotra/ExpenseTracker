import java.util.Scanner;
import java.sql.*;

public class ExpenseTracker{

    static Connection getConnection() throws SQLException{
        String url = "jdbc:sqlite:expenses.db";
        return DriverManager.getConnection(url);
    }
    static void createTable(){
        String sql = """
            CREATE TABLE IF NOT EXISTS expenses(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                category TEXT,
                amount REAL,
                date TEXT
            )
        """;
        try(Connection conn  = getConnection(); Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Database and table ready.");
        }catch(SQLException e){
            System.out.println("Error creating table.");
            e.printStackTrace();
        }
    }
    static void addExpenseToDB(String title, String category, double amount, String date){
        String sql = "INSERT INTO expenses(title, category,amount,date)VALUES(?,?,?,?)";
        try(Connection conn  = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1,title);
                pstmt.setString(2,category);
                pstmt.setDouble(3,amount);
                pstmt.setString(4,date);
                
                pstmt.executeUpdate();
                System.out.println("Expenses saved to database.");
        }catch(SQLException e){
            System.out.println("Error inserting expense.");
            e.printStackTrace();
        }
    }

    static void addExpense(Scanner sc){
        System.out.print("Enter expense title: ");
        String title = sc.nextLine();

        System.out.print("Enter category(Food, Travel etc): ");
        String category =sc.nextLine();

        System.out.print("Enter expense amount: ");
        double amount = sc.nextDouble();

        sc.nextLine();
        System.out.print("Enter the date(YYYY-MM-DD): ");
        String date = sc.nextLine();

        addExpenseToDB(title, category, amount, date);

        System.out.println("Expense added successfully!");
    }
    static void viewExpensesFromDB(){
        String sql="SELECT id,title,category,amount,date FROM expenses";
        double total=0;
        try(Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
                System.out.println("Your Expenses: ");
                boolean found = false;
                while(rs.next()){
                    String title = rs.getString("title");
                    String category = rs.getString("category");
                    Double amount = rs.getDouble("amount");
                    String date = rs.getString("date");
                    int id = rs.getInt("id");
                    System.out.println(id+" | "+title + " | "+ category +" | $"+ amount + " | "+ date);
                    total +=amount;
                    found = true;
                }
                if(!found){
                    System.out.println("No expenses recorded");
                }else{
                    System.out.println("Total expenses: $"+ total);
                }
        }catch(SQLException e){
            System.out.println("Error reading expenses.");
            e.printStackTrace();
        }
    }
    static void viewExpensesByDateFromDB(Scanner sc){
        System.out.print("Enter date(YYYY-MM-DD): ");
        String inputDate = sc.nextLine();
        String sql = """
                SELECT id,title,category,amount,date FROM expenses WHERE date = ?
        """;
        double total =0;
        boolean found = false;

        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1,inputDate);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n Expenses on "+ inputDate+":");
            while(rs.next()){
                String title = rs.getString("title");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                String date = rs.getString("date");
                int id = rs.getInt("id");

                System.out.println(id+" | "+title+" | "+ category + " | $"+amount+" | "+date );
                total+=amount;
                found = true;
            }
            if(!found){
                System.out.println("No expenses found on this date");
            }else{
                System.out.println("Total expense on "+ inputDate+": $"+total);
            }
        }catch(SQLException e){
            System.out.println("Error fetching expenses by date");
            e.printStackTrace();
        }
    }
    static void viewExpensesByCategoryFromDB(){
        String sql="""
                SELECT category , SUM(amount)AS total
                FROM expenses
                GROUP BY category
            """;
        try(Connection conn = getConnection();Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
                System.out.println("Expenses by category:");
                boolean found =false;
                while(rs.next()){
                    String category = rs.getString("category");
                    double total = rs.getDouble("total");
                    System.out.println(category+" :$"+total);
                    found = true;
                }
                if(!found){
                    System.out.println("No expenses recorded.");
                }
            }catch(SQLException e){
                System.out.println("Error fetching category wise expenses.");
                e.printStackTrace();
            }
    }
    static void deleteExpensesByIdFromDB(Scanner sc){
        System.out.print("Enter expense ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();
        String sql = "DELETE FROM expenses WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,id);
            int rowsAffected = pstmt.executeUpdate();
            
            if(rowsAffected>0){
                System.out.println("Expense deleted successfully.");
            }else{
                System.out.println("No expense found with this ID.");
            }
        }catch(SQLException e){
            System.out.println("Error deleting expense.");
            e.printStackTrace();
        }
    }
    static void updateExpenseByIdFromDB(Scanner sc){
        System.out.print("Enter expense id to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new title: ");
        String title = sc.nextLine();

        System.out.print("Enter new category: ");
        String category = sc.nextLine();

        System.out.print("Enter new amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter new date(YYYY-MM-DD): ");
        String date = sc.nextLine();

        String sql = """
                UPDATE expenses
                SET title = ?,category =?, amount=?,date=?
                WHERE id=?
            """;
        try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,title);
            pstmt.setString(2,category);
            pstmt.setDouble(3,amount);
            pstmt.setString(4,date);
            pstmt.setInt(5,id);
            int rowsAffected =  pstmt.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Expenses updated successfully.");
            }else{
                System.out.println("No expenses found with this ID.");
            }
        }catch(SQLException e){
            System.out.println("Error updating expense.");
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        createTable();
        Scanner sc = new Scanner(System.in);
        int choice;
        do{
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Exit");
            System.out.println("4. View Expenses by date");
            System.out.println("5. View Expenses by category");
            System.out.println("6. Delete expense");
            System.out.println("7. Update expense");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            if(choice==1){
                addExpense(sc);
            }else if(choice==2){
                viewExpensesFromDB();
            }else if(choice==3){
                System.out.println("Exiting Goodbye!");
            }else if(choice ==4){
                viewExpensesByDateFromDB(sc);
            }else if (choice==5){
                viewExpensesByCategoryFromDB();
            }else if(choice ==6){
                deleteExpensesByIdFromDB(sc);
            }else if(choice==7){
                updateExpenseByIdFromDB(sc);
            }
            else{
                System.out.println("Invalid choice. Try again.");
            }
        }while(choice!=3);
        sc.close();
    }
}