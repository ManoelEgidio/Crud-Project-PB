package br.com.crud_project.web.controller;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ServiceTimeoutException;
import br.com.crud_project.domain.exception.ServiceUnavailableException;
import br.com.crud_project.domain.exception.SystemOverloadException;
import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.service.ProductCatalog;
import br.com.crud_project.web.form.ProductForm;
import br.com.crud_project.web.form.ProductInputMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping
public class ProductController {
    private static final String PRODUCT_LIST_VIEW = "products/list";
    private static final String PRODUCT_FORM_VIEW = "products/form";

    private final ProductCatalog service;
    private final ProductInputMapper inputMapper;

    public ProductController(ProductCatalog service, ProductInputMapper inputMapper) {
        this.service = service;
        this.inputMapper = inputMapper;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "messageType", required = false) String messageType,
            Model model
    ) {
        try {
            model.addAttribute("products", service.findAll());
        } catch (RuntimeException exception) {
            model.addAttribute("products", List.of());
            model.addAttribute("message", resolveSafeMessage(exception));
            model.addAttribute("messageType", "error");
            return PRODUCT_LIST_VIEW;
        }
        model.addAttribute("message", message);
        model.addAttribute("messageType", messageType == null ? "success" : messageType);
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
            service.create(inputMapper.toProduct(form, form.id()));
            redirectAttributes.addAttribute("message", "Produto cadastrado com sucesso.");
            redirectAttributes.addAttribute("messageType", "success");
            return "redirect:/products";
        } catch (ValidationException | DuplicateProductException exception) {
            return renderForm(model, form, false, exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException exception) {
            return renderForm(model, form, false, resolveSafeMessage(exception), resolveStatus(exception));
        }
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = service.findById(id);
            return renderForm(model, ProductForm.fromProduct(product), true, null, HttpStatus.OK);
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addAttribute("message", exception.getMessage());
            redirectAttributes.addAttribute("messageType", "error");
            return "redirect:/products";
        } catch (RuntimeException exception) {
            redirectAttributes.addAttribute("message", resolveSafeMessage(exception));
            redirectAttributes.addAttribute("messageType", "error");
            return "redirect:/products";
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
            service.update(inputMapper.toProduct(form, id));
            redirectAttributes.addAttribute("message", "Produto atualizado com sucesso.");
            redirectAttributes.addAttribute("messageType", "success");
            return "redirect:/products";
        } catch (ValidationException | ProductNotFoundException exception) {
            return renderForm(model, form, true, exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException exception) {
            return renderForm(model, form, true, resolveSafeMessage(exception), resolveStatus(exception));
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addAttribute("message", "Produto removido com sucesso.");
            redirectAttributes.addAttribute("messageType", "success");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addAttribute("message", exception.getMessage());
            redirectAttributes.addAttribute("messageType", "error");
        } catch (RuntimeException exception) {
            redirectAttributes.addAttribute("message", resolveSafeMessage(exception));
            redirectAttributes.addAttribute("messageType", "error");
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

    private String resolveSafeMessage(RuntimeException exception) {
        if (exception instanceof ServiceTimeoutException) {
            return "A operacao demorou mais do que o esperado. Tente novamente.";
        }
        if (exception instanceof ServiceUnavailableException) {
            return "Servico temporariamente indisponivel. Tente novamente.";
        }
        if (exception instanceof SystemOverloadException) {
            return "Sistema temporariamente sobrecarregado. Tente novamente em instantes.";
        }
        return "Nao foi possivel processar a solicitacao no momento.";
    }

    private HttpStatus resolveStatus(RuntimeException exception) {
        if (exception instanceof ServiceTimeoutException) {
            return HttpStatus.REQUEST_TIMEOUT;
        }
        if (exception instanceof ServiceUnavailableException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        if (exception instanceof SystemOverloadException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
