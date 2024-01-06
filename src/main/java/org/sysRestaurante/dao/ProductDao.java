package org.sysRestaurante.dao;

import java.security.PublicKey;

public class ProductDao {
    private int idProduct;
    private int idCategory;
    private int quantity;
    private int supply;
    private int minSupply;
    private int sold;
    private long barCode;
    private double sellPrice;
    private double buyPrice;
    private double total;
    private boolean menuItem;
    private boolean trackStock;
    private boolean ingredient;
    private String description;
    private String categoryDescription;
    private CategoryDao categoryDao = new CategoryDao();

    public ProductDao() {
        quantity = 1;
        total = 0.0;
    }

    public ProductDao(ProductDao copy)  {
        this.setQuantity(copy.getQuantity());
        this.setSold(copy.getSold());
        this.setIdProduct(copy.getIdProduct());
        this.setCategoryDao(copy.getCategoryDao());
        this.setDescription(copy.getDescription());
        this.setTotal(copy.getTotal());
        this.setSupply(copy.getSupply());
        this.setMinSupply(copy.getSupply());
        this.setBarCode(copy.getBarCode());
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public boolean isIngredient() {
        return ingredient;
    }

    public void setIngredient(boolean ingredient) {
        this.ingredient = ingredient;
    }

    public boolean isMenuItem() {
        return menuItem;
    }

    public void setMenuItem(boolean menuItem) {
        this.menuItem = menuItem;
    }

    public boolean isTrackStock() {
        return trackStock;
    }

    public void setTrackStock(boolean trackStock) {
        this.trackStock = trackStock;
    }

    public int getMinSupply() {
        return minSupply;
    }

    public void setMinSupply(int minSupply) {
        this.minSupply = minSupply;
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
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

    public String getCategoryDescription() {
        return categoryDao.getCategoryDescription();
    }

    public static class CategoryDao {

        public enum Type {
            DRINK(1), LUNCH(2), TASTE(3), EXTRA_PORTION(4), NONE(5);

            final private int value;
            Type(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }
        }

        private String categoryDescription;
        private int idCategory;

        public CategoryDao() {
            this.categoryDescription = "Sem categoria";
            this.idCategory = Type.NONE.getValue();
        }

        public String getCategoryDescription() {
            return categoryDescription;
        }

        public void setCategoryDescription(String categoryDescription) {
            this.categoryDescription = categoryDescription;
        }

        public int getIdCategory() {
            return idCategory;
        }

        public void setIdCategory(int idCategory) {
            this.idCategory = idCategory;
        }
    }
}
