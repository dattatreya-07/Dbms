package dbms;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Material Management System
 * Oracle JDBC + Java Swing.
 * Table: MATERIAL (MATERIAL_ID, MATERIAL_NAME, UNIT, TOTAL_QUANTITY,
 *                  PURCHASE_DATE, COST_PER_UNIT, CATEGORY_ID)
 */
public class MaterialManagement extends JFrame {

    // ─── DB CONFIG ────────────────────────────────────────────────────────────
    private static final String DB_URL  = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "C##project_user";   // ← change
    private static final String DB_PASS = "1234";   // ← change

    // ─── PALETTE ─────────────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(13,  17,  23);
    private static final Color BG_CARD    = new Color(22,  27,  34);
    private static final Color BG_SIDEBAR = new Color(17,  21,  28);
    private static final Color ACCENT     = new Color(88, 166, 255);
    private static final Color ACCENT2    = new Color(63, 185, 80);
    private static final Color ACCENT3    = new Color(255, 166,  77);
    private static final Color DANGER     = new Color(248,  81,  73);
    private static final Color TEXT_PRI   = new Color(230, 237, 243);
    private static final Color TEXT_SEC   = new Color(139, 148, 158);
    private static final Color BORDER_COL = new Color(48,  54,  61);
    private static final Color ROW_EVEN   = new Color(22,  27,  34);
    private static final Color ROW_ODD    = new Color(28,  33,  40);
    private static final Color ROW_SEL    = new Color(33,  58,  95);

    // ─── FONTS ───────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEAD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 13);

    // ─── STATE ────────────────────────────────────────────────────────────────
    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable dataTable;
    private JLabel statusLabel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Form fields
    private JTextField tfId, tfName, tfUnit, tfQty, tfDate, tfCost, tfCatId;
    private JTextField searchField;

    // ─── SIDEBAR NAV ─────────────────────────────────────────────────────────
    private JButton activeNavBtn = null;

    public MaterialManagement() {
        setTitle("Material Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        initDB();
        buildUI();
        setVisible(true);
        loadTableData();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  DATABASE
    // ═══════════════════════════════════════════════════════════════════════
    private void initDB() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("[DB] Connected to Oracle successfully.");
        } catch (Exception e) {
            showError("Database Connection Failed", e.getMessage());
            System.err.println("[DB] Connection error: " + e.getMessage());
        }
    }

    private void loadTableData() {
        loadTableData("SELECT * FROM MATERIAL ORDER BY MATERIAL_ID");
    }

    private void loadTableData(String sql) {
        tableModel.setRowCount(0);
        if (connection == null) { setStatus("No DB connection.", DANGER); return; }
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            int count = 0;
            while (rs.next()) {
                java.sql.Date d = rs.getDate("PURCHASE_DATE");
                tableModel.addRow(new Object[]{
                    rs.getInt("MATERIAL_ID"),
                    rs.getString("MATERIAL_NAME"),
                    rs.getString("UNIT"),
                    rs.getInt("TOTAL_QUANTITY"),
                    d != null ? sdf.format(d) : "",
                    rs.getDouble("COST_PER_UNIT"),
                    rs.getInt("CATEGORY_ID")
                });
                count++;
            }
            setStatus("Loaded " + count + " record(s).", ACCENT2);
        } catch (SQLException e) {
            setStatus("Query error: " + e.getMessage(), DANGER);
        }
    }

    private void insertMaterial() {
        if (!validateForm()) return;
        String sql = "INSERT INTO MATERIAL (MATERIAL_ID, MATERIAL_NAME, UNIT, " +
                     "TOTAL_QUANTITY, PURCHASE_DATE, COST_PER_UNIT, CATEGORY_ID) " +
                     "VALUES (?, ?, ?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, Integer.parseInt(tfId.getText().trim()));
            ps.setString(2, tfName.getText().trim());
            ps.setString(3, tfUnit.getText().trim());
            ps.setInt   (4, Integer.parseInt(tfQty.getText().trim()));
            ps.setString(5, tfDate.getText().trim());
            ps.setDouble(6, Double.parseDouble(tfCost.getText().trim()));
            ps.setInt   (7, Integer.parseInt(tfCatId.getText().trim()));
            ps.executeUpdate();
            setStatus("Record inserted successfully!", ACCENT2);
            loadTableData();
            clearForm();
            showCard("VIEW");
        } catch (SQLException e) {
            setStatus("Insert error: " + e.getMessage(), DANGER);
        }
    }

    private void updateMaterial() {
        if (!validateForm()) return;
        String sql = "UPDATE MATERIAL SET MATERIAL_NAME=?, UNIT=?, TOTAL_QUANTITY=?, " +
                     "PURCHASE_DATE=TO_DATE(?, 'DD-MM-YYYY'), COST_PER_UNIT=?, " +
                     "CATEGORY_ID=? WHERE MATERIAL_ID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfUnit.getText().trim());
            ps.setInt   (3, Integer.parseInt(tfQty.getText().trim()));
            ps.setString(4, tfDate.getText().trim());
            ps.setDouble(5, Double.parseDouble(tfCost.getText().trim()));
            ps.setInt   (6, Integer.parseInt(tfCatId.getText().trim()));
            ps.setInt   (7, Integer.parseInt(tfId.getText().trim()));
            int rows = ps.executeUpdate();
            setStatus(rows > 0 ? "Record updated!" : "No record found.", rows > 0 ? ACCENT2 : ACCENT3);
            loadTableData();
        } catch (SQLException e) {
            setStatus("Update error: " + e.getMessage(), DANGER);
        }
    }

    private void deleteMaterial() {
        String idTxt = tfId.getText().trim();
        if (idTxt.isEmpty()) { setStatus("Enter Material ID to delete.", ACCENT3); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete Material ID " + idTxt + "?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM MATERIAL WHERE MATERIAL_ID=?")) {
            ps.setInt(1, Integer.parseInt(idTxt));
            int rows = ps.executeUpdate();
            setStatus(rows > 0 ? "Record deleted!" : "ID not found.", rows > 0 ? ACCENT2 : ACCENT3);
            loadTableData();
            clearForm();
        } catch (SQLException e) {
            setStatus("Delete error: " + e.getMessage(), DANGER);
        }
    }

    private void searchMaterial() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadTableData(); return; }
        loadTableData("SELECT * FROM MATERIAL WHERE " +
                      "UPPER(MATERIAL_NAME) LIKE UPPER('%" + q + "%') OR " +
                      "TO_CHAR(MATERIAL_ID) LIKE '%" + q + "%' " +
                      "ORDER BY MATERIAL_ID");
    }

    private void populateFormFromTable() {
        int row = dataTable.getSelectedRow();
        if (row < 0) return;
        tfId.setText   (tableModel.getValueAt(row, 0).toString());
        tfName.setText (tableModel.getValueAt(row, 1).toString());
        tfUnit.setText (tableModel.getValueAt(row, 2).toString());
        tfQty.setText  (tableModel.getValueAt(row, 3).toString());
        tfDate.setText (tableModel.getValueAt(row, 4).toString());
        tfCost.setText (tableModel.getValueAt(row, 5).toString());
        tfCatId.setText(tableModel.getValueAt(row, 6).toString());
        showCard("FORM");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UI BUILD
    // ═══════════════════════════════════════════════════════════════════════
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);
        root.add(buildStatusBar(),BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Top Bar ───────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        JLabel logo = new JLabel("  ⬡  MaterialMS");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);
        logo.setBorder(new EmptyBorder(0, 20, 0, 0));
        bar.add(logo, BorderLayout.WEST);

        JLabel tagLine = new JLabel("Inventory & Procurement Console  ");
        tagLine.setFont(FONT_SMALL);
        tagLine.setForeground(TEXT_SEC);
        bar.add(tagLine, BorderLayout.EAST);

        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(BG_SIDEBAR);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL));

        side.add(Box.createVerticalStrut(18));
        side.add(sectionLabel("NAVIGATION"));
        side.add(Box.createVerticalStrut(6));

        JButton btnView   = navButton("📋  View All",    "VIEW");
        JButton btnInsert = navButton("➕  Insert",       "FORM");
        JButton btnSearch = navButton("🔍  Search",       "SEARCH");
        JButton btnStats  = navButton("📊  Statistics",   "STATS");

        side.add(btnView);
        side.add(btnInsert);
        side.add(btnSearch);
        side.add(btnStats);

        side.add(Box.createVerticalStrut(20));
        side.add(sectionLabel("ACTIONS"));
        side.add(Box.createVerticalStrut(6));

        JButton btnRefresh = navButton("↺  Refresh",     null);
        btnRefresh.addActionListener(e -> { loadTableData(); showCard("VIEW"); });

        JButton btnClear   = navButton("✕  Clear Form",  null);
        btnClear.addActionListener(e -> clearForm());

        side.add(btnRefresh);
        side.add(btnClear);

        side.add(Box.createVerticalGlue());
        side.add(buildDbStatus());
        side.add(Box.createVerticalStrut(14));

        // Set default active
        setActiveNav(btnView);
        btnView.addActionListener(e -> { setActiveNav(btnView);   showCard("VIEW"); });
        btnInsert.addActionListener(e -> { setActiveNav(btnInsert); showCard("FORM"); });
        btnSearch.addActionListener(e -> { setActiveNav(btnSearch); showCard("SEARCH"); });
        btnStats.addActionListener(e -> { setActiveNav(btnStats);  showCard("STATS"); loadStats(); });

        return side;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(210, 22));
        return lbl;
    }

    private JButton navButton(String text, String card) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("active") != null) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 30));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_PRI);
        btn.setBackground(new Color(0,0,0,0));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 18, 9, 10));
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.putClientProperty("active", null);
            activeNavBtn.setForeground(TEXT_PRI);
            activeNavBtn.repaint();
        }
        activeNavBtn = btn;
        btn.putClientProperty("active", true);
        btn.setForeground(ACCENT);
        btn.repaint();
    }

    private JPanel buildDbStatus() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(BG_SIDEBAR);
        p.setMaximumSize(new Dimension(210, 36));
        JLabel dot = new JLabel("●");
        dot.setForeground(connection != null ? ACCENT2 : DANGER);
        dot.setFont(FONT_SMALL);
        JLabel lbl = new JLabel(connection != null ? "Oracle Connected" : "Not Connected");
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SEC);
        p.add(dot); p.add(lbl);
        return p;
    }

    // ── Content Area ──────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        contentPanel.add(buildViewPanel(),   "VIEW");
        contentPanel.add(buildFormPanel(),   "FORM");
        contentPanel.add(buildSearchPanel(), "SEARCH");
        contentPanel.add(buildStatsPanel(),  "STATS");

        return contentPanel;
    }

    private void showCard(String name) { cardLayout.show(contentPanel, name); }

    // ── VIEW panel ────────────────────────────────────────────────────────
    private JPanel buildViewPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("All Materials");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_PRI);
        header.add(title, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnEdit = actionBtn("✏  Edit Selected", ACCENT);
        JButton btnDel  = actionBtn("🗑  Delete Selected", DANGER);
        btnEdit.addActionListener(e -> populateFormFromTable());
        btnDel.addActionListener(e -> {
            int row = dataTable.getSelectedRow();
            if (row >= 0) {
                tfId.setText(tableModel.getValueAt(row, 0).toString());
                deleteMaterial();
            } else setStatus("Select a row to delete.", ACCENT3);
        });
        btnRow.add(btnEdit); btnRow.add(btnDel);
        header.add(btnRow, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Material Name", "Unit", "Qty", "Purchase Date", "Cost/Unit", "Cat. ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        dataTable = new JTable(tableModel);
        styleTable(dataTable);

        dataTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) populateFormFromTable();
            }
        });

        JScrollPane scroll = new JScrollPane(dataTable);
        styleScrollPane(scroll);
        p.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("  Double-click a row to edit · Right-click for options");
        hint.setFont(FONT_SMALL); hint.setForeground(TEXT_SEC);
        p.add(hint, BorderLayout.SOUTH);

        return p;
    }

    // ── FORM panel ────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_DARK);
        outer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Insert / Update Material");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_PRI);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        outer.add(title, BorderLayout.NORTH);

        // Card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1),
            new EmptyBorder(28, 32, 28, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        tfId    = formField("e.g. 101");
        tfName  = formField("e.g. Cement OPC 53");
        tfUnit  = formField("e.g. KG, BAG, MTR");
        tfQty   = formField("e.g. 500");
        tfDate  = formField("DD-MM-YYYY");
        tfCost  = formField("e.g. 350.00");
        tfCatId = formField("e.g. 3");

        String[][] rows2 = {
            {"Material ID *",    "0"},
            {"Material Name *",  "1"},
            {"Unit *",           "2"},
            {"Total Quantity *", "3"},
            {"Purchase Date *",  "4"},
            {"Cost Per Unit *",  "5"},
            {"Category ID *",    "6"}
        };
        JTextField[] fields = {tfId, tfName, tfUnit, tfQty, tfDate, tfCost, tfCatId};

        for (int i = 0; i < rows2.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.25;
            JLabel lbl = new JLabel(rows2[i][0]);
            lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_SEC);
            card.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.75;
            card.add(fields[i], gbc);
        }

        // Buttons row
        gbc.gridx = 0; gbc.gridy = rows2.length; gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 8, 4, 8);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnInsert = actionBtn("➕  Insert",    ACCENT2);
        JButton btnUpdate = actionBtn("✏  Update",    ACCENT);
        JButton btnDelete = actionBtn("🗑  Delete",    DANGER);
        JButton btnClear  = actionBtn("✕  Clear",     new Color(80, 86, 95));

        btnInsert.addActionListener(e -> insertMaterial());
        btnUpdate.addActionListener(e -> updateMaterial());
        btnDelete.addActionListener(e -> deleteMaterial());
        btnClear .addActionListener(e -> clearForm());

        btnRow.add(btnClear); btnRow.add(btnDelete);
        btnRow.add(btnUpdate); btnRow.add(btnInsert);
        card.add(btnRow, gbc);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG_DARK);
        GridBagConstraints wc = new GridBagConstraints();
        wc.anchor = GridBagConstraints.NORTH;
        wc.weightx = 1; wc.weighty = 1;
        wrap.add(card, wc);

        outer.add(wrap, BorderLayout.CENTER);
        return outer;
    }

    // ── SEARCH panel ─────────────────────────────────────────────────────
    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Search Materials");
        title.setFont(FONT_HEAD); title.setForeground(TEXT_PRI);
        title.setBorder(new EmptyBorder(0,0,12,0));
        p.add(title, BorderLayout.NORTH);

        // Search bar
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(BG_DARK);
        searchField = formField("Search by name or ID...");
        searchField.setFont(FONT_BODY);
        JButton btnGo = actionBtn("Search", ACCENT);
        btnGo.addActionListener(e -> searchMaterial());
        searchField.addActionListener(e -> searchMaterial());

        bar.add(searchField, BorderLayout.CENTER);
        bar.add(btnGo, BorderLayout.EAST);

        // Result table (reuse same model)
        JTable srTable = new JTable(tableModel);
        styleTable(srTable);
        JScrollPane scroll = new JScrollPane(srTable);
        styleScrollPane(scroll);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG_DARK);
        center.add(bar,    BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── STATS panel ──────────────────────────────────────────────────────
    private JTextArea statsArea;

    private JPanel buildStatsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Statistics & Summary");
        title.setFont(FONT_HEAD); title.setForeground(TEXT_PRI);
        title.setBorder(new EmptyBorder(0,0,12,0));
        p.add(title, BorderLayout.NORTH);

        statsArea = new JTextArea();
        statsArea.setFont(FONT_MONO);
        statsArea.setBackground(BG_CARD);
        statsArea.setForeground(ACCENT2);
        statsArea.setEditable(false);
        statsArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        statsArea.setText("Click 'Statistics' in the sidebar to load stats.");

        JScrollPane scroll = new JScrollPane(statsArea);
        styleScrollPane(scroll);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void loadStats() {
        if (connection == null) { statsArea.setText("No DB connection."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════╗\n");
        sb.append("║         MATERIAL MANAGEMENT  —  STATISTICS           ║\n");
        sb.append("╚══════════════════════════════════════════════════════╝\n\n");

        try {
            // Summary
            String[] sqls = {
                "SELECT COUNT(*) AS C FROM MATERIAL",
                "SELECT SUM(TOTAL_QUANTITY) AS C FROM MATERIAL",
                "SELECT SUM(TOTAL_QUANTITY * COST_PER_UNIT) AS C FROM MATERIAL",
                "SELECT AVG(COST_PER_UNIT) AS C FROM MATERIAL",
                "SELECT MAX(COST_PER_UNIT) AS C FROM MATERIAL",
                "SELECT MIN(COST_PER_UNIT) AS C FROM MATERIAL"
            };
            String[] labels = {
                "Total Records       ", "Total Quantity      ",
                "Total Inventory Val ", "Avg Cost/Unit       ",
                "Max Cost/Unit       ", "Min Cost/Unit       "
            };
            for (int i = 0; i < sqls.length; i++) {
                try (Statement st = connection.createStatement();
                     ResultSet rs = st.executeQuery(sqls[i])) {
                    if (rs.next()) {
                        sb.append(String.format("  %-22s : %s\n",
                            labels[i], rs.getString(1) != null ? rs.getString(1) : "0"));
                    }
                }
            }

            // Category breakdown
            sb.append("\n──────────────────────────────────────────────────────\n");
            sb.append("  Records By Category:\n\n");
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(
                    "SELECT CATEGORY_ID, COUNT(*) AS CNT, SUM(TOTAL_QUANTITY) AS TOT " +
                    "FROM MATERIAL GROUP BY CATEGORY_ID ORDER BY CATEGORY_ID")) {
                sb.append(String.format("  %-14s %-12s %-14s\n", "Category ID", "# Items", "Total Qty"));
                sb.append("  " + "─".repeat(42) + "\n");
                while (rs.next())
                    sb.append(String.format("  %-14s %-12s %-14s\n",
                        rs.getString(1), rs.getString(2), rs.getString(3)));
            }

            // Top 5 by value
            sb.append("\n──────────────────────────────────────────────────────\n");
            sb.append("  Top 5 by Inventory Value (Qty × Cost):\n\n");
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(
                    "SELECT MATERIAL_NAME, TOTAL_QUANTITY, COST_PER_UNIT, " +
                    "(TOTAL_QUANTITY * COST_PER_UNIT) AS VAL " +
                    "FROM MATERIAL ORDER BY VAL DESC FETCH FIRST 5 ROWS ONLY")) {
                sb.append(String.format("  %-30s %8s %12s %14s\n",
                    "Name", "Qty", "Cost/Unit", "Total Value"));
                sb.append("  " + "─".repeat(68) + "\n");
                while (rs.next())
                    sb.append(String.format("  %-30s %8s %12s %14s\n",
                        rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4)));
            }

        } catch (SQLException e) {
            sb.append("\n[ERROR] ").append(e.getMessage());
        }
        statsArea.setText(sb.toString());
        statsArea.setCaretPosition(0);
    }

    // ── Status Bar ────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(10, 14, 20));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL));
        bar.setPreferredSize(new Dimension(0, 30));

        statusLabel = new JLabel("  Ready.");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_SEC);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel right = new JLabel("MaterialMS v1.0 — Oracle DB  ");
        right.setFont(FONT_SMALL); right.setForeground(TEXT_SEC);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════════════
    private JTextField formField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(TEXT_SEC.getRed(), TEXT_SEC.getGreen(),
                                          TEXT_SEC.getBlue(), 120));
                    g2.setFont(FONT_SMALL);
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        tf.setFont(FONT_BODY);
        tf.setBackground(new Color(30, 36, 44));
        tf.setForeground(TEXT_PRI);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(280, 36));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    new EmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COL, 1),
                    new EmptyBorder(6, 10, 6, 10)));
            }
        });
        return tf;
    }

    private JButton actionBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover()
                    ? bg.brighter()
                    : getModel().isPressed()
                        ? bg.darker() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable t) {
        t.setFont(FONT_BODY);
        t.setForeground(TEXT_PRI);
        t.setBackground(BG_CARD);
        t.setSelectionBackground(ROW_SEL);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(BORDER_COL);
        t.setRowHeight(34);
        t.setShowVerticalLines(true);
        t.setShowHorizontalLines(true);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);

        // Header
        JTableHeader hdr = t.getTableHeader();
        hdr.setFont(FONT_HEAD);
        hdr.setBackground(new Color(30, 36, 46));
        hdr.setForeground(ACCENT);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        hdr.setReorderingAllowed(false);

        // Column widths
        int[] widths = {55, 200, 70, 70, 115, 90, 75};
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Alternating rows renderer
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));
        sp.getViewport().setBackground(BG_CARD);
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
    }

    private boolean validateForm() {
        if (tfId.getText().trim().isEmpty() || tfName.getText().trim().isEmpty()
            || tfUnit.getText().trim().isEmpty() || tfQty.getText().trim().isEmpty()
            || tfDate.getText().trim().isEmpty() || tfCost.getText().trim().isEmpty()
            || tfCatId.getText().trim().isEmpty()) {
            setStatus("All fields are required.", ACCENT3);
            return false;
        }
        try { Integer.parseInt(tfId.getText().trim()); } catch (NumberFormatException e) {
            setStatus("Material ID must be a number.", DANGER); return false; }
        try { Integer.parseInt(tfQty.getText().trim()); } catch (NumberFormatException e) {
            setStatus("Quantity must be a number.", DANGER); return false; }
        try { Double.parseDouble(tfCost.getText().trim()); } catch (NumberFormatException e) {
            setStatus("Cost must be numeric.", DANGER); return false; }
        try { Integer.parseInt(tfCatId.getText().trim()); } catch (NumberFormatException e) {
            setStatus("Category ID must be a number.", DANGER); return false; }
        return true;
    }

    private void clearForm() {
        tfId.setText(""); tfName.setText(""); tfUnit.setText("");
        tfQty.setText(""); tfDate.setText(""); tfCost.setText("");
        tfCatId.setText("");
        setStatus("Form cleared.", TEXT_SEC);
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText("  " + msg);
        statusLabel.setForeground(color);
    }

    private void showError(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MAIN
    // ═══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Global UI defaults
        UIManager.put("OptionPane.background",        new Color(22, 27, 34));
        UIManager.put("Panel.background",             new Color(22, 27, 34));
        UIManager.put("OptionPane.messageForeground", new Color(230, 237, 243));

        SwingUtilities.invokeLater(MaterialManagement::new);
    }
}

