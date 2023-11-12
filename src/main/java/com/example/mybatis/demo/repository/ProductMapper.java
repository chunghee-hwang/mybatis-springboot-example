package com.example.mybatis.demo.repository;

import com.example.mybatis.demo.model.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("""
        SELECT *
        FROM products
        WHERE prod_id = #{id}
    """)
    Product selectProductById(Long id);

    @Select("""
        SELECT *
        FROM products
    """)
    List<Product> selectAllProducts();

    @Insert("""
        INSERT INTO products (prod_name, prod_price)
        VALUES (#{prodName}, #{prodPrice})
    """)
    void insertProduct(Product product);
}
