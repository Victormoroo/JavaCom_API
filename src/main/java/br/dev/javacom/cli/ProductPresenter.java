package br.dev.javacom.cli;

import br.dev.javacom.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ProductPresenter {

    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private static final int LIST_ID = 4;
    private static final int LIST_NAME = 36;
    private static final int LIST_PRICE = 12;
    private static final int LIST_STOCK = 7;
    private static final int LIST_STATUS = 13;

    private static final int STOCK_ID = 4;
    private static final int STOCK_NAME = 36;
    private static final int STOCK_QTY = 9;
    private static final int STOCK_SIT = 18;

    private static final int DETAIL_WIDTH = 66;
    private static final int DETAIL_LABEL = 10;

    private final ConsoleIO console;

    public void printList(List<ProductResponse> products) {
        if (products.isEmpty()) {
            console.println("Nenhum produto encontrado.");
            return;
        }

        console.println(border('┌', '┬', '┐', LIST_ID, LIST_NAME, LIST_PRICE, LIST_STOCK, LIST_STATUS));
        console.println(row(
                center("ID", LIST_ID),
                center("Produto", LIST_NAME),
                center("Preço", LIST_PRICE),
                center("Estoque", LIST_STOCK),
                center("Status", LIST_STATUS)));
        console.println(border('├', '┼', '┤', LIST_ID, LIST_NAME, LIST_PRICE, LIST_STOCK, LIST_STATUS));

        int available = 0;
        for (ProductResponse p : products) {
            if (p.purchasable()) available++;
            console.println(row(
                    padLeft(String.valueOf(p.id()), LIST_ID),
                    padRight(truncate(p.name(), LIST_NAME), LIST_NAME),
                    padLeft(BRL.format(p.price()), LIST_PRICE),
                    padLeft(String.valueOf(p.stockQuantity()), LIST_STOCK),
                    padRight(listStatus(p), LIST_STATUS)));
        }
        console.println(border('└', '┴', '┘', LIST_ID, LIST_NAME, LIST_PRICE, LIST_STOCK, LIST_STATUS));
        console.println();
        console.println(("Total: %d produto(s) · %d disponível(is) para compra")
                .formatted(products.size(), available));
    }

    public void printDetail(ProductResponse p) {
        int inner = DETAIL_WIDTH - 2;
        console.println("┌" + "─".repeat(inner) + "┐");
        console.println("│ " + padRight("Produto #" + p.id(), inner - 2) + " │");
        console.println("├" + "─".repeat(inner) + "┤");
        printField("Nome", p.name(), inner);
        printField("Preço", BRL.format(p.price()), inner);
        printField("Estoque", p.stockQuantity() + " unidade(s)", inner);
        printField("Status", detailStatus(p), inner);
        console.println("│" + " ".repeat(inner) + "│");
        console.println("│  " + padRight("Descrição", inner - 4) + "  │");
        for (String line : wrap(p.description(), inner - 4)) {
            console.println("│  " + padRight(line, inner - 4) + "  │");
        }
        console.println("└" + "─".repeat(inner) + "┘");
    }

    public void printStock(List<ProductResponse> products) {
        if (products.isEmpty()) {
            console.println("Nenhum produto cadastrado.");
            return;
        }

        console.println(border('┌', '┬', '┐', STOCK_ID, STOCK_NAME, STOCK_QTY, STOCK_SIT));
        console.println(row(
                center("ID", STOCK_ID),
                center("Produto", STOCK_NAME),
                center("Estoque", STOCK_QTY),
                center("Situação", STOCK_SIT)));
        console.println(border('├', '┼', '┤', STOCK_ID, STOCK_NAME, STOCK_QTY, STOCK_SIT));

        int totalUnits = 0;
        int available = 0;
        int unavailable = 0;
        for (ProductResponse p : products) {
            if (p.purchasable()) {
                available++;
                totalUnits += p.stockQuantity();
            } else {
                unavailable++;
            }
            console.println(row(
                    padLeft(String.valueOf(p.id()), STOCK_ID),
                    padRight(truncate(p.name(), STOCK_NAME), STOCK_NAME),
                    padLeft(String.valueOf(p.stockQuantity()), STOCK_QTY),
                    padRight(stockSituation(p), STOCK_SIT)));
        }
        console.println(border('└', '┴', '┘', STOCK_ID, STOCK_NAME, STOCK_QTY, STOCK_SIT));
        console.println();
        console.println(("Total: %d unidade(s) em estoque · %d disponível(is) · %d indisponível(is)")
                .formatted(totalUnits, available, unavailable));
    }

    private void printField(String label, String value, int inner) {
        int valueWidth = inner - DETAIL_LABEL - 4;
        List<String> lines = wrap(value, valueWidth);
        boolean first = true;
        for (String line : lines) {
            String labelCol = first ? padRight(label, DETAIL_LABEL) : " ".repeat(DETAIL_LABEL);
            console.println("│  " + labelCol + padRight(line, valueWidth) + "  │");
            first = false;
        }
    }

    private String listStatus(ProductResponse p) {
        if (!p.active()) return "INATIVO";
        if (p.stockQuantity() == null || p.stockQuantity() == 0) return "SEM ESTOQUE";
        return "ATIVO";
    }

    private String detailStatus(ProductResponse p) {
        if (!p.active()) return "INATIVO · não disponível";
        if (p.stockQuantity() == null || p.stockQuantity() == 0) return "ATIVO · sem estoque";
        return "ATIVO · disponível para compra";
    }

    private String stockSituation(ProductResponse p) {
        if (!p.active()) return "Inativo";
        if (p.stockQuantity() == null || p.stockQuantity() == 0) return "Sem estoque";
        if (p.stockQuantity() < 5) return "Estoque baixo";
        return "Disponível";
    }

    private String border(char left, char mid, char right, int... widths) {
        StringBuilder sb = new StringBuilder().append(left);
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i] + 2));
            sb.append(i == widths.length - 1 ? right : mid);
        }
        return sb.toString();
    }

    private String row(String... cells) {
        StringBuilder sb = new StringBuilder("│");
        for (String c : cells) {
            sb.append(' ').append(c).append(" │");
        }
        return sb.toString();
    }

    private String padRight(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        return s + " ".repeat(w - s.length());
    }

    private String padLeft(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        return " ".repeat(w - s.length()) + s;
    }

    private String center(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        int total = w - s.length();
        int left = total / 2;
        return " ".repeat(left) + s + " ".repeat(total - left);
    }

    private String truncate(String s, int w) {
        if (s == null) return "";
        if (s.length() <= w) return s;
        if (w <= 1) return s.substring(0, w);
        return s.substring(0, w - 1) + "…";
    }

    private List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }
        String[] words = text.trim().split("\\s+");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (word.length() > width) {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                for (int i = 0; i < word.length(); i += width) {
                    lines.add(word.substring(i, Math.min(i + width, word.length())));
                }
                continue;
            }
            if (current.length() == 0) {
                current.append(word);
            } else if (current.length() + 1 + word.length() <= width) {
                current.append(' ').append(word);
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }
}
