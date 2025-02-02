import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersFrame extends JFrame {
    private int userId;
    private JPanel ordersPanel;
    private List<JCheckBox> orderCheckboxes;

    public OrdersFrame(int userId) {
        this.userId = userId;
        setTitle("My Orders");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for orders list
        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for cancel button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelOrderButton = new JButton("Cancel Selected Orders");
        cancelOrderButton.setBackground(new Color(65, 105, 225)); // Royal blue
        cancelOrderButton.setForeground(Color.WHITE);
        cancelOrderButton.addActionListener(e -> cancelSelectedOrders());
        buttonPanel.add(cancelOrderButton);
        add(buttonPanel, BorderLayout.SOUTH);

        orderCheckboxes = new ArrayList<>();
        loadOrders();
    }

    private void loadOrders() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String query = "SELECT o.id AS order_id, o.total_amount, o.order_date, o.order_day, " +
                    "p.id AS product_id, p.name, p.description, p.price, p.image_url, od.quantity " +
                    "FROM orders o " +
                    "JOIN order_details od ON o.id = od.order_id " +
                    "JOIN products p ON od.product_id = p.id " +
                    "WHERE o.user_id = ? " +
                    "ORDER BY o.order_date DESC, p.id ASC"; // Ensure proper product ordering
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    int currentOrderId = -1;
                    JPanel orderPanel = null;
                    while (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        if (orderId != currentOrderId) {
                            // Add the previous order panel to the UI
                            if (orderPanel != null) {
                                ordersPanel.add(orderPanel);
                                ordersPanel.add(Box.createVerticalStrut(10)); // Add spacing
                            }
                            currentOrderId = orderId;
                            orderPanel = createOrderPanel(rs);
                        }
                        addProductToOrderPanel(orderPanel, rs); // Add product to the current order
                    }
                    // Add the last order panel
                    if (orderPanel != null) {
                        ordersPanel.add(orderPanel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load orders. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel createOrderPanel(ResultSet rs) throws SQLException {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BorderLayout());
        orderPanel.setBorder(new LineBorder(Color.GRAY, 1));
        orderPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        int orderId = rs.getInt("order_id");

        // Order details at the top
        JPanel orderDetailsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        orderDetailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        orderDetailsPanel.setBackground(new Color(230, 230, 230)); // Slightly darker gray

        JLabel orderIdLabel = new JLabel("Order ID: " + orderId);
        JLabel orderDateLabel = new JLabel("Date: " + rs.getDate("order_date"));
        JLabel orderDayLabel = new JLabel("Day: " + rs.getString("order_day"));
        JLabel totalAmountLabel = new JLabel("Total Amount: $" + rs.getDouble("total_amount"));

        orderDetailsPanel.add(orderIdLabel);
        orderDetailsPanel.add(orderDateLabel);
        orderDetailsPanel.add(orderDayLabel);
        orderDetailsPanel.add(totalAmountLabel);

        orderPanel.add(orderDetailsPanel, BorderLayout.NORTH);

        // Products list in the order
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        productsPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        orderPanel.add(productsPanel, BorderLayout.CENTER);

        // Checkbox for selecting the order
        JCheckBox orderCheckBox = new JCheckBox("Select Order");
        orderCheckBox.setBackground(new Color(245, 245, 245)); // Light gray background
        orderCheckboxes.add(orderCheckBox);
        orderPanel.add(orderCheckBox, BorderLayout.SOUTH);

        return orderPanel;
    }

    private void addProductToOrderPanel(JPanel orderPanel, ResultSet rs) throws SQLException {
        JPanel productsPanel = (JPanel) orderPanel.getComponent(1); // Center component

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        productPanel.setPreferredSize(new Dimension(200, 150)); // Fixed size for each product
        productPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        productPanel.setBackground(Color.WHITE);

        // Product image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String imageUrl = rs.getString("image_url");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        }
        productPanel.add(imageLabel, BorderLayout.WEST);

        // Product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        detailsPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name: " + rs.getString("name"));
        JLabel priceLabel = new JLabel("Price: $" + rs.getDouble("price"));
        JLabel quantityLabel = new JLabel("Quantity: " + rs.getInt("quantity"));

        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(quantityLabel);

        productPanel.add(detailsPanel, BorderLayout.CENTER);

        productsPanel.add(productPanel); // Add product panel to products panel
    }

    private void cancelSelectedOrders() {
        List<Integer> selectedOrderIds = new ArrayList<>();
        for (int i = 0; i < ordersPanel.getComponentCount(); i++) {
            Component component = ordersPanel.getComponent(i);
            if (component instanceof JPanel) {
                JPanel orderPanel = (JPanel) component;
                JCheckBox orderCheckBox = (JCheckBox) orderPanel.getComponent(2); // The checkbox is the third component
                if (orderCheckBox.isSelected()) {
                    JPanel orderDetailsPanel = (JPanel) orderPanel.getComponent(0);
                    JLabel orderIdLabel = (JLabel) orderDetailsPanel.getComponent(0);
                    int orderId = Integer.parseInt(orderIdLabel.getText().split(": ")[1]);
                    selectedOrderIds.add(orderId);
                }
            }
        }

        if (selectedOrderIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders selected for cancellation.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel the selected orders?", "Cancel Orders", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
                String deleteOrderDetailsQuery = "DELETE FROM order_details WHERE order_id = ?";
                String deleteOrderQuery = "DELETE FROM orders WHERE id = ?";

                for (int orderId : selectedOrderIds) {
                    // Delete order details first
                    try (PreparedStatement stmt = connection.prepareStatement(deleteOrderDetailsQuery)) {
                        stmt.setInt(1, orderId);
                        stmt.executeUpdate();
                    }

                    // Delete the order itself
                    try (PreparedStatement stmt = connection.prepareStatement(deleteOrderQuery)) {
                        stmt.setInt(1, orderId);
                        stmt.executeUpdate();
                    }
                }

                // Remove the selected order panels from the UI
                SwingUtilities.invokeLater(() -> {
                    for (int i = ordersPanel.getComponentCount() - 1; i >= 0; i--) {
                        Component component = ordersPanel.getComponent(i);
                        if (component instanceof JPanel) {
                            JPanel orderPanel = (JPanel) component;
                            JCheckBox orderCheckBox = (JCheckBox) orderPanel.getComponent(2); // The checkbox is the third component
                            if (orderCheckBox.isSelected()) {
                                ordersPanel.remove(i);
                            }
                        }
                    }

                    ordersPanel.revalidate();
                    ordersPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Selected orders canceled successfully!");
                });
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to cancel the selected orders. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrdersFrame frame = new OrdersFrame(1); // Example user ID
            frame.setVisible(true);
        });
    }
}



