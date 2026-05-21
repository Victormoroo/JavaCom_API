package br.dev.javacom.cli;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
public class ConsoleIO {

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final PrintStream out = System.out;

    public void println(String text) {
        out.println(text);
    }

    public void println() {
        out.println();
    }

    public void print(String text) {
        out.print(text);
    }

    public void printHeader(String title) {
        String line = "=".repeat(Math.max(40, title.length() + 4));
        out.println();
        out.println(line);
        out.println("  " + title);
        out.println(line);
    }

    public String readLine(String prompt) {
        out.print(prompt);
        try {
            String value = reader.readLine();
            return value == null ? "" : value.trim();
        } catch (Exception ex) {
            return "";
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int parsed = Integer.parseInt(value);
                if (parsed < min || parsed > max) {
                    println("Valor fora do intervalo permitido (" + min + "-" + max + ").");
                    continue;
                }
                return parsed;
            } catch (NumberFormatException ex) {
                println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    public int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }

    public Long readLong(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            String value = readLine(prompt).replace(",", ".");
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException ex) {
                println("Valor inválido. Use formato 0.00 ou 0,00.");
            }
        }
    }

    public boolean confirm(String prompt) {
        String value = readLine(prompt + " (s/n): ").toLowerCase();
        return value.equals("s") || value.equals("sim") || value.equals("y") || value.equals("yes");
    }
}
