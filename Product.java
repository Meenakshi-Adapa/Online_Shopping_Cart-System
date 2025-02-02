public class Product {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int quantity;  // Added quantity field

    // Constructor to initialize the product
    public Product(int id, String name, String description, String imageUrl, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = 1;  // Default quantity is 1
    }

    // Getters for each field
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Overriding toString() method to display product details in the JList
    @Override
    public String toString() {
        return name + " - $" + price;  // This will be displayed in the list
    }
}
