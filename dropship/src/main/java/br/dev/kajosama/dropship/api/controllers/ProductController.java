package br.dev.kajosama.dropship.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.mappers.ProductMapper;
import br.dev.kajosama.dropship.api.payloads.requests.ProductRequest;
import br.dev.kajosama.dropship.api.payloads.responses.ProductResponse;
import br.dev.kajosama.dropship.api.services.ProductService;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ProductMapper productMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse registerProduct(@Valid @RequestBody Product product) {
        Product saved = productService.saveProduct(product);
        return ProductResponse.fromEntity(saved);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product)).getBody();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        if (productService.getAllProducts().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.getAllProducts()
                .stream().map(ProductResponse::fromEntity)
                .collect(Collectors.toList())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {

        Product p = productService.getProductById(id);
        productMapper.updateProductFromDto(request, p);
        productService.saveProduct(p);

        return ResponseEntity.ok(ProductResponse.fromEntity(p));

    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryId(@PathVariable Long id) {
        if (productService.getProductByCategoryId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ProductResponse> list = productService.getProductByCategoryId(id)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

}
