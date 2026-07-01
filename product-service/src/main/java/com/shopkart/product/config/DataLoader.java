package com.shopkart.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopkart.product.dto.ProductJsonDto;
import com.shopkart.product.model.Product;
import com.shopkart.product.model.Size;
import com.shopkart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // All JSON files placed in src/main/resources/data/
    private static final String[] JSON_FILES = {
            "data/men_jeans.json",
            "data/men_shirt.json",
            "data/LenghaCholi.json",
            "data/women_dress.json",
            "data/women_jeans.json",
            "data/women_top.json"
    };

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            log.info("Products already loaded ({} items). Skipping seed.", productRepository.count());
            return;
        }

        int totalLoaded = 0;
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (String filePath : JSON_FILES) {
            try {
                Resource resource = resolver.getResource("classpath:" + filePath);
                if (!resource.exists()) {
                    log.warn("File not found: {} — skipping", filePath);
                    continue;
                }

                try (InputStream is = resource.getInputStream()) {
                    ProductJsonDto[] arr = objectMapper.readValue(is, ProductJsonDto[].class);
                    List<ProductJsonDto> jsonProducts = List.of(arr);

                    List<Product> products = jsonProducts.stream()
                            .map(this::mapToEntity)
                            .collect(Collectors.toList());

                    productRepository.saveAll(products);
                    totalLoaded += products.size();
                    log.info("Loaded {} products from {}", products.size(), filePath);
                }
            } catch (Exception e) {
                log.error("Error loading {}: {}", filePath, e.getMessage(), e);
            }
        }

        log.info("=== DATA LOAD COMPLETE: {} total products in database ===", totalLoaded);
    }

    private Product mapToEntity(ProductJsonDto dto) {
        List<Size> sizes = dto.getSize() == null ? List.of() :
                dto.getSize().stream()
                        .map(s -> new Size(s.getName(), s.getQuantity()))
                        .collect(Collectors.toList());

        return Product.builder()
                .imageUrl(dto.getImageUrl())
                .brand(dto.getBrand())
                .title(dto.getTitle())
                .color(dto.getColor())
                .discountedPrice(dto.getDiscountedPrice())
                .price(dto.getPrice())
                .discountPersent(dto.getDiscountPersent())
                .size(sizes)
                .quantity(dto.getQuantity())
                .topLavelCategory(dto.getTopLavelCategory())
                .secondLavelCategory(dto.getSecondLavelCategory())
                .thirdLavelCategory(dto.getThirdLavelCategory())
                .description(dto.getDescription())
                .currency("USD")
                .build();
    }
}