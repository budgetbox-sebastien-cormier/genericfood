package com.budgetbox.genericfood.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.budgetbox.genericfood.dao.Product;
import com.budgetbox.genericfood.services.ProductGroupService;
import com.budgetbox.genericfood.services.ProductService;
import com.budgetbox.genericfood.shared.ProductSearchQuery;

@Controller
public class MainController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductGroupService productGroupService;

	@GetMapping("/")
    public String homepage(
    		Model model,
    		@RequestParam("page") Optional<Integer> page, 
    		@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
        int pageSize = size.orElse(25);

        ProductSearchQuery query = new ProductSearchQuery();
        query.setFrom((currentPage - 1) * pageSize);
        query.setSize(pageSize);
        
        
		Page<Product> products = productService.searchProducts(query);
	    model.addAttribute("products", products);
	    
	    int totalPages = products.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }	    
	    
        return "index";
    }

	@GetMapping("/product")
    public String homepage(Model model, @RequestParam("id") int productId) {
		Product product = productService.get(productId);
	    model.addAttribute("product", product);
        model.addAttribute("group", productGroupService.getById(product.getGroupId()));
        model.addAttribute("subgroup", productGroupService.getById(product.getSubGroupId()));
        return "product";
    }
}
