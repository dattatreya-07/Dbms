package dbms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

public class OnlineShoppingSystem extends JFrame {

    static final String DB_URL  = "jdbc:oracle:thin:@localhost:1521:XE";
    static final String DB_USER = "C##project_user";
    static final String DB_PASS = "1234";

    static final Color BG_DARK      = new Color(8,  12,  20);
    static final Color BG_CARD      = new Color(16, 22,  36);
    static final Color BG_INPUT     = new Color(24, 32,  50);
    static final Color BG_HOVER     = new Color(30, 42,  64);
    static final Color ACCENT       = new Color(96, 165, 250);
    static final Color ACCENT2      = new Color(251,146, 60);
    static final Color ACCENT3      = new Color(52, 211,153);
    static final Color PURPLE       = new Color(167,139,250);
    static final Color TEXT_PRIMARY = new Color(241,245,249);
    static final Color TEXT_MUTED   = new Color(100,116,139);
    static final Color TEXT_SUB     = new Color(148,163,184);
    static final Color SUCCESS      = new Color(52, 211,153);
    static final Color DANGER       = new Color(248,113,113);
    static final Color WARNING      = new Color(251,191, 36);
    static final Color BORDER_COL   = new Color(30, 41,  59);

    static int    loggedInCustomerId = -1;
    static String loggedInName       = "";
    static String loggedInEmail      = "";
    static int    activeCartId       = -1;

    private JPanel     sideNav;
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JLabel     userLabel;

    private LoginPanel         loginPanel;
    private RegisterPanel      registerPanel;
    private DashboardPanel     dashPanel;
    private ProductEntryPanel  productEntryPanel;
    private ProductSearchPanel productSearchPanel;
    private CartPanel          cartPanel;
    private CheckoutPanel      checkoutPanel;
    private InventoryPanel     inventoryPanel;
    private OrderHistoryPanel  orderHistoryPanel;

    public OnlineShoppingSystem() {
        setTitle("ShopNow — Online Shopping System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        buildUI();
        showPanel("LOGIN");
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        sideNav = buildSideNav();
        add(sideNav, BorderLayout.WEST);
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);
        loginPanel         = new LoginPanel(this);
        registerPanel      = new RegisterPanel(this);
        dashPanel          = new DashboardPanel(this);
        productEntryPanel  = new ProductEntryPanel(this);
        productSearchPanel = new ProductSearchPanel(this);
        cartPanel          = new CartPanel(this);
        checkoutPanel      = new CheckoutPanel(this);
        inventoryPanel     = new InventoryPanel(this);
        orderHistoryPanel  = new OrderHistoryPanel(this);
        contentPanel.add(loginPanel,        "LOGIN");
        contentPanel.add(registerPanel,     "REGISTER");
        contentPanel.add(dashPanel,         "DASHBOARD");
        contentPanel.add(productEntryPanel, "PRODUCT_ENTRY");
        contentPanel.add(productSearchPanel,"PRODUCT_SEARCH");
        contentPanel.add(cartPanel,         "CART");
        contentPanel.add(checkoutPanel,     "CHECKOUT");
        contentPanel.add(inventoryPanel,    "INVENTORY");
        contentPanel.add(orderHistoryPanel, "ORDER_HISTORY");
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSideNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(BG_CARD);
        nav.setPreferredSize(new Dimension(230, 820));
        nav.setBorder(BorderFactory.createMatteBorder(0,0,0,1,BORDER_COL));

        JPanel logoBlock = new JPanel(new BorderLayout());
        logoBlock.setBackground(BG_CARD);
        logoBlock.setBorder(new EmptyBorder(26,22,18,22));
        JLabel logo = new JLabel("ShopNow");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 21));
        logo.setForeground(ACCENT);
        JLabel logoSub = new JLabel("Shopping Platform");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        logoSub.setForeground(TEXT_MUTED);
        JPanel lt = new JPanel(); lt.setLayout(new BoxLayout(lt,BoxLayout.Y_AXIS));
        lt.setBackground(BG_CARD); lt.add(logo); lt.add(logoSub);
        logoBlock.add(lt);
        nav.add(logoBlock);
        nav.add(makeDivider());

        JPanel userPill = new JPanel(new BorderLayout(10,0));
        userPill.setBackground(BG_HOVER);
        userPill.setBorder(new EmptyBorder(10,16,10,16));
        userPill.setMaximumSize(new Dimension(230,52));
        JLabel avatar = new JLabel("◉");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        avatar.setForeground(ACCENT);
        userLabel = new JLabel("Not signed in");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_SUB);
        userPill.add(avatar, BorderLayout.WEST);
        userPill.add(userLabel, BorderLayout.CENTER);
        nav.add(Box.createRigidArea(new Dimension(0,8)));
        nav.add(userPill);
        nav.add(Box.createRigidArea(new Dimension(0,8)));
        nav.add(makeDivider());
        nav.add(Box.createRigidArea(new Dimension(0,8)));

        String[][] items = {
            {"⬡","Dashboard",       "DASHBOARD"},
            {"⊞","Browse Products", "PRODUCT_SEARCH"},
            {"⊡","My Cart",         "CART"},
            {"≡","My Orders",       "ORDER_HISTORY"},
            {"✦","Add Product",     "PRODUCT_ENTRY"},
            {"◈","Inventory",       "INVENTORY"},
        };
        for (String[] it : items) nav.add(makeNavBtn(it[0],it[1],it[2]));
        nav.add(Box.createVerticalGlue());
        nav.add(makeDivider());
        nav.add(makeNavBtn("⏻","Sign Out","LOGOUT"));
        nav.add(Box.createRigidArea(new Dimension(0,16)));
        return nav;
    }

    private JButton makeNavBtn(String icon, String label, String target) {
        JButton btn = new JButton(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())       g2.setColor(BG_INPUT);
                else if (getModel().isRollover()) g2.setColor(BG_HOVER);
                else                              g2.setColor(BG_CARD);
                g2.fillRoundRect(6,2,getWidth()-12,getHeight()-4,8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setLayout(new BorderLayout(10,0));
        JLabel ic=new JLabel(icon);
        ic.setFont(new Font("Segoe UI",Font.BOLD,14));
        ic.setForeground("Sign Out".equals(label)?DANGER:ACCENT);
        ic.setBorder(new EmptyBorder(0,4,0,0));
        JLabel lb=new JLabel(label);
        lb.setFont(new Font("Segoe UI",Font.PLAIN,13));
        lb.setForeground("Sign Out".equals(label)?DANGER:TEXT_PRIMARY);
        btn.add(ic,BorderLayout.WEST); btn.add(lb,BorderLayout.CENTER);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10,14,10,14));
        btn.setMaximumSize(new Dimension(230,44));
        btn.setMinimumSize(new Dimension(230,44));
        btn.setPreferredSize(new Dimension(230,44));
        btn.addActionListener(e->{
            if ("LOGOUT".equals(target)) logout(); else showPanel(target);
        });
        return btn;
    }

    private JSeparator makeDivider(){
        JSeparator s=new JSeparator();
        s.setForeground(BORDER_COL); s.setBackground(BORDER_COL);
        s.setMaximumSize(new Dimension(230,1)); return s;
    }

    void showPanel(String name){
        boolean in=loggedInCustomerId>0;
        if (!in&&!name.equals("LOGIN")&&!name.equals("REGISTER")){showPanel("LOGIN");return;}
        sideNav.setVisible(in);
        if ("CART".equals(name)          &&in) cartPanel.refresh();
        if ("INVENTORY".equals(name)     &&in) inventoryPanel.refresh();
        if ("ORDER_HISTORY".equals(name) &&in) orderHistoryPanel.refresh();
        if ("DASHBOARD".equals(name)     &&in) dashPanel.refresh();
        cardLayout.show(contentPanel,name);
    }

    void onLoginSuccess(int id,String name,String email){
        loggedInCustomerId=id; loggedInName=name; loggedInEmail=email;
        userLabel.setText(name); sideNav.setVisible(true); showPanel("DASHBOARD");
    }

    void logout(){
        loggedInCustomerId=-1; loggedInName=""; loggedInEmail=""; activeCartId=-1;
        userLabel.setText("Not signed in"); sideNav.setVisible(false); showPanel("LOGIN");
    }

    static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
    }

    // ── Shared UI helpers ──────────────────────────────────
    static JLabel makeTitle(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,24));l.setForeground(TEXT_PRIMARY);return l;}
    static JLabel makeLabel(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.PLAIN,12));l.setForeground(TEXT_SUB);return l;}
    static JTextField makeField(){
        JTextField tf=new JTextField();tf.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tf.setForeground(TEXT_PRIMARY);tf.setBackground(BG_INPUT);tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COL,1),new EmptyBorder(9,13,9,13)));return tf;
    }
    static JPasswordField makePassField(){
        JPasswordField pf=new JPasswordField();pf.setFont(new Font("Segoe UI",Font.PLAIN,13));
        pf.setForeground(TEXT_PRIMARY);pf.setBackground(BG_INPUT);pf.setCaretColor(ACCENT);
        pf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COL,1),new EmptyBorder(9,13,9,13)));return pf;
    }
    static JTextArea makeTextArea(int rows){
        JTextArea ta=new JTextArea(rows,20);ta.setFont(new Font("Segoe UI",Font.PLAIN,13));
        ta.setForeground(TEXT_PRIMARY);ta.setBackground(BG_INPUT);ta.setCaretColor(ACCENT);
        ta.setLineWrap(true);ta.setWrapStyleWord(true);ta.setBorder(new EmptyBorder(9,13,9,13));return ta;
    }
    static JComboBox<String> makeCombo(String[] items){
        JComboBox<String> cb=new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI",Font.PLAIN,13));cb.setForeground(TEXT_PRIMARY);cb.setBackground(BG_INPUT);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COL,1));
        cb.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
                super.getListCellRendererComponent(l,v,i,s,f);
                setBackground(s?BG_HOVER:BG_CARD);setForeground(TEXT_PRIMARY);setBorder(new EmptyBorder(7,13,7,13));return this;
            }
        });return cb;
    }
    static JButton makePrimaryBtn(String t){
        JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isPressed()?ACCENT.darker():getModel().isRollover()?new Color(130,190,255):ACCENT);g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);g2.dispose();super.paintComponent(g);}};
        styleBtn(b,BG_DARK);return b;
    }
    static JButton makeSuccessBtn(String t){
        JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isPressed()?SUCCESS.darker():getModel().isRollover()?new Color(80,230,170):SUCCESS);g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);g2.dispose();super.paintComponent(g);}};
        styleBtn(b,BG_DARK);return b;
    }
    static JButton makeDangerBtn(String t){
        JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isPressed()?DANGER.darker():getModel().isRollover()?new Color(255,140,140):DANGER);g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);g2.dispose();super.paintComponent(g);}};
        styleBtn(b,Color.WHITE);return b;
    }
    static JButton makeGhostBtn(String t){
        JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?BG_HOVER:BG_CARD);g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);g2.setColor(BORDER_COL);g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);g2.dispose();super.paintComponent(g);}};
        styleBtn(b,TEXT_PRIMARY);return b;
    }
    private static void styleBtn(JButton b,Color fg){
        b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setForeground(fg);b.setOpaque(false);
        b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(150,40));
    }
    static JTable makeStyledTable(String[] cols){
        DefaultTableModel m=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        JTable t=new JTable(m);
        t.setFont(new Font("Segoe UI",Font.PLAIN,13));t.setForeground(TEXT_PRIMARY);t.setBackground(BG_CARD);
        t.setGridColor(BORDER_COL);t.setRowHeight(38);t.setShowHorizontalLines(true);t.setShowVerticalLines(false);
        t.setSelectionBackground(BG_HOVER);t.setSelectionForeground(TEXT_PRIMARY);t.setIntercellSpacing(new Dimension(0,0));
        JTableHeader h=t.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,12));h.setBackground(BG_INPUT);h.setForeground(ACCENT);
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_COL));h.setPreferredSize(new Dimension(h.getWidth(),40));
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tbl,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(tbl,v,sel,foc,r,c);
                setBackground(sel?BG_HOVER:(r%2==0?BG_CARD:new Color(20,28,44)));
                setForeground(TEXT_PRIMARY);setBorder(new EmptyBorder(0,14,0,14));return this;
            }
        });return t;
    }
    static JScrollPane makeScroll(Component c){
        JScrollPane sp=new JScrollPane(c);sp.setBorder(BorderFactory.createLineBorder(BORDER_COL,1));
        sp.setBackground(BG_CARD);sp.getViewport().setBackground(BG_CARD);
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            @Override protected void configureScrollBarColors(){thumbColor=BORDER_COL;trackColor=BG_INPUT;}
            @Override protected JButton createDecreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
            @Override protected JButton createIncreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });return sp;
    }
    static JPanel makeCard(){
        JPanel p=new JPanel();p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COL,1),new EmptyBorder(20,24,20,24)));return p;
    }
    static boolean isValidEmail(String e){return Pattern.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",e);}
    static boolean isValidPhone(String p){return Pattern.matches("^[6-9]\\d{9}$",p);}

    public static void main(String[] args){
        try{Class.forName("oracle.jdbc.OracleDriver");}
        catch(ClassNotFoundException e){JOptionPane.showMessageDialog(null,"Oracle JDBC driver not found!\nAdd ojdbc8.jar to classpath.","Driver Error",JOptionPane.ERROR_MESSAGE);}
        UIManager.put("OptionPane.background",BG_CARD);UIManager.put("Panel.background",BG_CARD);UIManager.put("OptionPane.messageForeground",TEXT_PRIMARY);
        SwingUtilities.invokeLater(OnlineShoppingSystem::new);
    }
}

// ════════════════════════════════════════════════════════════════════════
//  LOGIN
// ════════════════════════════════════════════════════════════════════════
class LoginPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTextField     emailField;
    private JPasswordField passField;
    private JLabel         statusLabel;

    LoginPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new GridBagLayout());build();}

    private void build(){
        JPanel card=new JPanel(new GridBagLayout());
        card.setBackground(OnlineShoppingSystem.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(OnlineShoppingSystem.BORDER_COL,1),new EmptyBorder(44,52,40,52)));
        card.setPreferredSize(new Dimension(420,500));
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.gridwidth=2;g.weightx=1;int row=0;

        g.gridy=row++;JLabel brand=new JLabel("ShopNow",SwingConstants.CENTER);brand.setFont(new Font("Segoe UI",Font.BOLD,30));brand.setForeground(OnlineShoppingSystem.ACCENT);card.add(brand,g);
        g.gridy=row++;g.insets=new Insets(2,0,28,0);JLabel tag=new JLabel("Sign in to your account",SwingConstants.CENTER);tag.setFont(new Font("Segoe UI",Font.PLAIN,13));tag.setForeground(OnlineShoppingSystem.TEXT_MUTED);card.add(tag,g);g.insets=new Insets(0,0,0,0);

        g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Email Address"),g);
        g.gridy=row++;g.insets=new Insets(4,0,14,0);emailField=OnlineShoppingSystem.makeField();card.add(emailField,g);
        g.insets=new Insets(0,0,0,0);g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Password"),g);
        g.gridy=row++;g.insets=new Insets(4,0,8,0);passField=OnlineShoppingSystem.makePassField();card.add(passField,g);
        g.insets=new Insets(0,0,0,0);g.gridy=row++;statusLabel=new JLabel(" ",SwingConstants.CENTER);statusLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));statusLabel.setForeground(OnlineShoppingSystem.DANGER);card.add(statusLabel,g);
        g.gridy=row++;g.insets=new Insets(10,0,8,0);JButton lb=OnlineShoppingSystem.makePrimaryBtn("Sign In");lb.setPreferredSize(new Dimension(316,44));card.add(lb,g);
        g.gridy=row++;g.insets=new Insets(4,0,0,0);JButton rb=OnlineShoppingSystem.makeGhostBtn("Create New Account");rb.setPreferredSize(new Dimension(316,40));card.add(rb,g);
        g.gridy=row++;g.insets=new Insets(20,0,0,0);JLabel hint=new JLabel("Demo: admin@shop.com  /  Admin@123",SwingConstants.CENTER);hint.setFont(new Font("Segoe UI",Font.ITALIC,11));hint.setForeground(OnlineShoppingSystem.TEXT_MUTED);card.add(hint,g);

        lb.addActionListener(e->doLogin());rb.addActionListener(e->app.showPanel("REGISTER"));passField.addActionListener(e->doLogin());add(card);
    }

    private void doLogin(){
        String email=emailField.getText().trim(),pass=new String(passField.getPassword()).trim();
        if(email.isEmpty()||pass.isEmpty()){statusLabel.setText("Please enter email and password.");return;}
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("SELECT CUSTOMER_ID,FULL_NAME,EMAIL FROM CUSTOMERS WHERE EMAIL=? AND PASSWORD_HASH=? AND IS_ACTIVE=1")){
            ps.setString(1,email);ps.setString(2,pass);ResultSet rs=ps.executeQuery();
            if(rs.next()){app.onLoginSuccess(rs.getInt(1),rs.getString(2),rs.getString(3));emailField.setText("");passField.setText("");statusLabel.setText(" ");}
            else statusLabel.setText("Invalid email or password.");
        }catch(SQLException ex){statusLabel.setText("Connection error.");}
    }
}

// ════════════════════════════════════════════════════════════════════════
//  REGISTER  (with inline validation)
// ════════════════════════════════════════════════════════════════════════
class RegisterPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTextField     nameField,emailField,phoneField;
    private JPasswordField passField,confirmField;
    private JLabel         nameErr,emailErr,phoneErr,passErr;

    RegisterPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new GridBagLayout());build();}

    private JLabel err(){JLabel l=new JLabel(" ");l.setFont(new Font("Segoe UI",Font.PLAIN,11));l.setForeground(OnlineShoppingSystem.DANGER);return l;}

    private void build(){
        JPanel card=new JPanel(new GridBagLayout());
        card.setBackground(OnlineShoppingSystem.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(OnlineShoppingSystem.BORDER_COL,1),new EmptyBorder(36,48,36,48)));
        card.setPreferredSize(new Dimension(500,640));
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.gridwidth=2;g.weightx=1;int row=0;

        g.gridy=row++;JLabel title=new JLabel("Create Account",SwingConstants.CENTER);title.setFont(new Font("Segoe UI",Font.BOLD,26));title.setForeground(OnlineShoppingSystem.TEXT_PRIMARY);card.add(title,g);
        g.gridy=row++;g.insets=new Insets(2,0,22,0);JLabel sub=new JLabel("Join ShopNow — it's free",SwingConstants.CENTER);sub.setFont(new Font("Segoe UI",Font.PLAIN,13));sub.setForeground(OnlineShoppingSystem.TEXT_MUTED);card.add(sub,g);g.insets=new Insets(0,0,0,0);

        g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Full Name *"),g);
        g.gridy=row++;g.insets=new Insets(4,0,2,0);nameField=OnlineShoppingSystem.makeField();card.add(nameField,g);
        g.gridy=row++;g.insets=new Insets(0,0,10,0);nameErr=err();card.add(nameErr,g);g.insets=new Insets(0,0,0,0);

        g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Email Address *"),g);
        g.gridy=row++;g.insets=new Insets(4,0,2,0);emailField=OnlineShoppingSystem.makeField();card.add(emailField,g);
        g.gridy=row++;g.insets=new Insets(0,0,10,0);emailErr=err();card.add(emailErr,g);g.insets=new Insets(0,0,0,0);

        g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Phone Number * (10-digit Indian mobile starting with 6-9)"),g);
        g.gridy=row++;g.insets=new Insets(4,0,2,0);phoneField=OnlineShoppingSystem.makeField();card.add(phoneField,g);
        g.gridy=row++;g.insets=new Insets(0,0,10,0);phoneErr=err();card.add(phoneErr,g);g.insets=new Insets(0,0,0,0);

        g.gridwidth=1;g.weightx=0.5;
        g.gridy=row;g.gridx=0;g.insets=new Insets(0,0,4,6);card.add(OnlineShoppingSystem.makeLabel("Password *"),g);
        g.gridx=1;g.insets=new Insets(0,6,4,0);card.add(OnlineShoppingSystem.makeLabel("Confirm Password *"),g);row++;
        g.gridy=row;g.gridx=0;g.insets=new Insets(0,0,2,6);passField=OnlineShoppingSystem.makePassField();card.add(passField,g);
        g.gridx=1;g.insets=new Insets(0,6,2,0);confirmField=OnlineShoppingSystem.makePassField();card.add(confirmField,g);row++;
        g.gridwidth=2;g.gridx=0;g.gridy=row++;g.insets=new Insets(0,0,10,0);passErr=err();card.add(passErr,g);g.insets=new Insets(0,0,0,0);

        g.gridy=row++;g.insets=new Insets(8,0,6,0);JButton rb=OnlineShoppingSystem.makePrimaryBtn("Register");rb.setPreferredSize(new Dimension(400,44));card.add(rb,g);
        g.gridy=row++;g.insets=new Insets(0,0,0,0);JButton bb=OnlineShoppingSystem.makeGhostBtn("Back to Login");bb.setPreferredSize(new Dimension(400,40));card.add(bb,g);

        rb.addActionListener(e->doRegister());bb.addActionListener(e->app.showPanel("LOGIN"));add(card);
    }

    private void doRegister(){
        String name=nameField.getText().trim(),email=emailField.getText().trim(),phone=phoneField.getText().trim();
        String pass=new String(passField.getPassword()),conf=new String(confirmField.getPassword());
        boolean ok=true;
        nameErr.setText(" ");emailErr.setText(" ");phoneErr.setText(" ");passErr.setText(" ");
        if(name.isEmpty()){nameErr.setText("Name is required.");ok=false;}
        if(!OnlineShoppingSystem.isValidEmail(email)){emailErr.setText("Enter a valid email (e.g. name@domain.com).");ok=false;}
        if(!OnlineShoppingSystem.isValidPhone(phone)){phoneErr.setText("Enter a valid 10-digit mobile number starting 6–9.");ok=false;}
        if(pass.length()<6){passErr.setText("Password must be at least 6 characters.");ok=false;}
        else if(!pass.equals(conf)){passErr.setText("Passwords do not match.");ok=false;}
        if(!ok)return;
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "INSERT INTO CUSTOMERS(CUSTOMER_ID,FULL_NAME,EMAIL,PHONE,PASSWORD_HASH) VALUES(SEQ_CUSTOMER_ID.NEXTVAL,?,?,?,?)")){
            ps.setString(1,name);ps.setString(2,email);ps.setString(3,phone);ps.setString(4,pass);ps.executeUpdate();
            JOptionPane.showMessageDialog(app,"Account created! Please log in.","Success",JOptionPane.INFORMATION_MESSAGE);
            app.showPanel("LOGIN");
        }catch(SQLException ex){
            if(ex.getMessage().contains("ORA-00001")||ex.getMessage().toLowerCase().contains("unique"))
                emailErr.setText("This email is already registered.");
            else JOptionPane.showMessageDialog(app,"Error: "+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ════════════════════════════════════════════════════════════════════════
//  DASHBOARD  — per-user real data, greeting, professional metrics
// ════════════════════════════════════════════════════════════════════════
class DashboardPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JLabel custVal,prodVal,ordVal,revVal,cartVal,myOrdVal,greetLabel,timeLabel;

    DashboardPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel header=new JPanel(new BorderLayout());header.setBackground(OnlineShoppingSystem.BG_DARK);header.setBorder(new EmptyBorder(32,36,20,36));
        greetLabel=new JLabel("Welcome!");greetLabel.setFont(new Font("Segoe UI",Font.BOLD,26));greetLabel.setForeground(OnlineShoppingSystem.TEXT_PRIMARY);
        timeLabel=new JLabel("");timeLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));timeLabel.setForeground(OnlineShoppingSystem.TEXT_MUTED);
        JPanel gbox=new JPanel();gbox.setLayout(new BoxLayout(gbox,BoxLayout.Y_AXIS));gbox.setBackground(OnlineShoppingSystem.BG_DARK);
        gbox.add(greetLabel);gbox.add(Box.createRigidArea(new Dimension(0,4)));gbox.add(timeLabel);
        header.add(gbox,BorderLayout.WEST);add(header,BorderLayout.NORTH);

        JPanel body=new JPanel();body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));body.setBackground(OnlineShoppingSystem.BG_DARK);body.setBorder(new EmptyBorder(0,36,36,36));

        body.add(sectionLbl("STORE OVERVIEW"));body.add(Box.createRigidArea(new Dimension(0,12)));
        JPanel row1=new JPanel(new GridLayout(1,4,14,0));row1.setBackground(OnlineShoppingSystem.BG_DARK);row1.setMaximumSize(new Dimension(3000,105));
        custVal=new JLabel("—");prodVal=new JLabel("—");ordVal=new JLabel("—");revVal=new JLabel("—");
        row1.add(statCard("Customers","👥",custVal,OnlineShoppingSystem.ACCENT));
        row1.add(statCard("Active Products","📦",prodVal,OnlineShoppingSystem.SUCCESS));
        row1.add(statCard("Total Orders","🛒",ordVal,OnlineShoppingSystem.ACCENT2));
        row1.add(statCard("Revenue (₹)","💰",revVal,OnlineShoppingSystem.PURPLE));
        body.add(row1);body.add(Box.createRigidArea(new Dimension(0,28)));

        body.add(sectionLbl("MY ACCOUNT"));body.add(Box.createRigidArea(new Dimension(0,12)));
        JPanel row2=new JPanel(new GridLayout(1,2,14,0));row2.setBackground(OnlineShoppingSystem.BG_DARK);row2.setMaximumSize(new Dimension(3000,105));
        cartVal=new JLabel("—");myOrdVal=new JLabel("—");
        row2.add(statCard("Items in My Cart","🛍",cartVal,OnlineShoppingSystem.WARNING));
        row2.add(statCard("My Total Orders","📋",myOrdVal,OnlineShoppingSystem.ACCENT3));
        body.add(row2);body.add(Box.createRigidArea(new Dimension(0,28)));

        body.add(sectionLbl("QUICK ACTIONS"));body.add(Box.createRigidArea(new Dimension(0,12)));
        JPanel acts=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));acts.setBackground(OnlineShoppingSystem.BG_DARK);acts.setMaximumSize(new Dimension(3000,56));
        String[][] btns={{"Browse Products","PRODUCT_SEARCH"},{"My Cart","CART"},{"Add Product","PRODUCT_ENTRY"},{"Inventory","INVENTORY"},{"My Orders","ORDER_HISTORY"}};
        for(String[] b:btns){JButton btn=OnlineShoppingSystem.makeGhostBtn(b[0]);btn.setPreferredSize(new Dimension(145,40));String t=b[1];btn.addActionListener(e->app.showPanel(t));acts.add(btn);}
        body.add(acts);

        JScrollPane scroll=new JScrollPane(body);scroll.setBorder(null);scroll.setBackground(OnlineShoppingSystem.BG_DARK);scroll.getViewport().setBackground(OnlineShoppingSystem.BG_DARK);
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            @Override protected void configureScrollBarColors(){thumbColor=OnlineShoppingSystem.BORDER_COL;trackColor=OnlineShoppingSystem.BG_DARK;}
            @Override protected JButton createDecreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
            @Override protected JButton createIncreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
        });
        add(scroll,BorderLayout.CENTER);
    }

    private JLabel sectionLbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,10));l.setForeground(OnlineShoppingSystem.TEXT_MUTED);l.setAlignmentX(0f);return l;}

    private JPanel statCard(String label,String icon,JLabel valLbl,Color color){
        JPanel p=new JPanel(new BorderLayout(0,6));p.setBackground(OnlineShoppingSystem.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(OnlineShoppingSystem.BORDER_COL,1),new EmptyBorder(18,20,14,20)));
        JPanel top=new JPanel(new BorderLayout());top.setBackground(OnlineShoppingSystem.BG_CARD);
        JLabel lbl=new JLabel(label);lbl.setFont(new Font("Segoe UI",Font.PLAIN,12));lbl.setForeground(OnlineShoppingSystem.TEXT_MUTED);
        JLabel ic=new JLabel(icon);ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,18));
        top.add(lbl,BorderLayout.CENTER);top.add(ic,BorderLayout.EAST);
        valLbl.setFont(new Font("Segoe UI",Font.BOLD,28));valLbl.setForeground(color);
        JPanel bar=new JPanel();bar.setBackground(color);bar.setPreferredSize(new Dimension(10,3));
        p.add(top,BorderLayout.NORTH);p.add(valLbl,BorderLayout.CENTER);p.add(bar,BorderLayout.SOUTH);return p;
    }

    void refresh(){
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String g=hour<12?"Good morning":hour<17?"Good afternoon":"Good evening";
        greetLabel.setText(g+", "+OnlineShoppingSystem.loggedInName+"!");
        timeLabel.setText(new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy  hh:mm a").format(new java.util.Date()));
        try(Connection con=OnlineShoppingSystem.getConnection()){
            custVal.setText(q(con,"SELECT COUNT(*) FROM CUSTOMERS WHERE IS_ACTIVE=1"));
            prodVal.setText(q(con,"SELECT COUNT(*) FROM PRODUCTS WHERE IS_ACTIVE=1"));
            ordVal.setText(q(con,"SELECT COUNT(*) FROM ORDERS"));
            revVal.setText(q(con,"SELECT NVL(ROUND(SUM(TOTAL_AMOUNT),0),0) FROM ORDERS"));
            int cid=OnlineShoppingSystem.loggedInCustomerId;
            cartVal.setText(q(con,"SELECT NVL(SUM(ci.QUANTITY),0) FROM CART_ITEMS ci JOIN CART ca ON ci.CART_ID=ca.CART_ID WHERE ca.CUSTOMER_ID="+cid+" AND ca.STATUS='ACTIVE'"));
            myOrdVal.setText(q(con,"SELECT COUNT(*) FROM ORDERS WHERE CUSTOMER_ID="+cid));
        }catch(SQLException e){/* silent */}
    }
    private String q(Connection c,String sql) throws SQLException{Statement s=c.createStatement();ResultSet r=s.executeQuery(sql);r.next();return r.getString(1);}
}

// ════════════════════════════════════════════════════════════════════════
//  PRODUCT ENTRY
// ════════════════════════════════════════════════════════════════════════
class ProductEntryPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTextField nameField,priceField,stockField;
    private JTextArea  descField;
    private JComboBox<String> catCombo;
    private int[] catIds;

    ProductEntryPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel header=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));header.setBackground(OnlineShoppingSystem.BG_DARK);header.setBorder(new EmptyBorder(32,36,16,36));header.add(OnlineShoppingSystem.makeTitle("Add New Product"));add(header,BorderLayout.NORTH);
        JPanel center=new JPanel(new GridBagLayout());center.setBackground(OnlineShoppingSystem.BG_DARK);
        JPanel card=OnlineShoppingSystem.makeCard();card.setLayout(new GridBagLayout());card.setPreferredSize(new Dimension(540,490));
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.gridwidth=2;g.weightx=1;int row=0;

        g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Product Name *"),g);
        g.gridy=row++;g.insets=new Insets(4,0,14,0);nameField=OnlineShoppingSystem.makeField();card.add(nameField,g);
        g.insets=new Insets(0,0,0,0);g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Category *"),g);
        g.gridy=row++;g.insets=new Insets(4,0,14,0);loadCatCombo();card.add(catCombo,g);

        g.gridwidth=1;g.weightx=0.5;
        g.gridy=row;g.gridx=0;g.insets=new Insets(0,0,4,6);card.add(OnlineShoppingSystem.makeLabel("Price (₹) *"),g);
        g.gridx=1;g.insets=new Insets(0,6,4,0);card.add(OnlineShoppingSystem.makeLabel("Stock Quantity *"),g);row++;
        g.gridy=row;g.gridx=0;g.insets=new Insets(0,0,14,6);priceField=OnlineShoppingSystem.makeField();card.add(priceField,g);
        g.gridx=1;g.insets=new Insets(0,6,14,0);stockField=OnlineShoppingSystem.makeField();card.add(stockField,g);
        row++;g.gridwidth=2;g.gridx=0;

        g.insets=new Insets(0,0,0,0);g.gridy=row++;card.add(OnlineShoppingSystem.makeLabel("Description (optional)"),g);
        g.gridy=row++;g.insets=new Insets(4,0,20,0);
        descField=OnlineShoppingSystem.makeTextArea(3);
        JScrollPane ds=new JScrollPane(descField);ds.setBorder(BorderFactory.createLineBorder(OnlineShoppingSystem.BORDER_COL,1));ds.setPreferredSize(new Dimension(490,72));card.add(ds,g);

        g.insets=new Insets(0,0,0,0);g.gridy=row++;
        JPanel br=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0));br.setBackground(OnlineShoppingSystem.BG_CARD);
        JButton sv=OnlineShoppingSystem.makePrimaryBtn("Save Product");sv.setPreferredSize(new Dimension(160,42));
        JButton cl=OnlineShoppingSystem.makeGhostBtn("Clear");cl.setPreferredSize(new Dimension(100,42));
        br.add(sv);br.add(cl);card.add(br,g);
        sv.addActionListener(e->save());cl.addActionListener(e->clear());
        center.add(card);add(center,BorderLayout.CENTER);
    }

    private void loadCatCombo(){
        List<String> ns=new ArrayList<>();List<Integer> is=new ArrayList<>();
        ns.add("-- Select Category --");is.add(-1);
        try(Connection c=OnlineShoppingSystem.getConnection();Statement st=c.createStatement();
            ResultSet rs=st.executeQuery("SELECT CATEGORY_ID,CATEGORY_NAME FROM CATEGORIES ORDER BY CATEGORY_NAME")){
            while(rs.next()){is.add(rs.getInt(1));ns.add(rs.getString(2));}
        }catch(SQLException e){/* silent */}
        catCombo=OnlineShoppingSystem.makeCombo(ns.toArray(new String[0]));
        catIds=is.stream().mapToInt(Integer::intValue).toArray();
    }

    private void save(){
        String nm=nameField.getText().trim(),pr=priceField.getText().trim(),sk=stockField.getText().trim(),dc=descField.getText().trim();
        int ci=catCombo.getSelectedIndex();
        if(nm.isEmpty()||pr.isEmpty()||sk.isEmpty()||ci==0){JOptionPane.showMessageDialog(app,"Fill all required fields.","Validation",JOptionPane.WARNING_MESSAGE);return;}
        try{double pv=Double.parseDouble(pr);int sv=Integer.parseInt(sk);if(pv<0||sv<0)throw new NumberFormatException();
            try(Connection con=OnlineShoppingSystem.getConnection();PreparedStatement ps=con.prepareStatement(
                "INSERT INTO PRODUCTS(PRODUCT_ID,PRODUCT_NAME,CATEGORY_ID,PRICE,STOCK_QTY,DESCRIPTION) VALUES(SEQ_PRODUCT_ID.NEXTVAL,?,?,?,?,?)")){
                ps.setString(1,nm);ps.setInt(2,catIds[ci]);ps.setDouble(3,pv);ps.setInt(4,sv);ps.setString(5,dc);ps.executeUpdate();
                JOptionPane.showMessageDialog(app,"Product saved!","Success",JOptionPane.INFORMATION_MESSAGE);clear();
            }
        }catch(NumberFormatException ex){JOptionPane.showMessageDialog(app,"Invalid price or stock.","Validation",JOptionPane.WARNING_MESSAGE);}
        catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
    private void clear(){nameField.setText("");priceField.setText("");stockField.setText("");descField.setText("");catCombo.setSelectedIndex(0);}
}

// ════════════════════════════════════════════════════════════════════════
//  PRODUCT SEARCH
// ════════════════════════════════════════════════════════════════════════
class ProductSearchPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTextField searchField;
    private JComboBox<String> catFilter;
    private JTable table;private DefaultTableModel model;

    ProductSearchPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel top=new JPanel(new BorderLayout());top.setBackground(OnlineShoppingSystem.BG_DARK);top.setBorder(new EmptyBorder(28,36,12,36));
        JPanel tr=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));tr.setBackground(OnlineShoppingSystem.BG_DARK);tr.add(OnlineShoppingSystem.makeTitle("Browse Products"));top.add(tr,BorderLayout.NORTH);
        JPanel sr=new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));sr.setBackground(OnlineShoppingSystem.BG_DARK);
        searchField=OnlineShoppingSystem.makeField();searchField.setPreferredSize(new Dimension(280,38));
        catFilter=OnlineShoppingSystem.makeCombo(getCats());catFilter.setPreferredSize(new Dimension(190,38));
        JButton sb=OnlineShoppingSystem.makePrimaryBtn("Search");sb.setPreferredSize(new Dimension(100,38));
        JButton ab=OnlineShoppingSystem.makeSuccessBtn("Add to Cart");ab.setPreferredSize(new Dimension(130,38));
        JButton rf=OnlineShoppingSystem.makeGhostBtn("↻");rf.setPreferredSize(new Dimension(50,38));
        sr.add(searchField);sr.add(catFilter);sr.add(sb);sr.add(ab);sr.add(rf);
        top.add(sr,BorderLayout.SOUTH);add(top,BorderLayout.NORTH);

        table=OnlineShoppingSystem.makeStyledTable(new String[]{"ID","Product Name","Category","Price (₹)","In Stock","Description"});
        model=(DefaultTableModel)table.getModel();
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane sp=OnlineShoppingSystem.makeScroll(table);sp.setBorder(new EmptyBorder(0,36,28,36));add(sp,BorderLayout.CENTER);

        sb.addActionListener(e->load());searchField.addActionListener(e->load());rf.addActionListener(e->load());ab.addActionListener(e->addToCart());load();
    }

    private String[] getCats(){
        List<String> l=new ArrayList<>();l.add("All Categories");
        try(Connection c=OnlineShoppingSystem.getConnection();Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT CATEGORY_NAME FROM CATEGORIES ORDER BY CATEGORY_NAME")){
            while(rs.next())l.add(rs.getString(1));
        }catch(SQLException e){/* silent */}return l.toArray(new String[0]);
    }

    void load(){
        model.setRowCount(0);String kw=searchField.getText().trim(),cat=(String)catFilter.getSelectedItem();
        try(Connection con=OnlineShoppingSystem.getConnection()){
            String sql="SELECT p.PRODUCT_ID,p.PRODUCT_NAME,c.CATEGORY_NAME,p.PRICE,p.STOCK_QTY,p.DESCRIPTION FROM PRODUCTS p JOIN CATEGORIES c ON p.CATEGORY_ID=c.CATEGORY_ID WHERE p.IS_ACTIVE=1 ";
            if(!kw.isEmpty())sql+="AND UPPER(p.PRODUCT_NAME) LIKE UPPER(?) ";
            if(cat!=null&&!cat.startsWith("All"))sql+="AND c.CATEGORY_NAME=? ";
            sql+="ORDER BY p.PRODUCT_NAME";
            PreparedStatement ps=con.prepareStatement(sql);int idx=1;
            if(!kw.isEmpty())ps.setString(idx++,"%"+kw+"%");
            if(cat!=null&&!cat.startsWith("All"))ps.setString(idx,cat);
            ResultSet rs=ps.executeQuery();
            while(rs.next())model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),String.format("%.2f",rs.getDouble(4)),rs.getInt(5),rs.getString(6)});
        }catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void addToCart() {

        // 1. Check selection
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(app, "Select a product.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int pid = (int) model.getValueAt(row, 0);
        String pname = (String) model.getValueAt(row, 1);
        int stock = (int) model.getValueAt(row, 4);

        // 2. Stock check
        if (stock <= 0) {
            JOptionPane.showMessageDialog(app, "Out of stock.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Get quantity input
        String qs = JOptionPane.showInputDialog(app, "Enter quantity (max " + stock + "):");
        if (qs == null || qs.trim().isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(qs.trim());
            if (qty <= 0 || qty > stock) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(app, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = OnlineShoppingSystem.getConnection()) {

            // 4. Get or create cart
            int cartId = getOrCreateCart(con);

            // 5. Check if item already exists in cart
            PreparedStatement check = con.prepareStatement(
                "SELECT CART_ITEM_ID, QUANTITY FROM CART_ITEMS WHERE CART_ID=? AND PRODUCT_ID=?"
            );
            check.setInt(1, cartId);
            check.setInt(2, pid);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                // 6. Update existing item
                int newQty = rs.getInt("QUANTITY") + qty;

                PreparedStatement update = con.prepareStatement(
                    "UPDATE CART_ITEMS SET QUANTITY=? WHERE CART_ITEM_ID=?"
                );
                update.setInt(1, newQty);
                update.setInt(2, rs.getInt("CART_ITEM_ID"));

                update.executeUpdate();

            } else {

                // 7. Get product price (SAFE METHOD)
                PreparedStatement priceStmt = con.prepareStatement(
                    "SELECT PRICE FROM PRODUCTS WHERE PRODUCT_ID=?"
                );
                priceStmt.setInt(1, pid);

                ResultSet priceRs = priceStmt.executeQuery();
                priceRs.next();
                double price = priceRs.getDouble("PRICE");

                // 8. Insert into cart (CORRECT MAPPING)
                PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO CART_ITEMS (CART_ITEM_ID, CART_ID, PRODUCT_ID, QUANTITY, UNIT_PRICE) " +
                    "VALUES (SEQ_CART_ITEM_ID.NEXTVAL, ?, ?, ?, ?)"
                );

                insert.setInt(1, cartId);
                insert.setInt(2, pid);
                insert.setInt(3, qty);       // ✅ QUANTITY
                insert.setDouble(4, price);  // ✅ UNIT_PRICE

                insert.executeUpdate();
            }

            // 9. Success message
            JOptionPane.showMessageDialog(app, pname + " added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(app, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getOrCreateCart(Connection con) throws SQLException{
        if(OnlineShoppingSystem.activeCartId>0)return OnlineShoppingSystem.activeCartId;
        PreparedStatement chk=con.prepareStatement("SELECT CART_ID FROM CART WHERE CUSTOMER_ID=? AND STATUS='ACTIVE' AND ROWNUM=1");
        chk.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet rs=chk.executeQuery();
        if(rs.next()){OnlineShoppingSystem.activeCartId=rs.getInt(1);return OnlineShoppingSystem.activeCartId;}
        PreparedStatement ins=con.prepareStatement("INSERT INTO CART(CART_ID,CUSTOMER_ID) VALUES(SEQ_CART_ID.NEXTVAL,?)");
        ins.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ins.executeUpdate();
        PreparedStatement gn=con.prepareStatement("SELECT MAX(CART_ID) FROM CART WHERE CUSTOMER_ID=?");
        gn.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet r2=gn.executeQuery();r2.next();
        OnlineShoppingSystem.activeCartId=r2.getInt(1);return OnlineShoppingSystem.activeCartId;
    }
}

// ════════════════════════════════════════════════════════════════════════
//  CART
// ════════════════════════════════════════════════════════════════════════
class CartPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTable table;private DefaultTableModel model;private JLabel totalLabel;

    CartPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));top.setBackground(OnlineShoppingSystem.BG_DARK);top.setBorder(new EmptyBorder(28,36,16,36));top.add(OnlineShoppingSystem.makeTitle("My Cart"));add(top,BorderLayout.NORTH);
        table=OnlineShoppingSystem.makeStyledTable(new String[]{"Item ID","Product","Unit Price (₹)","Quantity","Subtotal (₹)"});
        model=(DefaultTableModel)table.getModel();JScrollPane sp=OnlineShoppingSystem.makeScroll(table);sp.setBorder(new EmptyBorder(0,36,0,36));add(sp,BorderLayout.CENTER);
        JPanel bot=new JPanel(new BorderLayout());bot.setBackground(OnlineShoppingSystem.BG_DARK);bot.setBorder(new EmptyBorder(16,36,32,36));
        totalLabel=new JLabel("Total: ₹0.00");totalLabel.setFont(new Font("Segoe UI",Font.BOLD,20));totalLabel.setForeground(OnlineShoppingSystem.ACCENT);
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));btns.setBackground(OnlineShoppingSystem.BG_DARK);
        JButton rm=OnlineShoppingSystem.makeGhostBtn("Remove Item");JButton cl=OnlineShoppingSystem.makeDangerBtn("Clear Cart");JButton ck=OnlineShoppingSystem.makePrimaryBtn("Checkout →");
        rm.setPreferredSize(new Dimension(130,40));cl.setPreferredSize(new Dimension(110,40));ck.setPreferredSize(new Dimension(130,40));
        btns.add(rm);btns.add(cl);btns.add(ck);bot.add(totalLabel,BorderLayout.WEST);bot.add(btns,BorderLayout.EAST);add(bot,BorderLayout.SOUTH);
        rm.addActionListener(e->remove());cl.addActionListener(e->clear());ck.addActionListener(e->app.showPanel("CHECKOUT"));
    }

    void refresh(){
        model.setRowCount(0);double total=0;
        if(OnlineShoppingSystem.activeCartId<0){
            try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("SELECT CART_ID FROM CART WHERE CUSTOMER_ID=? AND STATUS='ACTIVE' AND ROWNUM=1")){
                ps.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet rs=ps.executeQuery();if(rs.next())OnlineShoppingSystem.activeCartId=rs.getInt(1);
            }catch(SQLException e){/* silent */}
        }
        if(OnlineShoppingSystem.activeCartId<0){totalLabel.setText("Total: ₹0.00");return;}
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "SELECT ci.CART_ITEM_ID,p.PRODUCT_NAME,ci.UNIT_PRICE,ci.QUANTITY,(ci.UNIT_PRICE*ci.QUANTITY) FROM CART_ITEMS ci JOIN PRODUCTS p ON ci.PRODUCT_ID=p.PRODUCT_ID WHERE ci.CART_ID=?")){
            ps.setInt(1,OnlineShoppingSystem.activeCartId);ResultSet rs=ps.executeQuery();
            while(rs.next()){double sub=rs.getDouble(5);total+=sub;model.addRow(new Object[]{rs.getInt(1),rs.getString(2),String.format("%.2f",rs.getDouble(3)),rs.getInt(4),String.format("%.2f",sub)});}
        }catch(SQLException ex){/* silent */}
        totalLabel.setText(String.format("Total: ₹%.2f",total));
    }

    private void remove(){
        int r=table.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(app,"Select an item.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        int id=(int)model.getValueAt(r,0);
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM CART_ITEMS WHERE CART_ITEM_ID=?")){ps.setInt(1,id);ps.executeUpdate();refresh();}
        catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
    private void clear(){
        if(OnlineShoppingSystem.activeCartId<0)return;
        if(JOptionPane.showConfirmDialog(app,"Clear all items?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM CART_ITEMS WHERE CART_ID=?")){ps.setInt(1,OnlineShoppingSystem.activeCartId);ps.executeUpdate();refresh();}
        catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}

// ════════════════════════════════════════════════════════════════════════
//  CHECKOUT
// ════════════════════════════════════════════════════════════════════════
class CheckoutPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTable revTable;private DefaultTableModel revModel;
    private JTextField addrField;private JComboBox<String> payCombo;
    private JLabel totalLabel,countLabel;

    CheckoutPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));top.setBackground(OnlineShoppingSystem.BG_DARK);top.setBorder(new EmptyBorder(28,36,8,36));top.add(OnlineShoppingSystem.makeTitle("Checkout"));add(top,BorderLayout.NORTH);
        JPanel main=new JPanel(new GridLayout(1,2,16,0));main.setBackground(OnlineShoppingSystem.BG_DARK);main.setBorder(new EmptyBorder(8,36,36,36));

        JPanel lc=OnlineShoppingSystem.makeCard();lc.setLayout(new BorderLayout(0,12));
        JLabel st=new JLabel("Order Summary");st.setFont(new Font("Segoe UI",Font.BOLD,15));st.setForeground(OnlineShoppingSystem.TEXT_PRIMARY);lc.add(st,BorderLayout.NORTH);
        revModel=new DefaultTableModel(new String[]{"Product","Qty","Price (₹)","Subtotal (₹)"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
        revTable=OnlineShoppingSystem.makeStyledTable(new String[]{"Product","Qty","Price (₹)","Subtotal (₹)"});revTable.setModel(revModel);
        lc.add(OnlineShoppingSystem.makeScroll(revTable),BorderLayout.CENTER);
        JPanel sf=new JPanel(new GridLayout(2,1,0,2));sf.setBackground(OnlineShoppingSystem.BG_CARD);
        countLabel=new JLabel("0 items");countLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));countLabel.setForeground(OnlineShoppingSystem.TEXT_MUTED);
        totalLabel=new JLabel("Total: ₹0.00");totalLabel.setFont(new Font("Segoe UI",Font.BOLD,18));totalLabel.setForeground(OnlineShoppingSystem.ACCENT);
        sf.add(countLabel);sf.add(totalLabel);lc.add(sf,BorderLayout.SOUTH);

        JPanel rc=OnlineShoppingSystem.makeCard();rc.setLayout(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.gridwidth=2;g.weightx=1;int row=0;
        g.gridy=row++;JLabel dt=new JLabel("Delivery Details");dt.setFont(new Font("Segoe UI",Font.BOLD,15));dt.setForeground(OnlineShoppingSystem.TEXT_PRIMARY);rc.add(dt,g);
        g.gridy=row++;g.insets=new Insets(16,0,4,0);rc.add(OnlineShoppingSystem.makeLabel("Delivery Address *"),g);
        g.gridy=row++;g.insets=new Insets(0,0,14,0);addrField=OnlineShoppingSystem.makeField();rc.add(addrField,g);
        g.insets=new Insets(0,0,4,0);g.gridy=row++;rc.add(OnlineShoppingSystem.makeLabel("Payment Method"),g);
        g.gridy=row++;g.insets=new Insets(0,0,20,0);payCombo=OnlineShoppingSystem.makeCombo(new String[]{"Cash on Delivery","UPI","Net Banking","Credit / Debit Card"});rc.add(payCombo,g);
        g.gridy=row++;g.insets=new Insets(0,0,20,0);
        JPanel info=new JPanel(new GridLayout(2,1,0,4));info.setBackground(new Color(15,35,55));info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(40,80,120),1),new EmptyBorder(12,14,12,14)));
        JLabel l1=new JLabel("📦  Estimated Delivery: 3–5 Business Days");JLabel l2=new JLabel("🔄  Return Policy: 7-Day Easy Returns");
        for(JLabel l:new JLabel[]{l1,l2}){l.setFont(new Font("Segoe UI",Font.PLAIN,12));l.setForeground(OnlineShoppingSystem.TEXT_SUB);}info.add(l1);info.add(l2);rc.add(info,g);
        g.insets=new Insets(0,0,8,0);g.gridy=row++;JButton pb=OnlineShoppingSystem.makeSuccessBtn("Place Order");pb.setPreferredSize(new Dimension(400,46));pb.setFont(new Font("Segoe UI",Font.BOLD,15));rc.add(pb,g);
        g.insets=new Insets(0,0,0,0);g.gridy=row++;JButton bb=OnlineShoppingSystem.makeGhostBtn("← Back to Cart");bb.setPreferredSize(new Dimension(400,38));rc.add(bb,g);

        main.add(lc);main.add(rc);add(main,BorderLayout.CENTER);
        pb.addActionListener(e->place());bb.addActionListener(e->app.showPanel("CART"));
    }

    @Override public void setVisible(boolean v){super.setVisible(v);if(v)loadReview();}

    private void loadReview(){
        revModel.setRowCount(0);double total=0;int items=0;
        if(OnlineShoppingSystem.activeCartId<0)return;
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "SELECT p.PRODUCT_NAME,ci.QUANTITY,ci.UNIT_PRICE,(ci.UNIT_PRICE*ci.QUANTITY) FROM CART_ITEMS ci JOIN PRODUCTS p ON ci.PRODUCT_ID=p.PRODUCT_ID WHERE ci.CART_ID=?")){
            ps.setInt(1,OnlineShoppingSystem.activeCartId);ResultSet rs=ps.executeQuery();
            while(rs.next()){double s=rs.getDouble(4);total+=s;items++;revModel.addRow(new Object[]{rs.getString(1),rs.getInt(2),String.format("%.2f",rs.getDouble(3)),String.format("%.2f",s)});}
        }catch(SQLException ex){/* silent */}
        totalLabel.setText(String.format("Total: ₹%.2f",total));countLabel.setText(items+" item(s)");
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("SELECT NVL(ADDRESS,'') FROM CUSTOMERS WHERE CUSTOMER_ID=?")){
            ps.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet rs=ps.executeQuery();if(rs.next()&&!rs.getString(1).isEmpty())addrField.setText(rs.getString(1));
        }catch(SQLException ex){/* silent */}
    }

    private void place(){
        String addr=addrField.getText().trim();
        if(addr.isEmpty()){JOptionPane.showMessageDialog(app,"Enter delivery address.","Validation",JOptionPane.WARNING_MESSAGE);return;}
        if(revModel.getRowCount()==0){JOptionPane.showMessageDialog(app,"Cart is empty.","Info",JOptionPane.WARNING_MESSAGE);return;}
        if(JOptionPane.showConfirmDialog(app,"<html>Confirm order?<br><b>"+totalLabel.getText()+"</b><br>Deliver to: "+addr+"</html>","Confirm Order",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try(Connection con=OnlineShoppingSystem.getConnection()){
            con.setAutoCommit(false);
            PreparedStatement tp=con.prepareStatement("SELECT SUM(UNIT_PRICE*QUANTITY) FROM CART_ITEMS WHERE CART_ID=?");tp.setInt(1,OnlineShoppingSystem.activeCartId);ResultSet tr=tp.executeQuery();tr.next();double total=tr.getDouble(1);
            PreparedStatement io=con.prepareStatement("INSERT INTO ORDERS(ORDER_ID,CUSTOMER_ID,TOTAL_AMOUNT,DELIVERY_ADDRESS,PAYMENT_METHOD) VALUES(SEQ_ORDER_ID.NEXTVAL,?,?,?,?)");
            io.setInt(1,OnlineShoppingSystem.loggedInCustomerId);io.setDouble(2,total);io.setString(3,addr);io.setString(4,(String)payCombo.getSelectedItem());io.executeUpdate();
            PreparedStatement go=con.prepareStatement("SELECT MAX(ORDER_ID) FROM ORDERS WHERE CUSTOMER_ID=?");go.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet or=go.executeQuery();or.next();int oid=or.getInt(1);
            // Insert order items one by one to avoid VIRTUAL column issues
            PreparedStatement gi=con.prepareStatement("SELECT PRODUCT_ID,QUANTITY,UNIT_PRICE FROM CART_ITEMS WHERE CART_ID=?");gi.setInt(1,OnlineShoppingSystem.activeCartId);ResultSet ir=gi.executeQuery();
            PreparedStatement ii=con.prepareStatement("INSERT INTO ORDER_ITEMS(ORDER_ITEM_ID,ORDER_ID,PRODUCT_ID,QUANTITY,UNIT_PRICE) VALUES(SEQ_ORDER_ITEM_ID.NEXTVAL,?,?,?,?)");
            while(ir.next()){ii.setInt(1,oid);ii.setInt(2,ir.getInt(1));ii.setInt(3,ir.getInt(2));ii.setDouble(4,ir.getDouble(3));ii.executeUpdate();}
            PreparedStatement cc=con.prepareStatement("UPDATE CART SET STATUS='CHECKED_OUT' WHERE CART_ID=?");cc.setInt(1,OnlineShoppingSystem.activeCartId);cc.executeUpdate();
            con.commit();OnlineShoppingSystem.activeCartId=-1;
            JOptionPane.showMessageDialog(app,"<html><b>Order #"+oid+" placed!</b><br>Thank you for shopping with ShopNow.</html>","Order Confirmed",JOptionPane.INFORMATION_MESSAGE);
            app.showPanel("ORDER_HISTORY");
        }catch(SQLException ex){JOptionPane.showMessageDialog(app,"Order failed: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}

// ════════════════════════════════════════════════════════════════════════
//  INVENTORY  — full edit dialog (all fields except Product ID)
// ════════════════════════════════════════════════════════════════════════
class InventoryPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTable table;private DefaultTableModel model;

    InventoryPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel top=new JPanel(new BorderLayout());top.setBackground(OnlineShoppingSystem.BG_DARK);top.setBorder(new EmptyBorder(28,36,12,36));
        JPanel tr=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));tr.setBackground(OnlineShoppingSystem.BG_DARK);tr.add(OnlineShoppingSystem.makeTitle("Inventory Management"));
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));btns.setBackground(OnlineShoppingSystem.BG_DARK);
        JButton rf=OnlineShoppingSystem.makeGhostBtn("↻ Refresh");JButton ed=OnlineShoppingSystem.makePrimaryBtn("✎ Edit Product");
        JButton sk=OnlineShoppingSystem.makeSuccessBtn("Update Stock");JButton tg=OnlineShoppingSystem.makeGhostBtn("Toggle Active");
        for(JButton b:new JButton[]{rf,ed,sk,tg})b.setPreferredSize(new Dimension(130,38));
        btns.add(rf);btns.add(ed);btns.add(sk);btns.add(tg);top.add(tr,BorderLayout.WEST);top.add(btns,BorderLayout.EAST);add(top,BorderLayout.NORTH);

        table=OnlineShoppingSystem.makeStyledTable(new String[]{"ID","Product Name","Category","Price (₹)","Stock","Status"});
        model=(DefaultTableModel)table.getModel();
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                setBackground(sel?OnlineShoppingSystem.BG_HOVER:(r%2==0?OnlineShoppingSystem.BG_CARD:new Color(20,28,44)));
                setForeground(OnlineShoppingSystem.TEXT_PRIMARY);
                if(!sel&&c==4){try{int s=Integer.parseInt(v.toString());if(s==0)setForeground(OnlineShoppingSystem.DANGER);else if(s<10)setForeground(OnlineShoppingSystem.WARNING);else setForeground(OnlineShoppingSystem.SUCCESS);}catch(Exception ignored){}}
                setBorder(new EmptyBorder(0,14,0,14));return this;
            }
        });
        JScrollPane sp=OnlineShoppingSystem.makeScroll(table);sp.setBorder(new EmptyBorder(0,36,0,36));add(sp,BorderLayout.CENTER);

        JPanel leg=new JPanel(new FlowLayout(FlowLayout.LEFT,16,0));leg.setBackground(OnlineShoppingSystem.BG_DARK);leg.setBorder(new EmptyBorder(8,36,24,36));
        lbl(leg,OnlineShoppingSystem.SUCCESS,"● In Stock (≥10)");lbl(leg,OnlineShoppingSystem.WARNING,"● Low Stock (<10)");lbl(leg,OnlineShoppingSystem.DANGER,"● Out of Stock");add(leg,BorderLayout.SOUTH);

        rf.addActionListener(e->refresh());ed.addActionListener(e->openEdit());sk.addActionListener(e->quickStock());tg.addActionListener(e->toggle());refresh();
    }
    private void lbl(JPanel p,Color c,String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.PLAIN,12));l.setForeground(c);p.add(l);}

    void refresh(){
        model.setRowCount(0);
        try(Connection c=OnlineShoppingSystem.getConnection();Statement st=c.createStatement();ResultSet rs=st.executeQuery(
            "SELECT p.PRODUCT_ID,p.PRODUCT_NAME,c.CATEGORY_NAME,p.PRICE,p.STOCK_QTY,CASE WHEN p.IS_ACTIVE=1 THEN 'Active' ELSE 'Inactive' END FROM PRODUCTS p JOIN CATEGORIES c ON p.CATEGORY_ID=c.CATEGORY_ID ORDER BY p.PRODUCT_NAME")){
            while(rs.next())model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),String.format("%.2f",rs.getDouble(4)),rs.getInt(5),rs.getString(6)});
        }catch(SQLException ex){/* silent */}
    }

    private void openEdit(){
        int row=table.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(app,"Select a product to edit.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        int pid=(int)model.getValueAt(row,0);
        // Fetch product
        String nm="";int selCatId=-1;double pr=0;int sk=0;String dc="";
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "SELECT PRODUCT_NAME,CATEGORY_ID,PRICE,STOCK_QTY,NVL(DESCRIPTION,'') FROM PRODUCTS WHERE PRODUCT_ID=?")){
            ps.setInt(1,pid);ResultSet rs=ps.executeQuery();
            if(rs.next()){nm=rs.getString(1);selCatId=rs.getInt(2);pr=rs.getDouble(3);sk=rs.getInt(4);dc=rs.getString(5);}
        }catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
        // Fetch cats
        List<String> cn=new ArrayList<>();List<Integer> ci=new ArrayList<>();int si=0;
        try(Connection c=OnlineShoppingSystem.getConnection();Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT CATEGORY_ID,CATEGORY_NAME FROM CATEGORIES ORDER BY CATEGORY_NAME")){
            int i=0;while(rs.next()){ci.add(rs.getInt(1));cn.add(rs.getString(2));if(rs.getInt(1)==selCatId)si=i;i++;}
        }catch(SQLException ex){/* silent */}

        // Build dialog
        JDialog dlg=new JDialog((java.awt.Frame)SwingUtilities.getWindowAncestor(this),"Edit Product — #"+pid,true);
        dlg.setSize(490,530);dlg.setLocationRelativeTo(app);dlg.getContentPane().setBackground(OnlineShoppingSystem.BG_DARK);

        JPanel pn=new JPanel(new GridBagLayout());pn.setBackground(OnlineShoppingSystem.BG_CARD);pn.setBorder(new EmptyBorder(28,32,28,32));
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.gridwidth=2;g.weightx=1;int r=0;

        g.gridy=r++;JLabel title=new JLabel("Edit Product Details");title.setFont(new Font("Segoe UI",Font.BOLD,18));title.setForeground(OnlineShoppingSystem.TEXT_PRIMARY);pn.add(title,g);
        g.gridy=r++;g.insets=new Insets(14,0,4,0);pn.add(OnlineShoppingSystem.makeLabel("Product Name *"),g);
        g.gridy=r++;g.insets=new Insets(0,0,12,0);JTextField nf=OnlineShoppingSystem.makeField();nf.setText(nm);pn.add(nf,g);
        g.insets=new Insets(0,0,4,0);g.gridy=r++;pn.add(OnlineShoppingSystem.makeLabel("Category *"),g);
        g.gridy=r++;g.insets=new Insets(0,0,12,0);JComboBox<String> cc=OnlineShoppingSystem.makeCombo(cn.toArray(new String[0]));cc.setSelectedIndex(si);pn.add(cc,g);

        g.gridwidth=1;g.weightx=0.5;
        g.gridy=r;g.gridx=0;g.insets=new Insets(0,0,4,6);pn.add(OnlineShoppingSystem.makeLabel("Price (₹) *"),g);
        g.gridx=1;g.insets=new Insets(0,6,4,0);pn.add(OnlineShoppingSystem.makeLabel("Stock *"),g);r++;
        g.gridy=r;g.gridx=0;g.insets=new Insets(0,0,12,6);JTextField prf=OnlineShoppingSystem.makeField();prf.setText(String.format("%.2f",pr));pn.add(prf,g);
        g.gridx=1;g.insets=new Insets(0,6,12,0);JTextField skf=OnlineShoppingSystem.makeField();skf.setText(String.valueOf(sk));pn.add(skf,g);
        r++;g.gridwidth=2;g.gridx=0;

        g.insets=new Insets(0,0,4,0);g.gridy=r++;pn.add(OnlineShoppingSystem.makeLabel("Description"),g);
        g.gridy=r++;g.insets=new Insets(0,0,20,0);JTextArea df=OnlineShoppingSystem.makeTextArea(3);df.setText(dc);
        JScrollPane ds=new JScrollPane(df);ds.setBorder(BorderFactory.createLineBorder(OnlineShoppingSystem.BORDER_COL,1));ds.setPreferredSize(new Dimension(420,68));pn.add(ds,g);

        g.insets=new Insets(0,0,0,0);g.gridy=r++;
        JPanel br=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0));br.setBackground(OnlineShoppingSystem.BG_CARD);
        JButton sv=OnlineShoppingSystem.makePrimaryBtn("Save Changes");sv.setPreferredSize(new Dimension(150,40));
        JButton cl=OnlineShoppingSystem.makeGhostBtn("Cancel");cl.setPreferredSize(new Dimension(100,40));
        br.add(sv);br.add(cl);pn.add(br,g);

        int[] ciarr=ci.stream().mapToInt(Integer::intValue).toArray();
        sv.addActionListener(e->{
            String n2=nf.getText().trim(),p2=prf.getText().trim(),s2=skf.getText().trim(),d2=df.getText().trim();
            if(n2.isEmpty()||p2.isEmpty()||s2.isEmpty()){JOptionPane.showMessageDialog(dlg,"Fill required fields.","Validation",JOptionPane.WARNING_MESSAGE);return;}
            try{double pv=Double.parseDouble(p2);int sv2=Integer.parseInt(s2);if(pv<0||sv2<0)throw new NumberFormatException();
                try(Connection con=OnlineShoppingSystem.getConnection();PreparedStatement ps=con.prepareStatement(
                    "UPDATE PRODUCTS SET PRODUCT_NAME=?,CATEGORY_ID=?,PRICE=?,STOCK_QTY=?,DESCRIPTION=? WHERE PRODUCT_ID=?")){
                    ps.setString(1,n2);ps.setInt(2,ciarr[cc.getSelectedIndex()]);ps.setDouble(3,pv);ps.setInt(4,sv2);ps.setString(5,d2);ps.setInt(6,pid);ps.executeUpdate();
                    JOptionPane.showMessageDialog(dlg,"Updated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);dlg.dispose();refresh();
                }
            }catch(NumberFormatException ex){JOptionPane.showMessageDialog(dlg,"Invalid price or stock.","Validation",JOptionPane.WARNING_MESSAGE);}
            catch(SQLException ex){JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        cl.addActionListener(e->dlg.dispose());
        dlg.getContentPane().add(pn);dlg.setVisible(true);
    }

    private void quickStock(){
        int row=table.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(app,"Select a product.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        int pid=(int)model.getValueAt(row,0);int cur=(int)model.getValueAt(row,4);
        String v=JOptionPane.showInputDialog(app,"Current: "+cur+"\nNew stock:","Update Stock",JOptionPane.QUESTION_MESSAGE);if(v==null||v.isEmpty())return;
        try{int nq=Integer.parseInt(v.trim());if(nq<0)throw new NumberFormatException();
            try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE PRODUCTS SET STOCK_QTY=? WHERE PRODUCT_ID=?")){ps.setInt(1,nq);ps.setInt(2,pid);ps.executeUpdate();refresh();}
        }catch(NumberFormatException ex){JOptionPane.showMessageDialog(app,"Invalid value.","Error",JOptionPane.ERROR_MESSAGE);}
        catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void toggle(){
        int row=table.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(app,"Select a product.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        int pid=(int)model.getValueAt(row,0);String st=(String)model.getValueAt(row,5);int nv="Active".equals(st)?0:1;
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE PRODUCTS SET IS_ACTIVE=? WHERE PRODUCT_ID=?")){ps.setInt(1,nv);ps.setInt(2,pid);ps.executeUpdate();refresh();}
        catch(SQLException ex){JOptionPane.showMessageDialog(app,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}

// ════════════════════════════════════════════════════════════════════════
//  ORDER HISTORY  — FIXED: items load correctly, no VIRTUAL column issue
// ════════════════════════════════════════════════════════════════════════
class OrderHistoryPanel extends JPanel{
    private OnlineShoppingSystem app;
    private JTable ordTbl,itmTbl;private DefaultTableModel ordMdl,itmMdl;
    private JLabel itmTitle;

    OrderHistoryPanel(OnlineShoppingSystem app){this.app=app;setBackground(OnlineShoppingSystem.BG_DARK);setLayout(new BorderLayout());build();}

    private void build(){
        JPanel top=new JPanel(new BorderLayout());top.setBackground(OnlineShoppingSystem.BG_DARK);top.setBorder(new EmptyBorder(28,36,12,36));
        JPanel tr=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));tr.setBackground(OnlineShoppingSystem.BG_DARK);tr.add(OnlineShoppingSystem.makeTitle("My Orders"));
        JButton rf=OnlineShoppingSystem.makeGhostBtn("↻ Refresh");rf.setPreferredSize(new Dimension(100,36));
        top.add(tr,BorderLayout.WEST);top.add(rf,BorderLayout.EAST);add(top,BorderLayout.NORTH);

        JPanel main=new JPanel(new GridLayout(2,1,0,14));main.setBackground(OnlineShoppingSystem.BG_DARK);main.setBorder(new EmptyBorder(0,36,28,36));

        // Orders pane
        JPanel op=new JPanel(new BorderLayout(0,8));op.setBackground(OnlineShoppingSystem.BG_DARK);
        JLabel ol=new JLabel("Orders  —  click a row to view items");ol.setFont(new Font("Segoe UI",Font.BOLD,13));ol.setForeground(OnlineShoppingSystem.TEXT_SUB);ol.setBorder(new EmptyBorder(0,2,0,0));
        op.add(ol,BorderLayout.NORTH);
        ordTbl=OnlineShoppingSystem.makeStyledTable(new String[]{"Order ID","Date","Items","Total (₹)","Status","Delivery Address","Payment"});
        ordMdl=(DefaultTableModel)ordTbl.getModel();
        ordTbl.getColumnModel().getColumn(0).setMaxWidth(70);ordTbl.getColumnModel().getColumn(1).setPreferredWidth(100);ordTbl.getColumnModel().getColumn(2).setMaxWidth(60);ordTbl.getColumnModel().getColumn(3).setPreferredWidth(100);ordTbl.getColumnModel().getColumn(4).setPreferredWidth(90);
        // Status color renderer
        ordTbl.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                setBackground(sel?OnlineShoppingSystem.BG_HOVER:(r%2==0?OnlineShoppingSystem.BG_CARD:new Color(20,28,44)));
                setForeground(OnlineShoppingSystem.TEXT_PRIMARY);
                if(!sel&&c==4&&v!=null){switch(v.toString()){case"PLACED":setForeground(OnlineShoppingSystem.ACCENT);break;case"SHIPPED":setForeground(OnlineShoppingSystem.WARNING);break;case"DELIVERED":setForeground(OnlineShoppingSystem.SUCCESS);break;case"CANCELLED":setForeground(OnlineShoppingSystem.DANGER);break;}}
                setBorder(new EmptyBorder(0,14,0,14));return this;
            }
        });
        op.add(OnlineShoppingSystem.makeScroll(ordTbl),BorderLayout.CENTER);

        // Items pane
        JPanel ip=new JPanel(new BorderLayout(0,8));ip.setBackground(OnlineShoppingSystem.BG_DARK);
        itmTitle=new JLabel("Order Items  —  select an order above");itmTitle.setFont(new Font("Segoe UI",Font.BOLD,13));itmTitle.setForeground(OnlineShoppingSystem.TEXT_SUB);itmTitle.setBorder(new EmptyBorder(0,2,0,0));
        ip.add(itmTitle,BorderLayout.NORTH);
        itmTbl=OnlineShoppingSystem.makeStyledTable(new String[]{"Product Name","Quantity","Unit Price (₹)","Subtotal (₹)"});
        itmMdl=(DefaultTableModel)itmTbl.getModel();
        itmTbl.getColumnModel().getColumn(1).setMaxWidth(80);itmTbl.getColumnModel().getColumn(2).setPreferredWidth(120);itmTbl.getColumnModel().getColumn(3).setPreferredWidth(120);
        ip.add(OnlineShoppingSystem.makeScroll(itmTbl),BorderLayout.CENTER);

        main.add(op);main.add(ip);add(main,BorderLayout.CENTER);

        ordTbl.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())loadItems();});
        rf.addActionListener(e->refresh());
    }

    void refresh(){
        ordMdl.setRowCount(0);itmMdl.setRowCount(0);
        itmTitle.setText("Order Items  —  select an order above");
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "SELECT o.ORDER_ID, TO_CHAR(o.ORDER_DATE,'DD-Mon-YYYY'), " +
            "(SELECT COUNT(*) FROM ORDER_ITEMS oi2 WHERE oi2.ORDER_ID=o.ORDER_ID), " +
            "o.TOTAL_AMOUNT, o.STATUS, o.DELIVERY_ADDRESS, o.PAYMENT_METHOD " +
            "FROM ORDERS o WHERE o.CUSTOMER_ID=? ORDER BY o.ORDER_DATE DESC")){
            ps.setInt(1,OnlineShoppingSystem.loggedInCustomerId);ResultSet rs=ps.executeQuery();
            while(rs.next())ordMdl.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getInt(3),String.format("%.2f",rs.getDouble(4)),rs.getString(5),rs.getString(6),rs.getString(7)});
        }catch(SQLException ex){/* silent */}
    }

    private void loadItems(){
        itmMdl.setRowCount(0);int row=ordTbl.getSelectedRow();if(row<0)return;
        int oid=(int)ordMdl.getValueAt(row,0);
        itmTitle.setText("Order Items  —  Order #"+oid);
        try(Connection c=OnlineShoppingSystem.getConnection();PreparedStatement ps=c.prepareStatement(
            "SELECT p.PRODUCT_NAME, oi.QUANTITY, oi.UNIT_PRICE, (oi.QUANTITY * oi.UNIT_PRICE) " +
            "FROM ORDER_ITEMS oi JOIN PRODUCTS p ON oi.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE oi.ORDER_ID = ? ORDER BY p.PRODUCT_NAME")){
            ps.setInt(1,oid);ResultSet rs=ps.executeQuery();
            boolean any=false;
            while(rs.next()){any=true;itmMdl.addRow(new Object[]{rs.getString(1),rs.getInt(2),String.format("%.2f",rs.getDouble(3)),String.format("%.2f",rs.getDouble(4))});}
            if(!any)itmMdl.addRow(new Object[]{"No items recorded for this order.","—","—","—"});
        }catch(SQLException ex){JOptionPane.showMessageDialog(app,"Error loading items: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}
