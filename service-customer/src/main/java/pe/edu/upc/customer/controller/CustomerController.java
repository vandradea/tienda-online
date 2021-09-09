package pe.edu.upc.customer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.customer.entity.Customer;
import pe.edu.upc.customer.entity.Region;
import pe.edu.upc.customer.service.CustomerService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> listAllCustomer(@RequestParam(name = "regionId",required = false) Long regionId){
        List<Customer> customers = new ArrayList<>();
        if(regionId == null){
            customers = customerService.findCustomerAll();
            if(customers.isEmpty()){
                return ResponseEntity.notFound().build();
            }
        }else{
            Region region = new Region();
            region.setId(regionId);
            customers = customerService.findCustomerByRegion(region);
            if (customers == null){
                log.error("Customers with Region id {} not found.",regionId);
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(customers);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id){
        log.info("Fetching Customer with id {}",id);
        Customer customer = customerService.getCustomer(id);
        if (customer == null){
            log.error("Customer with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer, BindingResult result){
        log.info("Creating Customer : {}",customer);
        if (result.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }

        Customer customerDB = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerDB);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable("id") long id,@RequestBody Customer customer){
        log.info("Updating Customer with Id: {}",id);
        Customer customerDB = customerService.getCustomer(id);
        if(customerDB == null){
            log.error("Unable to update. Customer with Id {} not found",id);
            return ResponseEntity.notFound().build();
        }
        customer.setId(id);
        customerDB = customerService.updateCustomer(customer);
        return ResponseEntity.ok(customerDB);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable("id") Long id){
        log.info("Fetching & Deleting Customer with Id: {}",id);
        Customer customerDB = customerService.getCustomer(id);
        if(customerDB == null){
            log.error("Unable to delete. Customer with Id {} not found",id);
            return ResponseEntity.notFound().build();
        }
        customerDB = customerService.deleteCustomer(id);
        return ResponseEntity.ok(customerDB);
    }


    private String formatMessage( BindingResult result){
        List<Map<String,String>> errors = result.getFieldErrors().stream()
                .map(err ->{
                    Map<String,String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .message(errors).build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try{
            jsonString = mapper.writeValueAsString(errorMessage);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return jsonString;
    }
}
