import java.util.*;
import java.io.*;
import java.sql.*;

class Expense{
    String title;
    String category;
    double amount;
    String date;

    Expense(String title,String category,double amount,String date){
        this.title = title;
        this.amount = amount;
        this.category=category;
        this.date = date;
    }
 }
public class ExpenseTracker{
    //static ArrayList<Expense> expenses = new ArrayList<>();

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

        // Expense e = new Expense(title, category,amount,date);
        // expenses.add(e);
        addExpenseToDB(title, category, amount, date);

        System.out.println("Expense added successfully!");
    }
    static void viewExpensesFromDB(){
        String sql="SELECT title,category,amount,date FROM expenses";
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
                    System.out.println(title + " | "+ category +" | $"+ amount + " | "+ date);
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
        System.out.println("Enter date(YYYY-MM-DD): ");
        String inputDate = sc.nextLine();
        String sql = """
                SELECT title,category,amount,date FROM expenses WHERE date = ?
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

                System.out.println(title+" | "+ category + " | $"+amount+" | "+date );
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

    /*static void viewExpenses(){
        if(expenses.isEmpty()){
            System.out.println("No expenses recorded.");
            return;
        }
        double total=0;
        System.out.println("\n Your Expenses:");
        for(Expense exp: expenses){
            System.out.println(exp.title + " | "+exp.category+" | $"+exp.amount+" | "+exp.date);
            total+=exp.amount;
        }
        System.out.println("Total Spent: $"+total);    
    }
    static void saveExpensesToFile(){
        try{
            FileWriter fw = new FileWriter("expenses.txt");
            for(Expense exp: expenses){
                fw.write(exp.title + ", "+exp.category+", "+ exp.amount+", "+exp.date+"\n");
            }
            fw. close();
        }catch(IOException e){
            System.out.println("Error saving expenses.");
        }
    }
    static void loadExpensesFromFile(){
        try{
            File file = new File("expenses.txt");
            if(!file.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null){
                String [] parts = line.split(",");
                String title = parts[0].trim();
                String category =parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());
                String date= parts[3].trim();
                expenses.add(new Expense(title,category,amount,date));
            }
            br.close();
        }catch(Exception e){
            System.out.println("Error loading expenses.");
        }
    }
    static void viewExpensesbyDate(Scanner sc){
        System.out.print("Enter the date(YYYY-MM-DD)");
        String inputDate = sc.nextLine();
        boolean found=false;
        double total =0;
        for(Expense exp : expenses){
            if(exp.date.equals(inputDate)){
                System.out.println(exp.title +" | "+ exp.category+" | $"+exp.amount+" | "+exp.date);
                total+=exp.amount;
                found=true;
            }
        }
        if(!found){
            System.out.println("No expenses found for this date");
        }else{
            System.out.println("Total expenses on "+inputDate+" :$"+total);
        }
    }
    static void viewExpenseByCategory(){
        if(expenses.isEmpty()){
            System.out.println("No expenses recorded.");
            return;
        }
        HashMap<String,Double> map = new HashMap<>(); 
        for(Expense exp: expenses){
            map.put(exp.category,map.getOrDefault(exp.category,0.0)+exp.amount);
        }
        System.out.println("Expenses by category: ");
        for(Map.Entry<String, Double> entry: map.entrySet()){
            System.out.println(entry.getKey()+": $"+entry.getValue());
        }
    }*/
    public static void main(String args[]){
        createTable();
        //loadExpensesFromFile();
        Scanner sc = new Scanner(System.in);
        int choice;
        do{// do while because we have to show the menu to the user at least once
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Exit");
            System.out.println("4. View Expenses by date");
            System.out.println("5. View Expenses by category");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            if(choice==1){
                addExpense(sc);
            }else if(choice==2){
                //viewExpenses();
                viewExpensesFromDB();
            }else if(choice==3){
                System.out.println("Exiting Goodbye!");
            }else if(choice ==4){
                //viewExpensesbyDate(sc);
                viewExpensesByDateFromDB(sc);
            }else if (choice==5){
                //viewExpenseByCategory();
                viewExpensesByCategoryFromDB();
            }
            else{
                System.out.println("Invalid choice. Try again.");
            }
        }while(choice!=3);
        //saveExpensesToFile();
        sc.close();
    }
}