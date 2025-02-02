import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JToolBar;
import java.awt.Color;
import javax.swing.SwingConstants;
//import javax.swing.JTextArea;

public class CartMain1 extends JFrame {

	    private static final long serialVersionUID = 1L;
	    private JPanel contentPane;
	    private JPanel productPanel; // Panel for products
	    private List<Product> products;
	    private List<JCheckBox> checkboxes;
	    private CartFrame cartFrame;
	    private int userId; 
	    private List<Product> cartItems = new ArrayList<>();
	    BillingFrame billingFrame = new BillingFrame(userId);  
	    private static String currentUser;
	    //private JButton addToCart; 
	

	    public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                    CartMain1 frame = new CartMain1();
	                    frame.setVisible(true);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });
	    }
   

	    public CartMain1() {
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setBounds(100, 100, 900, 600);
	        contentPane = new JPanel();
	        contentPane.setBackground(new Color(0, 198, 198)); // Updated background color
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        getContentPane().setLayout(new BorderLayout());
	        setContentPane(contentPane);
	        contentPane.setLayout(null);

	        // ADD TO CART button
	        JButton addToCart = new JButton("ADD TO CART");
	        addToCart.setBounds(692, 149, 150, 23);
	        contentPane.add(addToCart);
	        
	        addToCart.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                addSelectedProductsToCart();
	            }
	        });

	        // BILL button
	        JButton billing = new JButton("BILL");
	        billing.setBounds(692, 234, 152, 23);
	        contentPane.add(billing);

	        billing.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (getCurrentUser() == null || getCurrentUser().trim().isEmpty()) {
	                    // User is not logged in, prompt them to log in
	                    int choice = JOptionPane.showConfirmDialog(
	                            null,
	                            "You are not logged in. Would you like to log in?",
	                            "Login Required",
	                            JOptionPane.YES_NO_OPTION
	                    );

	                    if (choice == JOptionPane.YES_OPTION) {
	                        loginUser(); // Call the login method
	                        if (getCurrentUser() != null && !getCurrentUser().trim().isEmpty()) {
	                            // Login successful, proceed with opening the BillingFrame
	                            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
	                                int userId = getUserId(getCurrentUser(), connection); // Fetch the user ID for the logged-in user
	                                BillingFrame billingFrame = new BillingFrame(userId);
	                                billingFrame.loadOrderDetails(); // Optionally load user order details
	                                billingFrame.setVisible(true);
	                            } catch (SQLException ex) {
	                                ex.printStackTrace();
	                                JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
	                            }
	                        }
	                    }
	                } else {
	                    // User is already logged in, proceed with opening the BillingFrame
	                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
	                        int userId = getUserId(getCurrentUser(), connection); // Fetch the user ID for the logged-in user
	                        BillingFrame billingFrame = new BillingFrame(userId);
	                        billingFrame.loadOrderDetails(); // Optionally load user order details
	                        billingFrame.setVisible(true);
	                    } catch (SQLException ex) {
	                        ex.printStackTrace();
	                        JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            }
	        });
	        
	     // ORDERS button
	        JButton ordersButton = new JButton("ORDERS");
	        ordersButton.setBounds(692, 324, 152, 23); // Adjust the position as needed
	        contentPane.add(ordersButton);

	        ordersButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (getCurrentUser() == null || getCurrentUser().trim().isEmpty()) {
	                    // User is not logged in, prompt them to log in
	                    int choice = JOptionPane.showConfirmDialog(
	                            null,
	                            "You are not logged in. Would you like to log in?",
	                            "Login Required",
	                            JOptionPane.YES_NO_OPTION
	                    );

	                    if (choice == JOptionPane.YES_OPTION) {
	                        loginUser(); // Call the login method
	                        if (getCurrentUser() != null && !getCurrentUser().trim().isEmpty()) {
	                            // Login successful, proceed with opening the OrdersFrame
	                            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
	                                int userId = getUserId(getCurrentUser(), connection); // Fetch the user ID for the logged-in user
	                                OrdersFrame ordersFrame = new OrdersFrame(userId);
	                                ordersFrame.setVisible(true);
	                            } catch (SQLException ex) {
	                                ex.printStackTrace();
	                                JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
	                            }
	                        }
	                    }
	                } else {
	                    // User is already logged in, proceed with opening the OrdersFrame
	                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
	                        int userId = getUserId(getCurrentUser(), connection); // Fetch the user ID for the logged-in user
	                        OrdersFrame ordersFrame = new OrdersFrame(userId);
	                        ordersFrame.setVisible(true);
	                    } catch (SQLException ex) {
	                        ex.printStackTrace();
	                        JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            }
	        });


	        // Product Panel
	        productPanel = new JPanel();
	        productPanel.setBackground(new java.awt.Color(255, 128, 128)); // Background color changed
	        JScrollPane scrollPane = new JScrollPane(productPanel);
	        scrollPane.setBounds(10, 52, 641, 511);
	        productPanel.setLayout(new GridLayout(1, 0, 0, 0));
	        scrollPane.setPreferredSize(new Dimension(600, 400)); // Adjust as needed
	        getContentPane().add(scrollPane, BorderLayout.CENTER);
	        contentPane.add(scrollPane);
	        //logout
	        JButton logout = new JButton("LOGOUT");
	        logout.setBounds(692, 415, 152, 23);
	        contentPane.add(logout);
	        logout.addActionListener(e -> logoutUser());
	        //tool bar 
	        JToolBar toolBar = new JToolBar();
	        toolBar.setBounds(10, 11, 841, 30);
	        toolBar.setRollover(true);
	        toolBar.setBackground(new Color(255, 128, 128)); // Toolbar color
	        contentPane.add(toolBar);
	                        
	        JLabel lblNewLabel = new JLabel("MINNY CART                                                          ");
	        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
	        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        toolBar.add(lblNewLabel);
	                        
	        // SIGN IN button
	        JButton signIn = new JButton("SIGN IN");
	        signIn.setBackground(new Color(255, 128, 128)); // Updated color
	        toolBar.add(signIn);
	        signIn.setFont(new Font("Tahoma", Font.BOLD, 11));
	        signIn.addActionListener(e -> signInUser());
	        
	        // LOGIN button
	        JButton login = new JButton("LOGIN");
	        login.setBackground(new Color(255, 128, 128)); // Updated color
	        toolBar.add(login);
	        login.setFont(new Font("Tahoma", Font.BOLD, 11));
	        login.addActionListener(e -> loginUser());
	                        
	        JButton cartDetails = new JButton("CART");
	        cartDetails.setBackground(new Color(255, 128, 128)); // Updated color
	        cartDetails.setHorizontalAlignment(SwingConstants.LEADING);
	        toolBar.add(cartDetails);
	        cartDetails.setFont(new Font("Tahoma", Font.BOLD, 11));
	        cartDetails.addActionListener(e -> viewCart());

	        loadProducts();
	    }

	    private void loadProducts() {
	        products = fetchProductsFromDatabase(); // Fetch products from DB
	        checkboxes = new ArrayList<>();
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between items
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow components to stretch horizontally

	        for (Product product : products) {
	            JPanel productItem = new JPanel();
	            productItem.setLayout(new GridBagLayout());
	            productItem.setBackground(new Color(245, 245, 220)); // Added background color
	            productItem.setBorder(new LineBorder(Color.GRAY, 1)); // Added border
	            GridBagConstraints itemGbc = new GridBagConstraints();
	            itemGbc.insets = new Insets(15, 15, 15, 15);
	            itemGbc.gridx = 0;
	            itemGbc.gridy = 0;
	            itemGbc.gridwidth = 2; // Span across multiple columns for the image
	            itemGbc.fill = GridBagConstraints.HORIZONTAL; // Allow the image to stretch horizontally

	            // Add product image with border
	            try {
	                File file = new File(product.getImageUrl());
	                if (file.exists()) {
	                    ImageIcon imageIcon = new ImageIcon(product.getImageUrl());
	                    java.awt.Image img = imageIcon.getImage();
	                    java.awt.Image scaledImg = img.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH); // Adjust the size here
	                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
	                    imageLabel.setBorder(new LineBorder(Color.DARK_GRAY, 2)); // Border for the image
	                    productItem.add(imageLabel, itemGbc);
	                } else {
	                    JLabel errorLabel = new JLabel("Image not found");
	                    productItem.add(errorLabel, itemGbc);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                JLabel errorLabel = new JLabel("Image load error");
	                productItem.add(errorLabel, itemGbc);
	            }

	            // Add product name with increased font size
	            itemGbc.gridy++; // Move to next row
	            itemGbc.gridwidth = 1; // Reset gridwidth
	            JLabel nameLabel = new JLabel(product.getName());
	            nameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
	            productItem.add(nameLabel, itemGbc);

	            // Add product price with increased font size
	            itemGbc.gridy++; // Move to next row
	            JLabel priceLabel = new JLabel("Price: RS" + product.getPrice());
	            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Increased font size
	            productItem.add(priceLabel, itemGbc);

	            // Add product description with increased font size
	            itemGbc.gridy++; // Move to next row
	            JLabel descriptionLabel = new JLabel("<html>" + product.getDescription() + "</html>"); // For wrapping long description text
	            descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Increased font size
	            productItem.add(descriptionLabel, itemGbc);

	            // Add product checkbox
	            itemGbc.gridy++; // Move to next row
	            JCheckBox checkBox = new JCheckBox();
	            checkboxes.add(checkBox);
	            productItem.add(checkBox, itemGbc);

	            // Add productItem to productPanel
	            productPanel.add(productItem, gbc);
	            gbc.gridy++; // Move to the next row in the main panel
	        }

	        productPanel.revalidate(); // Refresh UI
	        productPanel.repaint();
	    }


    // Dynamically adjust columns when the window is resized
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        // Recalculate the columns based on the new window size
        loadProducts();  // Reload products with updated layout
    }

    private void viewCart() {
        if (getCurrentUser() == null || getCurrentUser().isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view the cart.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fetch cart items from the database and assign it to the instance-level cartItems
        cartItems = fetchCartItemsForUser(getCurrentUser());  // Assign to the instance variable

        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "No Items in Cart", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (this.cartFrame != null) {
                this.cartFrame.dispose(); // Dispose existing cartFrame to avoid multiple instances
            }
            this.cartFrame =new CartFrame(cartItems, billingFrame);
 // Assign to the instance variable
            this.cartFrame.setVisible(true);
        }
    }



    private List<Product> fetchProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601");
            String query = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String imageUrl = resultSet.getString("image_url");
                double price = resultSet.getDouble("price");
                products.add(new Product(id, name, description, imageUrl, price));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }


    private void logoutUser() {
        if (getCurrentUser() != null) {
            String username = getCurrentUser();
           setCurrentUser(null);
           resetCartAndUI();
            // Show a logout success message
            JOptionPane.showMessageDialog(this, "Successfully logged out from account: " + username, 
                    "Logout Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No user is currently logged in.", 
                    "Logout Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetCartAndUI() {
        // Clear the cart items
        if (cartItems != null) {
            cartItems.clear(); // Clear the cart list
        }

        // Debug: Check cartFrame status
        if (cartFrame != null) {
            System.out.println("Disposing of cart frame...");
            cartFrame.dispose(); // Close the cart window
            cartFrame = null;    // Remove the reference to avoid memory leaks
        } else {
            System.out.println("Cart frame is already null.");
        }

        // Reset all product checkboxes
        if (checkboxes != null) {
            for (JCheckBox checkbox : checkboxes) {
                checkbox.setSelected(false); // Uncheck each checkbox
            }
        }

        // Notify user
        JOptionPane.showMessageDialog(this, "Cart and UI have been reset successfully.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
    }



    private void signInUser() {
        String username = JOptionPane.showInputDialog(this, "Enter New Username:", "Sign In", JOptionPane.PLAIN_MESSAGE);
        String password = JOptionPane.showInputDialog(this, "Enter New Password:", "Sign In", JOptionPane.PLAIN_MESSAGE);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call signUpUser to add the new user to the database
        if (signUpUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Sign Up Successful!", "Sign In", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another one.", "Sign In", JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean signUpUser(String username, String password) {
        // Validate input (username and password should not be empty)
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            
            // Check if the username already exists
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Username already exists
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another one.", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
                resultSet.close();
                return false;
            }

            // Username is unique, proceed with insertion
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);

            int rowsAffected = insertStatement.executeUpdate();
            insertStatement.close();

            if (rowsAffected > 0) {
                // Sign up successful
                return true;
            } else {
                // Insert failed
                JOptionPane.showMessageDialog(this, "Sign up failed. Please try again.", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void addSelectedProductsToCart() {
        // Check if the user is logged in
        if (getCurrentUser() == null || getCurrentUser().isEmpty()) {
            int response = JOptionPane.showConfirmDialog(this, "You must be logged in to add items to the cart. Do you want to log in?", "Login Required", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                loginUser();
            } else {
                return; // Stop if user chooses not to log in
            }
        }

        // List to hold added products
        List<Product> addedProducts = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601");

            int userId = getUserId(getCurrentUser(), connection);
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String checkQuery = "SELECT * FROM addToCart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);

            String insertQuery = "INSERT INTO addToCart (user_id, product_id) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            boolean itemAdded = false;
            for (int i = 0; i < checkboxes.size(); i++) {
                if (checkboxes.get(i).isSelected()) {
                    Product selectedProduct = products.get(i);

                    checkStatement.setInt(1, userId);
                    checkStatement.setInt(2, selectedProduct.getId());
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (!resultSet.next()) {
                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, selectedProduct.getId());
                        insertStatement.executeUpdate();

                        addedProducts.add(selectedProduct); // Add the product to the cart list
                        itemAdded = true;

                        // Show a dialog box with the product name and price
                        JOptionPane.showMessageDialog(this,
                                "Item Added: " + selectedProduct.getName() + "\nPrice: " + selectedProduct.getPrice(),
                                "Item Added to Cart",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, selectedProduct.getName() + " is already in the cart.", "Duplicate Item", JOptionPane.WARNING_MESSAGE);
                    }

                    resultSet.close();
                }
            }

            if (itemAdded) {
                JOptionPane.showMessageDialog(this, "Products added to cart successfully!", "Cart Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No new products were added to the cart.", "No Changes", JOptionPane.WARNING_MESSAGE);
            }

            checkStatement.close();
            insertStatement.close();
            connection.close();

            resetCheckboxes();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding products to cart.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loginUser() {
        // Check if the user is already logged in
        if (getCurrentUser() != null && !getCurrentUser().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "You are already logged in as " + getCurrentUser() + ". Please log out first if you want to log in with a different account.", "Already Logged In", JOptionPane.INFORMATION_MESSAGE);
            return; // Exit the method if the user is already logged in
        }

        String username = JOptionPane.showInputDialog(this, "Enter Username:", "Login", JOptionPane.PLAIN_MESSAGE);
        String password = JOptionPane.showInputDialog(this, "Enter Password:", "Login", JOptionPane.PLAIN_MESSAGE);

        // Check if the username or password is empty
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login Failed: Username and Password cannot be empty.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;  // Exit the method if the input is invalid
        }

        // Authenticate user
        if (authenticateUser(username, password)) {
            setCurrentUser(username);
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Get userId from the authenticated username
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
                int userId = getUserId(username, connection); // Get userId using the username
                if (userId == -1) {
                    JOptionPane.showMessageDialog(this, "Error: User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Optionally store the userId for later use
                    // You can open the BillingFrame at a different point in the app
                    // BillingFrame billingFrame = new BillingFrame(userId);
                    // billingFrame.setVisible(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }



    // Example of your authenticateUser method, which should query the database for validation
    private boolean authenticateUser(String username, String password) {
        try {
        	
            // Here we will check the credentials in the users database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601");
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                resultSet.close();
                statement.close();
                connection.close();
                return true;  // User authenticated
            } else {
                resultSet.close();
                statement.close();
                connection.close();
                return false;  // Invalid credentials
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error in database connection or query
        }
    }



    // Helper method to get user_id based on the username
    private int getUserId(String username, Connection connection) {
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                resultSet.close();
                statement.close();
                return userId;
            }

            resultSet.close();
            statement.close();
            return -1; // User not found
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // In case of error
    }

    // Method to reset the checkboxes after adding products to cart
    private void resetCheckboxes() {
        for (JCheckBox checkbox : checkboxes) {
            checkbox.setSelected(false);  // Uncheck all checkboxes
        }
    }
  


    private List<Product> fetchCartItemsForUser(String username) {
        List<Product> cartItems = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopping_cart_sys3", "root", "200601")) {
            String userQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement userStmt = connection.prepareStatement(userQuery);
            userStmt.setString(1, username);
            ResultSet userResult = userStmt.executeQuery();

            if (userResult.next()) {
                int userId = userResult.getInt("id");

                String cartQuery = "SELECT p.* FROM addToCart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
                PreparedStatement cartStmt = connection.prepareStatement(cartQuery);
                cartStmt.setInt(1, userId);
                ResultSet cartResult = cartStmt.executeQuery();

                while (cartResult.next()) {
                    int id = cartResult.getInt("id");
                    String name = cartResult.getString("name");
                    String description = cartResult.getString("description");
                    String imageUrl = cartResult.getString("image_url");
                    double price = cartResult.getDouble("price");

                    cartItems.add(new Product(id, name, description, imageUrl, price));
                }
                cartResult.close();
                cartStmt.close();
            }
            userResult.close();
            userStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }


	public static String getCurrentUser() {
		return currentUser;
	}


	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
}



  




