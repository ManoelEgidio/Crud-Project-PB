package br.com.crud_project.ui;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.service.ProductCatalog;

import java.util.List;
import java.util.Scanner;

public class ProductConsoleMenu {
    private static final int OPTION_CREATE = 1;
    private static final int OPTION_FIND_BY_ID = 2;
    private static final int OPTION_LIST_ALL = 3;
    private static final int OPTION_UPDATE = 4;
    private static final int OPTION_DELETE = 5;
    private static final int OPTION_EXIT = 0;

    private final ProductCatalog service;
    private final Scanner scanner;

    public ProductConsoleMenu(ProductCatalog service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int option = readInt("Escolha uma opcao: ");

            try {
                running = handleOption(option);
            } catch (ValidationException | DuplicateProductException | ProductNotFoundException exception) {
                System.out.println("Erro: " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Erro: opcao invalida.");
            }

            System.out.println();
        }
    }

    private boolean handleOption(int option) {
        return switch (option) {
            case OPTION_CREATE -> {
                createProduct();
                yield true;
            }
            case OPTION_FIND_BY_ID -> {
                findProductById();
                yield true;
            }
            case OPTION_LIST_ALL -> {
                listProducts();
                yield true;
            }
            case OPTION_UPDATE -> {
                updateProduct();
                yield true;
            }
            case OPTION_DELETE -> {
                deleteProduct();
                yield true;
            }
            case OPTION_EXIT -> {
                System.out.println("Encerrando sistema...");
                yield false;
            }
            default -> throw new IllegalArgumentException("Opcao invalida.");
        };
    }

    private void createProduct() {
        Product product = readProductData(true);
        service.create(product);
        System.out.println("Produto cadastrado com sucesso.");
    }

    private void findProductById() {
        String id = readText("Informe o ID do produto: ");
        Product product = service.findById(id);
        printProduct(product);
    }

    private void listProducts() {
        List<Product> products = service.findAll();

        if (products.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        products.forEach(this::printProduct);
    }

    private void updateProduct() {
        Product product = readProductData(false);
        service.update(product);
        System.out.println("Produto atualizado com sucesso.");
    }

    private void deleteProduct() {
        String id = readText("Informe o ID do produto a remover: ");
        service.deleteById(id);
        System.out.println("Produto removido com sucesso.");
    }

    private Product readProductData(boolean createMode) {
        String idMessage = createMode ? "Informe o ID: " : "Informe o ID do produto existente: ";
        String id = readText(idMessage);
        String name = readText("Informe o nome: ");
        double price = readDouble("Informe o preco: ");
        int quantity = readInt("Informe a quantidade em estoque: ");
        Category category = readCategory();

        return new Product(id, name, price, quantity, category);
    }

    private Category readCategory() {
        System.out.println("Categorias disponiveis:");
        for (Category category : Category.values()) {
            System.out.printf("- %s (%s)%n", category.name(), switch (category) {
                case FOOD -> "Alimentos";
                case ELECTRONICS -> "Eletronicos";
                case OFFICE -> "Escritorio";
            });
        }

        String selectedValue = readText("Informe a categoria: ").toUpperCase();
        return Category.valueOf(selectedValue);
    }

    private String readText(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Valor inteiro invalido. Tente novamente.");
            }
        }
    }

    private double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException exception) {
                System.out.println("Valor decimal invalido. Tente novamente.");
            }
        }
    }

    private void printProduct(Product product) {
        System.out.printf(
                "ID: %s | Nome: %s | Preco: %.2f | Quantidade: %d | Categoria: %s%n",
                product.id(),
                product.name(),
                product.price(),
                product.quantity(),
                product.categoryDescription()
        );
    }

    private void printMenu() {
        System.out.println("=== SISTEMA CRUD DE PRODUTOS ===");
        System.out.println("1 - Cadastrar produto");
        System.out.println("2 - Buscar produto por ID");
        System.out.println("3 - Listar produtos");
        System.out.println("4 - Atualizar produto");
        System.out.println("5 - Remover produto");
        System.out.println("0 - Sair");
    }
}
