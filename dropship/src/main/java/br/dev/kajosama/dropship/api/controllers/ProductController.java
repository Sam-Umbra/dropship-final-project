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
import br.dev.kajosama.dropship.api.payloads.requests.ProductRegisterRequest;
import br.dev.kajosama.dropship.api.payloads.requests.ProductUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.ProductResponse;
import br.dev.kajosama.dropship.api.services.ProductService;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import jakarta.validation.Valid;

/**
 * REST controller for managing products. Provides endpoints for creating,
 * retrieving, updating, and deleting products. Also provides various methods
 * for searching and filtering products.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    /**
     * Service for handling product-related business logic.
     */
    @Autowired
    ProductService productService;

    /**
     * Mapper for converting between Product DTOs and entities.
     */
    @Autowired
    ProductMapper productMapper;

    /**
     * Registers a new product.
     *
     * @param request The request payload containing the product details.
     * @return A {@link ProductResponse} object representing the newly created
     * product.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse registerProduct(@Valid @RequestBody ProductRegisterRequest request) {
        Product saved = productService.registerProduct(request);
        return ProductResponse.fromEntity(saved);
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return A {@link ProductResponse} object for the found product.
     */
    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product)).getBody();
    }

    /**
     * Retrieves a list of all products.
     *
     * @return A {@link ResponseEntity} containing a list of all products, or
     * 404 Not Found if no products exist.
     */
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

    /**
     * Finds products by their name.
     *
     * @param name The name (or partial name) to search for.
     * @return A {@link ResponseEntity} containing a list of matching products,
     * or 404 Not Found if no matches are found.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ProductResponse>> findProductsByName(@PathVariable String name) {
        if (productService.getProductsByName(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.getProductsByName(name)
                .stream().map(ProductResponse::fromEntity)
                .collect(Collectors.toList())
        );
    }

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param id The ID of the category.
     * @return A {@link ResponseEntity} containing a list of products in the
     * category, or 404 Not Found if the category is empty or does not exist.
     */
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

    /**
     * Retrieves all products associated with a specific supplier ID.
     *
     * @param id The ID of the supplier.
     * @return A {@link ResponseEntity} containing a list of the supplier's
     * products, or 404 Not Found if none exist.
     */
    @GetMapping("/supplier/id/{id}")
    public ResponseEntity<List<ProductResponse>> getProductsBySupplierId(@PathVariable Long id) {
        if (productService.getProductsBySupplierId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var productList = productService.getProductsBySupplierId(id)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }

    /**
     * Retrieves all products associated with a specific supplier name.
     *
     * @param name The name of the supplier.
     * @return A {@link ResponseEntity} containing a list of the supplier's
     * products, or 404 Not Found if none exist.
     */
    @GetMapping("/supplier/name/{name}")
    public ResponseEntity<List<ProductResponse>> getProductsBySupplierName(@PathVariable String name) {
        if (productService.getProductsBySupplierName(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var productList = productService.getProductsBySupplierName(name)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productList);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param request The request payload with the updated product details.
     * @return A {@link ResponseEntity} containing the updated product as a
     * {@link ProductResponse}.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductUpdateRequest request) {

        Product p = productService.getProductById(id);
        productService.verifyIfSupplierMatcherUserAndProduct(p);
        productMapper.updateProductFromDto(request, p);
        productService.saveProduct(p);

        return ResponseEntity.ok(ProductResponse.fromEntity(p));
    }

}
