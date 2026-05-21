package br.dev.javacom.service;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> listAll(boolean activeOnly);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void deactivate(Long id);

    List<ProductResponse> listStock();
}
