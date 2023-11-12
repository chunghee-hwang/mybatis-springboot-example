package com.example.mybatis.demo;

import com.example.mybatis.demo.model.Product;
import com.example.mybatis.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    public void getProductById() {
        Product product = productService.getProductById(1L);
        log.info("product: {}", product);
    }

    @Test
    public void getAllProducts() {
        List<Product> products = productService.getAllProducts();
        log.info("products: {}", products);
    }

    @Transactional
    @Test
    public void addProduct() {
        productService.addProduct(
                Product
                        .builder()
                        .prodName("쿤달 샴푸")
                        .prodPrice(7900)
                        .build()
        );
        productService.addProduct(
                Product
                        .builder()
                        .prodName("마스크팩")
                        .prodPrice(1000)
                        .build()
        );
        productService.addProduct(
                Product
                        .builder()
                        .prodName("티셔츠")
                        .prodPrice(5900)
                        .build()
        );
    }
}
