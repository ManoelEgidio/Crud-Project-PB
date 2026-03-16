package br.com.crud_project.web.controller;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.service.ProductService;
import br.com.crud_project.web.form.ProductForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping
public class ProductController {
    private static final String PRODUCT_LIST_VIEW = "products/list";
    private static final String PRODUCT_FORM_VIEW = "products/form";

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "message", required = false) String message, Model model) {
        model.addAttribute("products", service.findAll());
        model.addAttribute("message", message);
        return PRODUCT_LIST_VIEW;
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        return renderForm(model, ProductForm.empty(), false, null, HttpStatus.OK);
    }

    @PostMapping("/products")
    public String createProduct(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String price,
            @RequestParam String quantity,
            @RequestParam String category,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        ProductForm form = new ProductForm(id, name, price, quantity, category);
        try {
            service.create(toProduct(form, form.id()));
            redirectAttributes.addAttribute("message", "Produto cadastrado com sucesso.");
            return "redirect:/products";
        } catch (ValidationException | DuplicateProductException exception) {
            return renderForm(model, form, false, exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable String id, Model model) {
        try {
            Product product = service.findById(id);
            return renderForm(model, ProductForm.fromProduct(product), true, null, HttpStatus.OK);
        } catch (ProductNotFoundException exception) {
            return "redirect:/products?message=" + exception.getMessage();
        }
    }

    @PostMapping("/products/{id}")
    public String updateProduct(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam String price,
            @RequestParam String quantity,
            @RequestParam String category,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        ProductForm form = new ProductForm(id, name, price, quantity, category);
        try {
            service.update(toProduct(form, id));
            redirectAttributes.addAttribute("message", "Produto atualizado com sucesso.");
            return "redirect:/products";
        } catch (ValidationException | ProductNotFoundException exception) {
            return renderForm(model, form, true, exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addAttribute("message", "Produto removido com sucesso.");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addAttribute("message", exception.getMessage());
        }
        return "redirect:/products";
    }

    private String renderForm(
            Model model,
            ProductForm form,
            boolean editMode,
            String errorMessage,
            HttpStatus status
    ) {
        model.addAttribute("form", form);
        model.addAttribute("categories", Category.values());
        model.addAttribute("editMode", editMode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("pageTitle", editMode ? "Editar produto" : "Novo produto");
        model.addAttribute("formAction", editMode ? "/products/" + form.id() : "/products");
        model.addAttribute("responseStatus", status.value());
        return PRODUCT_FORM_VIEW;
    }

    private Product toProduct(ProductForm form, String id) {
        try {
            double parsedPrice = Double.parseDouble(form.price().trim().replace(',', '.'));
            int parsedQuantity = Integer.parseInt(form.quantity().trim());
            Category parsedCategory = Category.valueOf(form.category().trim().toUpperCase(Locale.ROOT));
            return new Product(id, form.name(), parsedPrice, parsedQuantity, parsedCategory);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Preço e quantidade devem ser numéricos.");
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Categoria inválida.");
        }
    }
}
