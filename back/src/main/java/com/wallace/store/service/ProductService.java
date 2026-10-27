package com.wallace.store.service;

import com.wallace.store.dto.ProductRequest;
import com.wallace.store.dto.ProductResponse;
import com.wallace.store.exception.ProductNotFoundException;
import com.wallace.store.model.Product;
import com.wallace.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findActive(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);

        return toResponse(repository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findActive(id);

        applyRequest(product, request);
        return toResponse(repository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        // Apagar de verdade quebraria os pedidos que ja tem o produto; ele so sai da loja
        findActive(id).setActive(false);
    }

    private Product findActive(Long id) {
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setTitle(request.title().trim());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCategory(request.category());
        product.setImg(request.img().trim());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getTitle(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImg(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
