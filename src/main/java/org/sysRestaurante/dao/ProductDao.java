package org.sysRestaurante.dao;

public class ProductDao {
    private int idProduct;
    private int idCategory;
    private int quantity;
    private long barCode;
    private double sellPrice;
    private double buyPrice;
    private double total;
    private String description;
    private String category;
    private CategoryDao categoryDao;

    public ProductDao() {
        quantity = 1;
        total = 0.0;
    }

    @Deprecated
    public void setCategory(int idCategory) {
        switch (idCategory) {
            case 1:
                category = "Bebidas";
                break;
            case 2:
                category = "Almoços";
                break;
            case 3:
                category = "Tira-gosto";
                break;
            case 4:
                category = "Porção extra";
            default:
                category = "Sem categoria";
                break;
        }
    }

    @Deprecated
    public String getCategory() {
        return this.category;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getBarCode() {
        return barCode;
    }

    public void setBarCode(long barCode) {
        this.barCode = barCode;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void incrementsQuantity() {
        this.quantity += 1;
    }

    public void incrementsQuantity(int qty) {
        this.quantity += qty;
    }

    public static class CategoryDao {
        private String description;
        private int idCategory;

        public CategoryDao() {
            setIdCategory(5);
            setDescription("Sem categoria");
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getIdCategory() {
            return idCategory;
        }

        public void setIdCategory(int idCategory) {
            this.idCategory = idCategory;
        }
    }
}
