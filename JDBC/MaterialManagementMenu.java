package dbms;
import java.sql.*;
import java.util.Scanner;

public class MaterialManagementMenu {
    private static final String DB_URL  = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "C##project_user"; 
    private static final String DB_PASS = "1234"; 

    private static Connection connection;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        initDB();
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MATERIAL MANAGEMENT MENU =====");
            System.out.println("1. VIEW ALL MATERIALS");
            System.out.println("2. INSERT MATERIAL");
            System.out.println("3. UPDATE MATERIAL");
            System.out.println("4. DELETE MATERIAL");
            System.out.println("5. EXIT");
            System.out.print("Enter choice (1-5): ");

            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1: loadAndDisplay(); break;
                case 2: insertMaterial(); break;
                case 3: updateMaterial(); break;
                case 4: deleteMaterial(); break;
                case 5:
                    exit = true;
                    closeDB();
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid Choice!");
                    break;
            }
        }
    }

    private static void initDB() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("[✔] Connected to Database.");
        } catch (Exception e) {
            System.err.println("[✘] Connection Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void loadAndDisplay() {
        String sql = "SELECT MATERIAL_ID, MATERIAL_NAME, UNIT, TOTAL_QUANTITY, " +
                     "TO_CHAR(PURCHASE_DATE,'DD-MON-YYYY') AS P_DATE, " +
                     "COST_PER_UNIT, CATEGORY_ID FROM MATERIAL ORDER BY MATERIAL_ID";

        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.printf("| %-6s | %-20s | %-8s | %-6s | %-12s | %-10s | %-6s |%n",
                           "ID", "MATERIAL NAME", "UNIT", "QTY", "PURCH_DATE", "COST/UNIT", "CAT_ID");
        System.out.println("-----------------------------------------------------------------------------------------");

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("| %-6d | %-20s | %-8s | %-6d | %-12s | %-10.2f | %-6d |%n",
                    rs.getInt("MATERIAL_ID"), rs.getString("MATERIAL_NAME"), rs.getString("UNIT"),
                    rs.getInt("TOTAL_QUANTITY"), rs.getString("P_DATE"),
                    rs.getDouble("COST_PER_UNIT"), rs.getInt("CATEGORY_ID"));
            }
            System.out.println("-----------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void insertMaterial() {
        try {
            System.out.print("Material ID: "); int id = sc.nextInt(); sc.nextLine();
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Unit: "); String unit = sc.nextLine();
            System.out.print("Total Quantity: "); int qty = sc.nextInt(); sc.nextLine();
            System.out.print("Purchase Date (DD-MM-YYYY): "); String date = sc.nextLine();
            System.out.print("Cost Per Unit: "); double cost = sc.nextDouble();
            System.out.print("Category ID: "); int catId = sc.nextInt();

            String sql = "INSERT INTO MATERIAL VALUES (?, ?, ?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id); ps.setString(2, name); ps.setString(3, unit);
            ps.setInt(4, qty); ps.setString(5, date); ps.setDouble(6, cost); ps.setInt(7, catId);
            
            ps.executeUpdate();
            System.out.println("[✔] Material added successfully!");
        } catch (Exception e) {
            System.err.println("[✘] Insert Error: " + e.getMessage());
        }
    }

    private static void updateMaterial() {
        try {
            System.out.print("Enter Material ID to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            PreparedStatement getPs = connection.prepareStatement("SELECT * FROM MATERIAL WHERE MATERIAL_ID=?");
            getPs.setInt(1, id);
            ResultSet rs = getPs.executeQuery();

            if (!rs.next()) {
                System.out.println("[⚠] Material ID not found.");
                return;
            }

            // பழைய மதிப்புகளைப் பெறுதல்
            String oldName = rs.getString("MATERIAL_NAME");
            String oldUnit = rs.getString("UNIT");
            int oldQty = rs.getInt("TOTAL_QUANTITY");
            double oldCost = rs.getDouble("COST_PER_UNIT");

            System.out.println("--- Leave blank to keep existing value ---");

            System.out.print("New Name [" + oldName + "]: ");
            String name = sc.nextLine();
            if (name.isEmpty()) name = oldName;

            System.out.print("New Unit [" + oldUnit + "]: ");
            String unit = sc.nextLine();
            if (unit.isEmpty()) unit = oldUnit;

            System.out.print("New Quantity [" + oldQty + "]: ");
            String qtyStr = sc.nextLine();
            int qty = qtyStr.isEmpty() ? oldQty : Integer.parseInt(qtyStr);

            System.out.print("New Cost/Unit [" + oldCost + "]: ");
            String costStr = sc.nextLine();
            double cost = costStr.isEmpty() ? oldCost : Double.parseDouble(costStr);

            String sql = "UPDATE MATERIAL SET MATERIAL_NAME=?, UNIT=?, TOTAL_QUANTITY=?, COST_PER_UNIT=? WHERE MATERIAL_ID=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, unit);
            ps.setInt(3, qty);
            ps.setDouble(4, cost);
            ps.setInt(5, id);

            ps.executeUpdate();
            System.out.println("[✔] Material updated successfully!");

        } catch (Exception e) {
            System.err.println("[✘] Update Error: " + e.getMessage());
        }
    }

    private static void deleteMaterial() {
        try {
            System.out.print("Enter Material ID to delete: ");
            int id = sc.nextInt();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM MATERIAL WHERE MATERIAL_ID=?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "[✔] Record deleted!" : "[⚠] Not found.");
        } catch (SQLException e) {
            System.err.println("[✘] Delete Error: " + e.getMessage());
        }
    }

    private static void closeDB() {
        try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
    }
}
