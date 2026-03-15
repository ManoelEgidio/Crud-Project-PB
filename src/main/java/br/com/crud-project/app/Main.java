package br.com.crud_project.app;

import br.com.crud_project.repository.InMemoryProductRepository;
import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.service.ProductService;
import br.com.crud_project.ui.ProductConsoleMenu;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ProductRepository repository = new InMemoryProductRepository();
        ProductService service = new ProductService(repository);
        ProductConsoleMenu menu = new ProductConsoleMenu(service);
        menu.start();
    }
}
