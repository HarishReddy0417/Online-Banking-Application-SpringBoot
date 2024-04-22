package com.example.bankproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.Customer;
import com.example.bankproject.repositorys.CustomerRepository;




@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Order(1)
    @Test
    public void testSaveCustomer(){
       
        Customer customer=new Customer();
        customer.setFirstName("harish");
        customer.setLastName("reddy");
        customer.setEmail("harish@gmail.com");

        Customer mockCustomer=new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setFirstName(customer.getFirstName());
        mockCustomer.setLastName(customer.getLastName());
        mockCustomer.setEmail(customer.getEmail());
        when(customerRepository.save(any(Customer.class))).thenReturn(mockCustomer);
        Customer result=customerService.createCustomer(customer);

        verify(customerRepository,times(1)).save(customer);
        assertEquals(1,result.getId());
        assertNotEquals(0, result);
    }

    @Order(2)
    @Test
    public void testGetAllCustomers(){

        Customer customerOne=new Customer(1L,"harish","reddy","harish@gmail.com");

        Customer customerTwo=new Customer(1L,"harsha","reddy","harsha@gmail.com");

        List<Customer> customerList=new ArrayList<>(Arrays.asList(customerOne,customerTwo));

        when(customerRepository.findAll()).thenReturn(customerList);

        assertIterableEquals(customerList, customerService.getAllCustomers());
        
        List<Customer> foundList =customerService.getAllCustomers();
        assertEquals(customerList, foundList);
        assertEquals(2, foundList.size());
        assertEquals("harish", foundList.get(0).getFirstName());

    }

    @Order(3)
    @Test
    public void testGetCustomerById(){
        Customer customer=new Customer(1L,"harish","reddy","harish@gmail.com");
        Long id=customer.getId();
        customer.setId(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Optional<Customer> foundCustomer=customerService.findById(id);
        
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer, foundCustomer.get());
        assertEquals(1L, foundCustomer.get().getId());
    }

    @Order(4)
    @Test
    public void testDeleteCustomerById(){
        Customer customer=new Customer(1L,"harish","reddy","harish@gmail.com");
        Long id=customer.getId();
        customerRepository.deleteById(id);
        verify(customerRepository, times(1)).deleteById(id);
        assertFalse(customerService.findById(id).isPresent());
    }

    @Order(5)
    @Test
    public void testFindByEmail(){
        Customer customer=new Customer(1L,"harish","reddy","harish@gmail.com");
        String email=customer.getEmail();
        Customer mockCustomer=new Customer();
        mockCustomer.setEmail(email);
        when(customerRepository.findByEmail(email)).thenReturn(mockCustomer);
        Customer foundCustomer=customerService.findByEmail(email);
        assertEquals(mockCustomer, foundCustomer);
    }

    @Order(6)
    @Test
    public void testFindByEmailNotFound(){
        Customer customer=new Customer(1L,"harish","reddy","harish@gmail.com");
        String email=customer.getEmail();
        Customer mockCustomer=new Customer();
        mockCustomer.setEmail("harsha@gmail.com");
        when(customerRepository.findByEmail(email)).thenReturn(mockCustomer);
        Customer foundCustomer=customerService.findByEmail(email);

        assertEquals(mockCustomer, foundCustomer);
    
        
    }
    @Order(7)
    @Test
    public void testCreateCustomerAccount() {
        Long customerId = 1L;
        Account account = new Account();
        Customer customer = new Customer();
        customer.setId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.createCustomerAccount(customerId, account);

        verify(customerRepository, times(1)).save(customer);
    }
    @Order(8)
    @Test
   public void testAccountExistsForBranch() {
        Long customerId = 1L;
        String branch = "icici";
        Customer customer = new Customer();
        Account account = new Account();
        account.setBranch(branch);
        customer.getAccounts().add(account);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        boolean exists = customerService.accountExistsForBranch(customerId, branch);

        Assertions.assertTrue(exists);
    }
    @Order(9)
    @Test
    public void testAccountDoesNotExistForBranch() {
        Long customerId = 1L;
        String branch = "icici";
        Customer customer = new Customer();
        Account account = new Account();
        account.setBranch("sbi");
        customer.getAccounts().add(account);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        boolean exists = customerService.accountExistsForBranch(customerId, branch);

        Assertions.assertFalse(exists);
    }
    @Order(10)
    @Test
    public void testAccountDoesNotExistForCustomer() {
        Long customerId = 1L;
        String branch = "icici";
        Customer customer = new Customer();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        boolean exists = customerService.accountExistsForBranch(customerId, branch);

        Assertions.assertFalse(exists);
    }


    @Order(11)
    @Test
    public void testGetCustomerByIdNotFound() {
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> customerService.getCustomerById(id));
    }
    @Order(12)
    @Test
    public void testCreateCustomerAccountCustomerNotFound() {
        Long customerId = 1L;
        Account account = new Account();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> customerService.createCustomerAccount(customerId, account));
    }
    

}
