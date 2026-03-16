package br.com.crud_project.app;

import br.com.crud_project.ui.ProductConsoleMenu;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ProductApplicationServices application = ProductApplicationFactory.createInMemoryApplication();
        ProductConsoleMenu menu = new ProductConsoleMenu(application.catalog());
        menu.start();
    }
}
