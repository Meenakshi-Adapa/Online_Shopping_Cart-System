import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;

class CartFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel productPanelContainer; // Container for product panels
    private List<Product> cartItems;
    private BillingFrame billingFrame;

    public CartFrame(List<Product> cartItems, BillingFrame billingFrame) {
        this.cartItems = cartItems;
        this.billingFrame = billingFrame;

        setTitle("Shopping Cart");
        setBounds(300, 100, 800, 600); // Increased frame size for better display
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel container for all product panels
        productPanelContainer = new JPanel(new GridBagLayout());
        productPanelContainer.setBackground(new Color(255, 187, 187));
        JScrollPane scrollPane = new JScrollPane(productPanelContainer);
        scrollPane.setPreferredSize(new Dimension(780, 550)); // Scrollable area size
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Create the title panel with text
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 0, 83)); // Same background color as the product panel
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Minny Cart Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Large and bold font for the title
        titleLabel.setForeground(new Color(255, 255, 255)); // Eye-catching color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Add to your heart");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Smaller font for the subtitle
        subtitleLabel.setForeground(new Color(192, 192, 192));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Add the title panel to the top of the frame
        getContentPane().add(titlePanel, BorderLayout.NORTH);

        // Check if cart is empty and show a message
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
        }

        // Create product panels for each item in cart
        int gridY = 0; // Grid position for products
        int gridX = 0; // Grid position for products
        for (Product product : cartItems) {
            productPanelContainer.add(createProductPanel(product), new GridBagConstraints(
                    gridX++, gridY, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0
            ));

            if (gridX >= 2) { // Move to the next row after 2 products per row
                gridX = 0;
                gridY++;
            }
        }
    }

    // The method to create each product panel
    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setPreferredSize(new Dimension(350, 250)); // Ensure all panels have the same size
        productPanel.setMaximumSize(new Dimension(350, 250));
        productPanel.setMinimumSize(new Dimension(350, 250));

        // Add a border to the product panel
        productPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Image label (square)
        JLabel imageLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(product.getImageUrl()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Image not available");
            productPanel.add(errorLabel);
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPanel.add(imageLabel);

        // Product name, price, and description
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPanel.add(nameLabel);

        JLabel priceLabel = new JLabel("RS" + product.getPrice());
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPanel.add(priceLabel);

        JLabel descriptionLabel = new JLabel("<html><div style='width: 250px; text-align: center;'>" + product.getDescription() + "</div></html>");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        productPanel.add(descriptionLabel);

        // Create a panel for buttons to align them side by side
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Align buttons horizontally with some space
        buttonPanel.setPreferredSize(new Dimension(350, 50));

        // Remove from cart button
        JButton removeButton = new JButton("Remove from Cart");
        removeButton.setBackground(new Color(0, 123, 255)); // Change to same color for both buttons
        removeButton.setForeground(Color.WHITE);
        removeButton.setPreferredSize(new Dimension(140, 30)); // Make the button smaller to fit
        removeButton.addActionListener(e -> removeProductFromCart(product, productPanel));
        buttonPanel.add(removeButton);

        // Place order button
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(new Color(0, 123, 255)); // Same color for consistency
        placeOrderButton.setForeground(Color.WHITE);
        placeOrderButton.setPreferredSize(new Dimension(140, 30)); // Make the button smaller to fit
        placeOrderButton.addActionListener(e -> placeOrder(product));
        buttonPanel.add(placeOrderButton);

        productPanel.add(buttonPanel);

        return productPanel;
    }

    private void removeProductFromCart(Product product, JPanel productPanel) {
        SwingUtilities.invokeLater(() -> {
            if (removeFromDatabase(product)) {
                productPanelContainer.remove(productPanel); // Remove the product panel from UI
                productPanelContainer.revalidate();
                productPanelContainer.repaint();
                JOptionPane.showMessageDialog(CartFrame.this, "Item removed from cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(CartFrame.this, "Failed to remove item from cart.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void placeOrder(Product product) {
        addProductToBilling(product);
    }

    private void addProductToBilling(Product product) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            // Check if the product already exists in the billing table
            String checkQuery = "SELECT COUNT(*) FROM billing WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, getUserId(CartMain1.getCurrentUser(), connection));
            checkStatement.setInt(2, product.getId());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Product already exists in the billing table
                JOptionPane.showMessageDialog(this, "Product already exists in the billing.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert the product into the billing table
            String insertBillingQuery = "INSERT INTO billing (user_id, product_id, quantity, price, product_name) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement billingStatement = connection.prepareStatement(insertBillingQuery);
            billingStatement.setInt(1, getUserId(CartMain1.getCurrentUser(), connection));
            billingStatement.setInt(2, product.getId());
            billingStatement.setInt(3, 1); // Quantity 1 for now
            billingStatement.setDouble(4, product.getPrice());
            billingStatement.setString(5, product.getName());
            billingStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product added to billing successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add product to billing.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getUserId(String username, Connection connection) {
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean removeFromDatabase(Product product) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String query = "DELETE FROM addToCart WHERE user_id = ? AND product_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getUserId(CartMain1.getCurrentUser(), connection));
            statement.setInt(2, product.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}




