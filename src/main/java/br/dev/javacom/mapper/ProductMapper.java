package br.dev.javacom.mapper;

import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "purchasable", source = ".", qualifiedByName = "computePurchasable")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Named("computePurchasable")
    default boolean computePurchasable(Product product) {
        return product.isActive() && product.getStockQuantity() != null && product.getStockQuantity() > 0;
    }
}
