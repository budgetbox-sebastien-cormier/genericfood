package com.budgetbox.genericfood.controlers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetbox.genericfood.dao.Product;
import com.budgetbox.genericfood.services.ProductService;
import com.budgetbox.genericfood.shared.ProductSearchQuery;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("products/v1.0")
public class ProductApiV1Controller {

	@Autowired
	private ProductService productService;

	private final ObjectMapper mapper = new ObjectMapper();

	private final Logger logger = Logger.getLogger(ProductApiV1Controller.class.getName());

	@GetMapping("/")
    public ResponseEntity<String> getProducts(
    		@RequestParam(required = false, defaultValue = "0") int from,
			@RequestParam(required = false, defaultValue = "25") int size,
			@RequestParam(required = false, defaultValue = "id") String sort,
			@RequestParam(required = false, defaultValue = "ASC") String order,
			@RequestParam(required = false, defaultValue = "") String keywords,
			@RequestParam(required = false, defaultValue = "0") int groupId,
			@RequestParam(required = false, defaultValue = "0") int subGroupId) {
		try {
			ProductSearchQuery query = new ProductSearchQuery();
			query.setFrom(from);
			query.setSize(size);
			query.setSort(sort);
			query.setOrder(order);
			query.setKeywords(keywords);
			query.setGroupId(groupId);
			query.setSubGroupId(subGroupId);

	        Page<Product> translationPage = productService.searchProducts(query);

	        Map<String, Object> result = new HashMap<>();
			result.put("total", translationPage.getTotalElements());
			result.put("hits", translationPage.getContent());

			return buildResponseEntity(mapper.writeValueAsString(result), HttpStatus.OK);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> getProduct(@PathVariable int id) {
		try {
			if(id > 0) {
				Product product = productService.get(id);
				if(product == null) {
					return buildResponseEntity(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND);
				}

				return buildResponseEntity(mapper.writeValueAsString(product), HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return buildResponseEntity(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
	}

    @PostMapping(value = "/")
    public ResponseEntity<String> addProduct(@RequestBody(required = true) String payload) {
		try {
			if(StringUtils.isNotBlank(payload)) {
				Product product = mapper.readValue(payload, Product.class);

				if(StringUtils.isBlank(product.getName()) || product.getGroupId() <= 0) {
					return buildResponseEntity("'name' and 'groupId' should not be empty", HttpStatus.BAD_REQUEST);
				}

				Product productNew = productService.save(product);
				return buildResponseEntity(mapper.writeValueAsString(productNew), HttpStatus.CREATED);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return buildResponseEntity(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
    }

	@PutMapping(value = "/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id, @RequestBody(required = true) String payload) {
		try {
			if(StringUtils.isNotBlank(payload)) {
				if(productService.get(id) == null) {
					return buildResponseEntity(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND);
				}

				Product product = mapper.readValue(payload, Product.class);
				product.setId(id);
				Product productNew = productService.save(product);
				return buildResponseEntity(mapper.writeValueAsString(productNew), HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return buildResponseEntity(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
    }

	@DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
		try {
			if(productService.get(id) == null) {
				return buildResponseEntity(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND);
			}

			productService.delete(id);
			return buildResponseEntity(HttpStatus.NO_CONTENT.getReasonPhrase(), HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@GetMapping("/ping")
	public ResponseEntity<String> getPing() {
		try {
			return buildResponseEntity(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<String> buildResponseEntity(String content, HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>(content, headers, status);
	}
}
