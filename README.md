# mybatis + spring boot 예시

## mybatis를 쓰는 이유
<blockquote>
지속성 프레임워크는 프로그램 데이터를 데이터베이스, 특히 관계형 데이터베이스에 저장하도록 지원하고 자동화하는 미들웨어입니다.

많은 지속성 프레임워크는 객체 관계 매핑(ORM) 도구이기도 합니다(예: Hibernate, MyBatis SQL Maps, Apache Cayenne, Entity Framework, Slick, Java Ultra-Lite Persistence).

매핑은 XML 파일 또는 메타데이터 주석을 사용하여 정의할 수 있습니다.

많은 Java 기반 지속성 프레임워크가 있지만 MyBatis는 다음과 같은 이유로 인기를 끌게 되었습니다.

- JDBC 보일러 플레이트 코드를 많이 없애줍니다.

- 러닝커브가 낮습니다.

- 레거시 데이터베이스와 잘 작동합니다.

- SQL을 포함합니다.

- Spring 및 Guice 프레임워크와의 통합을 지원합니다.

- 타사 캐시 라이브러리와의 통합을 지원합니다.

- 더 나은 성능을 유도합니다.

MyBatis와 다른 지속성 프레임워크의 중요한 차이점은 MyBatis가 SQL 사용을 강조하는 반면 Hibernate와 같은 다른 프레임워크는 일반적으로 사용자 지정 쿼리 언어, 즉 Hibernate Query Language(HQL) 또는 Enterprise JavaBeans Query Language(EJB QL)를 사용한다는 것입니다.
</blockquote>

## build.gradle.kts
```kts
dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.2")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.2")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
}
```

## model, repository, service 작성

### model.Product
필드명을 카멜케이스로 작성할 수 있는 이유는 application.yml에서 `configuration.map-underscore-to-camel-case: true`로 설정했기 때문입니다.
```java
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Product {
    private Long prodId; // 상품 아이디
    private String prodName; // 상품 이름
    private int prodPrice; // 상품 가격
}
```

### repository.ProductMapper
Mapper 어노테이션을 추가한 클래스는 dao 역할을 하며, 아래와 같이 xml 방식과 어노테이션 방식으로 Sql을 메소드에 연결할 수 있습니다.
```java
@Mapper
public interface ProductMapper {
    Product selectProductById(Long id); // 하나의 상품 가져오기
    List<Product> selectAllProducts(); // 모든 상품 가져오기
    void insertProduct(Product product); // 상품 하나 추가
}
```

### service.ProductService
```java
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductMapper productMapper;

    public Product getProductById(Long id) {
        return productMapper.selectProductById(id);
    }

    public List<Product> getAllProducts() {
        return productMapper.selectAllProducts();
    }

    @Transactional
    public void addProduct(Product product) {
        productMapper.insertProduct(product);
    }
}
```

## 1. XML Mapper 방식

### application.yml
- mapper-location: xml 경로를 설정합니다. xml 파일은 resources 밑에 만들면 됩니다.  
- type-aliases-package: xml 파일에서 모델 클래스의 패키지명을 생략하기 위해 설정합니다.
```yml
mybatis:
    mapper-locations: mybatis-mapper/**/*.xml
    type-aliases-package: com.example.mybatis.demo.model
```

### resources/mybatis-mapper/ProductMapper.xml
- 다음과 같이 select 태그와 insert 태그로 어떤 쿼리인지 구분합니다.
- resultType: 쿼리 결과를 어떤 데이터타입으로 변환할 지 명시합니다. resultType에 패키지명이 생략될 수 있는 이유는 위 yml 파일에서 설정했기 때문입니다.
- parameterType: 파라미터의 데이터타입을 명시합니다.
- 파라미터는 #{paramName}로 전달할 수 있습니다. 이름은 정해진 형식이 없습니다.
```xml
<mapper namespace="com.example.mybatis.demo.repository.ProductMapper">
    <select id="selectProductById" resultType="Product">
        SELECT prod_id,
               prod_name,
               prod_price
        FROM products
        WHERE prod_id = #{prodId}
    </select>

    <insert id="insertProduct" parameterType="Product">
        INSERT INTO products (prod_name, prod_price)
        VALUES (#{prodName}, #{prodPrice})
    </insert>
</mapper>
```

## 2. Annotation Mapper 방식 
### application.yml
위에서 설정했던 아래 두 설정을 주석처리합니다.  
```yml
# mybatis:
#    mapper-locations: mybatis-mapper/**/*.xml
#    type-aliases-package: com.example.mybatis.demo.model
```

### repository.ProductMapper
- xml 파일 대신 어노테이션으로 SQL을 정의합니다.
- xml 파일에서 select 태그로 작성된 것을 Select 어노테이션으로 변경합니다.
- xml 파일에서 insert 태그로 작성된 것을 Insert 어노테이션으로 변경합니다.
```java
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

```

## 테스트 코드 작성
@MybatisTest 어노테이션을 이용하여 테스트: [파일 링크](https://github.com/chunghee-hwang/mybatis-springboot-example/blob/main/src/test/java/com/example/mybatis/demo/ProductServiceTest.java)

## References
- https://medium.com/@andisyafrianda/why-mybatis-489b01f48905
- https://atoz-develop.tistory.com/entry/Spring-Boot-MyBatis-설정-방법#google_vignette
- https://devlog-wjdrbs96.tistory.com/200
