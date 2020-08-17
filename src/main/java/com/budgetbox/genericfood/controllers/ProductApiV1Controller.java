package com.budgetbox.genericfood.controllers;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.budgetbox.genericfood.dao.Product;
import com.budgetbox.genericfood.dao.ProductGroup;
import com.budgetbox.genericfood.services.ProductGroupService;
import com.budgetbox.genericfood.services.ProductService;
import com.budgetbox.genericfood.shared.ProductSearchQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

@RestController
@RequestMapping("products/v1.0")
public class ProductApiV1Controller {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductGroupService productGroupService;

	private final ObjectMapper mapper = new ObjectMapper();

	private final Logger logger = Logger.getLogger(ProductApiV1Controller.class.getName());

	/**
	 * This entry-point returns list of Products and permits to paginate. Results can be filtered by groupId, subGroupId.
	 * 
	 * @param from
	 * @param size
	 * @param sort
	 * @param order
	 * @param keywords
	 * @param groupId
	 * @param subGroupId
	 * @param includes
	 * @return
	 */
	@GetMapping("/")
    public ResponseEntity<String> getProducts(
    		@RequestParam(required = false, defaultValue = "0") int from,
			@RequestParam(required = false, defaultValue = "25") int size,
			@RequestParam(required = false, defaultValue = "id") String sort,
			@RequestParam(required = false, defaultValue = "ASC") String order,
			@RequestParam(required = false, defaultValue = "") String keywords,
			@RequestParam(required = false, defaultValue = "0") int groupId,
			@RequestParam(required = false, defaultValue = "0") int subGroupId,
			@RequestParam(required = false, defaultValue = "") String includes
			) {
		try {
			ProductSearchQuery query = new ProductSearchQuery();
			query.setFrom(from);
			query.setSize(size);
			query.setSort(sort);
			query.setOrder(order);
			query.setKeywords(keywords);
			query.setGroupId(groupId);
			query.setSubGroupId(subGroupId);

	        Page<Product> productsPage = productService.searchProducts(query);

	        Map<String, Object> result = new HashMap<>();
			result.put("total", productsPage.getTotalElements());
			result.put("hits", productsPage.getContent());
			
			if(includes.contains("groups")) {
				Set<Integer> groupIds = productsPage.getContent().parallelStream()
						.map(p -> p.getGroupId())
						.collect(Collectors.toSet());
				result.put("groups", productGroupService.getByIds(groupIds));
			}
			if(includes.contains("subgroups")) {
				Set<Integer> groupIds = productsPage.getContent().parallelStream()
						.map(p -> p.getSubGroupId())
						.collect(Collectors.toSet());
				result.put("subgroups", productGroupService.getByIds(groupIds));
			}

			return buildResponseEntity(mapper.writeValueAsString(result), HttpStatus.OK);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	/**
	 * Entry point to retrieve a Product
	 * 
	 * @param id
	 * @return
	 */
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

	/**
	 * Entry point to add Product
	 * 
	 * @param payload
	 * @return
	 */
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

    /**
     * Entry point to update an existing Product
     * 
     * @param id
     * @param payload
     * @return
     */
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

	/**
	 * Entry point in order to delete a product
	 * 
	 * @param id
	 * @return
	 */
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

	@RequestMapping(value="/init", method = RequestMethod.POST, consumes = {"multipart/form-data"})
	public ResponseEntity<String> initializeDatabase(@RequestParam(value = "inputFile", required = false) MultipartFile inputFile) {
		try {
			if(inputFile != null) {
				String data = new String(inputFile.getBytes());

				try (CSVReader csvReader = new CSVReader(new StringReader(data))) {
					// read on time to skip headers
					csvReader.readNext();

				    String[] values = null;
				    while ((values = csvReader.readNext()) != null) {
						String sFoodName = values[0];
						String sScientificName = values[1];
						String sGroup = values[2];
						String sSubGroup = values[3];

						ProductGroup group = productGroupService.getByName(sGroup);
						if(group == null) {
							group = new ProductGroup();
							group.setName(sGroup);
							group = productGroupService.save(group);
						}
						ProductGroup subGroup = productGroupService.getByName(sSubGroup);
						if(subGroup == null) {
							subGroup = new ProductGroup();
							subGroup.setName(sSubGroup);
							subGroup = productGroupService.save(subGroup);
						}

						Product product = new Product();
						product.setName(sFoodName);
						product.setScientificName(sScientificName);
						product.setGroupId(group.getId());
						product.setSubGroupId(subGroup.getId());
						
						productService.save(product);
				        
				    }
				}

				return buildResponseEntity(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return buildResponseEntity(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
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
