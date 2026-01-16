import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.ProductDao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductDaoTest {

    private final ProductDao product;

    public ProductDaoTest() {
        product = new ProductDao();
    }

    @Test
    void shouldBeAbleToCreateAProduct() {
        assertNotNull(product);
        assertEquals(0.0, product.getSellPrice());
        assertEquals(1, product.getQuantity());
    }

    @Test
    void shouldBeAbleToCreateCategoryObject() {
        ProductDao.CategoryDao category = new ProductDao.CategoryDao();
        String defaultDescription = "Sem categoria";
        assertNotNull(category);
        assertEquals(defaultDescription, category.getCategoryDescription());
    }
}