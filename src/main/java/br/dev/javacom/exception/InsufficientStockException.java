package br.dev.javacom.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public static InsufficientStockException of(String productName, int requested, int available) {
        return new InsufficientStockException(
                "Estoque insuficiente para o produto '%s'. Solicitado: %d, Disponível: %d"
                        .formatted(productName, requested, available));
    }
}
