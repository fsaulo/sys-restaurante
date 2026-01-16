import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Product;

public class ProductTest {

    private static int productId = 0;

    public ProductDao createTestProduct() {
        ProductDao product = new ProductDao();
        product.setDescription("Test");
        product.setCategoryDao(new ProductDao.CategoryDao());
        product.setIdCategory(0);
        return product;
    }

    @Test
    public void shouldInsertProduct() {
        ProductDao product = createTestProduct();
        assertNotNull(product);
        productId = Product.insert(product);
        assertNotEquals(-1, productId);
    }
    
    @Test
    public void shouldUpdateProduct() {
        ProductDao product = createTestProduct();
        assertNotNull(product);

        product.setIdProduct(productId);
        product.setDescription("Test2");
        Product.update(product);
    }

    @Test
    public void shouldRemoveProduct() {
        ProductDao product = createTestProduct();
        assertNotNull(product);

        product.setIdProduct(productId);
        Product.remove(product.getIdProduct());
    }
}
