import javax.swing.*; 
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel productPanel;
    private JLabel totalAmountLabel;
    private double totalAmount;
    private int userId;

    public BillingFrame(int userId) {
        this.userId = userId;
        setTitle("Billing");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Panel for displaying product details
        productPanel = new JPanel();
        productPanel.setBackground(new Color(255, 187, 187));
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around grid
        JScrollPane scrollPane = new JScrollPane(productPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Total amount label
        totalAmountLabel = new JLabel("Total: RS0.00");
        totalAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(totalAmountLabel, BorderLayout.SOUTH);

        // Place Order Button and Text Bar
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        placeOrderButton.addActionListener(e -> placeOrder());

        JLabel topBarText = new JLabel("Get..Set..Go..the items are your's noww.....");
        topBarText.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
        topBarText.setForeground(new Color(255, 255, 255));
        topBarText.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(0, 0, 113));
        buttonPanel.add(topBarText, BorderLayout.CENTER);
        buttonPanel.add(placeOrderButton, BorderLayout.EAST);
        getContentPane().add(buttonPanel, BorderLayout.NORTH);

        // Load the order details for the logged-in user
        loadOrderDetails();
    }

    public void loadOrderDetails() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String query = "SELECT p.id, p.name, p.description, p.price, p.image_url, b.quantity " +
                    "FROM billing b " +
                    "JOIN products p ON b.product_id = p.id " +
                    "WHERE b.user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int productId = rs.getInt("id");
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                        double price = rs.getDouble("price");
                        String imageUrl = rs.getString("image_url");
                        int quantity = rs.getInt("quantity");

                        Product product = new Product(productId, name, description, imageUrl, price);
                        product.setQuantity(quantity);
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        productPanel.removeAll(); // Clear existing panels
        for (Product product : products) {
            addProductToBilling(product);
        }
        updateTotalAmount();
    }

    public void addProductToBilling(Product product) {
        JPanel singleProductPanel = new JPanel();
        singleProductPanel.setLayout(new BorderLayout());
        singleProductPanel.setBorder(new LineBorder(Color.GRAY, 1)); // Add border
        singleProductPanel.setMaximumSize(new Dimension(800, 200)); // Restrict max size for dynamic adjustment

        // Set Product object as client property for later retrieval
        singleProductPanel.putClientProperty("product", product);

        // Image and Remove Button Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        JLabel productImageLabel = new JLabel();
        if (product.getImageUrl() != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(product.getImageUrl()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            productImageLabel.setIcon(icon);
            productImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeProductFromBilling(product));
        imagePanel.add(productImageLabel, BorderLayout.CENTER);
        imagePanel.add(removeButton, BorderLayout.SOUTH);

        singleProductPanel.add(imagePanel, BorderLayout.WEST);

        // Product Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding inside details panel

        JLabel productNameLabel = new JLabel(product.getName());
        JLabel productDescriptionLabel = new JLabel("<html><i>" + product.getDescription() + "</i></html>");
        JLabel productPriceLabel = new JLabel("Price: RS" + product.getPrice());

        detailsPanel.add(productNameLabel);
        detailsPanel.add(productDescriptionLabel);
        detailsPanel.add(productPriceLabel);

        // Quantity Controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton decreaseButton = new JButton("-");
        JLabel quantityLabel = new JLabel(String.valueOf(product.getQuantity()));
        JButton increaseButton = new JButton("+");

        decreaseButton.addActionListener(e -> updateQuantity(product, quantityLabel, -1));
        increaseButton.addActionListener(e -> updateQuantity(product, quantityLabel, 1));

        quantityPanel.add(decreaseButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(increaseButton);

        detailsPanel.add(quantityPanel);

        singleProductPanel.add(detailsPanel, BorderLayout.CENTER);

        // Add the single product panel to the main product panel
        productPanel.add(singleProductPanel);
        productPanel.revalidate();
        productPanel.repaint();

        updateTotalAmount();
    }

    private void updateQuantity(Product product, JLabel quantityLabel, int delta) {
        int newQuantity = product.getQuantity() + delta;
        if (newQuantity > 0) {
            product.setQuantity(newQuantity);
            quantityLabel.setText(String.valueOf(newQuantity));
            updateTotalAmount();
        }
    }

    private void updateTotalAmount() {
        totalAmount = 0;
        for (Component comp : productPanel.getComponents()) {
            JPanel singleProductPanel = (JPanel) comp;
            Product product = (Product) singleProductPanel.getClientProperty("product");
            JPanel detailsPanel = (JPanel) singleProductPanel.getComponent(1);
            JLabel productPriceLabel = (JLabel) detailsPanel.getComponent(2);
            double price = Double.parseDouble(productPriceLabel.getText().replace("Price: RS", "").trim());
            JLabel quantityLabel = (JLabel) ((JPanel) detailsPanel.getComponent(3)).getComponent(1);
            int quantity = Integer.parseInt(quantityLabel.getText());
            totalAmount += price * quantity;
        }
        totalAmountLabel.setText("Total: RS" + totalAmount); // Update currency symbol to RS
    }

    private void removeProductFromBilling(Product product) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String query = "DELETE FROM billing WHERE user_id = ? AND product_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, product.getId());
                stmt.executeUpdate();
            }
            productPanel.removeAll();
            loadOrderDetails();
            JOptionPane.showMessageDialog(this, "Product removed from the billing.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            // Get the current date and day
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            String currentDay = new java.text.SimpleDateFormat("EEEE").format(currentTimestamp);

            // Insert order into the orders table with date and day
            String query = "INSERT INTO orders (user_id, total_amount, order_date, order_day) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, userId);
                stmt.setDouble(2, totalAmount);
                stmt.setDate(3, currentDate);
                stmt.setString(4, currentDay);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Placing order failed, no rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        insertOrderDetails(orderId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertOrderDetails(int orderId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String query = "INSERT INTO order_details (order_id, product_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (Component comp : productPanel.getComponents()) {
                    JPanel singleProductPanel = (JPanel) comp;
                    Product product = (Product) singleProductPanel.getClientProperty("product");
                    JPanel detailsPanel = (JPanel) singleProductPanel.getComponent(1);
                    JLabel quantityLabel = (JLabel) ((JPanel) detailsPanel.getComponent(3)).getComponent(1);
                    int quantity = Integer.parseInt(quantityLabel.getText());

                    stmt.setInt(1, orderId);
                    stmt.setInt(2, product.getId());
                    stmt.setInt(3, quantity);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
