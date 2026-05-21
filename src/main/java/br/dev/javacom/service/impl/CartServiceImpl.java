package br.dev.javacom.service.impl;

import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.Cart;
import br.dev.javacom.entity.CartItem;
import br.dev.javacom.entity.Order;
import br.dev.javacom.entity.OrderItem;
import br.dev.javacom.entity.Product;
import br.dev.javacom.entity.User;
import br.dev.javacom.enums.OrderStatus;
import br.dev.javacom.exception.BusinessException;
import br.dev.javacom.exception.InsufficientStockException;
import br.dev.javacom.exception.ResourceNotFoundException;
import br.dev.javacom.mapper.CartMapper;
import br.dev.javacom.mapper.OrderMapper;
import br.dev.javacom.repository.CartRepository;
import br.dev.javacom.repository.OrderRepository;
import br.dev.javacom.repository.ProductRepository;
import br.dev.javacom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public CartResponse getCart(User user) {
        return cartMapper.toResponse(loadOrCreateCart(user));
    }

    @Override
    @Transactional
    public CartResponse addItem(User user, AddCartItemRequest request) {
        Cart cart = loadOrCreateCart(user);
        Product product = loadProduct(request.productId());
        ensurePurchasable(product);

        Optional<CartItem> existing = findItem(cart, product.getId());
        int newQty = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();
        ensureStock(product, newQty);

        existing.ifPresentOrElse(
                item -> item.setQuantity(newQty),
                () -> cart.addItem(CartItem.builder()
                        .product(product)
                        .quantity(request.quantity())
                        .build())
        );

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(User user, Long productId, UpdateCartItemRequest request) {
        Cart cart = loadOrCreateCart(user);
        CartItem item = findItem(cart, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));

        ensurePurchasable(item.getProduct());
        ensureStock(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(User user, Long productId) {
        Cart cart = loadOrCreateCart(user);
        CartItem item = findItem(cart, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));
        cart.removeItem(item);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public void clear(User user) {
        Cart cart = loadOrCreateCart(user);
        cart.clear();
    }

    @Override
    @Transactional
    public OrderResponse checkout(User user) {
        Cart cart = loadOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Carrinho vazio — adicione itens antes de finalizar");
        }

        BigDecimal total = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.COMPLETED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            ensurePurchasable(product);
            ensureStock(product, cartItem.getQuantity());

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            order.addItem(orderItem);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        cart.clear();

        return orderMapper.toResponse(saved);
    }

    private Cart loadOrCreateCart(User user) {
        return cartRepository.findByUserWithItems(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private Product loadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Produto", productId));
    }

    private Optional<CartItem> findItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();
    }

    private void ensurePurchasable(Product product) {
        if (!product.isActive()) {
            throw new BusinessException("Produto inativo: " + product.getName());
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            throw new InsufficientStockException("Produto sem estoque: " + product.getName());
        }
    }

    private void ensureStock(Product product, int requested) {
        if (requested <= 0) {
            throw new BusinessException("Quantidade deve ser maior que zero");
        }
        if (product.getStockQuantity() < requested) {
            throw InsufficientStockException.of(product.getName(), requested, product.getStockQuantity());
        }
    }
}
