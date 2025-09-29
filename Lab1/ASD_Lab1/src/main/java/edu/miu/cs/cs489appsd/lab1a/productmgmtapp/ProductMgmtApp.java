package edu.miu.cs.cs489appsd.lab1a.productmgmtapp;

import edu.miu.cs.cs489appsd.lab1.productmgmtapp.model.Product;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

public class ProductMgmtApp {
    private static final Logger logger = Logger.getLogger(ProductMgmtApp.class.getName());

    public static void main(String[] args) {
        Product[] products = new Product[] {
            new Product("31288741190182539912", "Banana", LocalDate.parse("2025-01-24"), 124, 0.55),
            new Product("29274582650152771644", "Apple", LocalDate.parse("2024-12-09"), 18, 1.09),
            new Product("91899274600128155167", "Carrot", LocalDate.parse("2025-03-31"), 89, 2.99),
            new Product("31288741190182539913", "Banana", LocalDate.parse("2025-02-13"), 240, 0.65)
        };
        printProducts(products);
    }

    public static void printProducts(Product[] products) {
        // Sort by name ascending, then unitPrice descending
        Arrays.sort(products, Comparator.comparing(Product::getName)
                .thenComparing(Comparator.comparing(Product::getUnitPrice).reversed()));

        printJson(products);
        printXml(products);
        printCsv(products);
    }

    private static void printJson(Product[] products) {
        logger.info("\nJSON Format:");
        logger.info("[");
        for (int i = 0; i < products.length; i++) {
            Product p = products[i];
            logger.info(String.format("  {\"productId\": \"%s\", \"name\": \"%s\", \"dateSupplied\": \"%s\", \"quantityInStock\": %d, \"unitPrice\": %.2f}%s",
                p.getProductId(), p.getName(), p.getDateSupplied(), p.getQuantityInStock(), p.getUnitPrice(),
                (i < products.length - 1) ? "," : ""));
        }
        logger.info("]");
    }

    private static void printXml(Product[] products) {
        logger.info("\nXML Format:");
        logger.info("<products>");
        for (Product p : products) {
            logger.info(String.format("  <product><productId>%s</productId><name>%s</name><dateSupplied>%s</dateSupplied><quantityInStock>%d</quantityInStock><unitPrice>%.2f</unitPrice></product>",
                p.getProductId(), p.getName(), p.getDateSupplied(), p.getQuantityInStock(), p.getUnitPrice()));
        }
        logger.info("</products>");
    }

    private static void printCsv(Product[] products) {
        logger.info("\nCSV Format:");
        logger.info("productId,name,dateSupplied,quantityInStock,unitPrice");
        for (Product p : products) {
            logger.info(String.format("%s,%s,%s,%d,%.2f",
                p.getProductId(), p.getName(), p.getDateSupplied(), p.getQuantityInStock(), p.getUnitPrice()));
        }
    }
}
