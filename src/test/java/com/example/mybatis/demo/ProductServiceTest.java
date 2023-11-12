package com.example.mybatis.demo;

import com.example.mybatis.demo.model.Product;
import com.example.mybatis.demo.repository.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@MybatisTest
public class ProductServiceTest {
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceTest(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Test
    public void getProductById() {
        Product product = productMapper.selectProductById(1L);
        log.info("product: {}", product);
        assertNotNull(product);
    }

    @Test
    public void getAllProducts() {
        List<Product> products = productMapper.selectAllProducts();
        log.info("products: {}", products);
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Transactional
    @Test
    public void addProduct() {
        List<Product> prevProducts = productMapper.selectAllProducts();
        int prevSize = prevProducts.size();
        productMapper.insertProduct(
                Product
                        .builder()
                        .prodName("쿤달 샴푸")
                        .prodPrice(7900)
                        .build()
        );
        productMapper.insertProduct(
                Product
                        .builder()
                        .prodName("마스크팩")
                        .prodPrice(1000)
                        .build()
        );
        productMapper.insertProduct(
                Product
                        .builder()
                        .prodName("티셔츠")
                        .prodPrice(5900)
                        .build()
        );
        List<Product> curProducts = productMapper.selectAllProducts();
        int curSize = curProducts.size();
        assertEquals(prevSize + 3, curSize);
    }
}
