package com.example.bankproject.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.Customer;
import com.example.bankproject.repositorys.CustomerRepository;
import com.example.bankproject.service.CustomerService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @Order(1)
    @Test
    public void testCustomersList() throws Exception {
      
        Customer customer1 = new Customer(1L, "harish", "reddy", "harish@gmail.com");
        Customer customer2 = new Customer(2L, "harsha", "reddy", "harsha@gmailcom");
        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(customerService.getAllCustomers()).thenReturn(customers);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/customers/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        List<Customer> customerList = (List<Customer>) mav.getModel().get("customers");
        assertNotNull(customerList);
        assertEquals(2, customerList.size());

        assertEquals("customer-list", mav.getViewName());
    }

    @Order(2)
    @Test
    void testShowAddCustomerForm() throws Exception {
        
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-form"))
                .andExpect(model().attributeExists("customer"));

    }

@Order(3)
@Test
void testAddCustomer_ValidCustomer_RedirectToList() throws Exception {
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFirstName("harish");
    customer.setLastName("reddy");
    customer.setEmail("harish@gmail.com");

    when(customerService.findByEmail("harish@gmail.com")).thenReturn(null);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/customers/add")
            .flashAttr("customer", customer))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/customers/list"))
            .andReturn();

    verify(customerService, times(1)).createCustomer(customer);
}

@Order(4)
@Test
void testEditCustomerForm() throws Exception {
  
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFirstName("harish");
    customer.setLastName("reddy");
    customer.setEmail("harish@gmail.com");

    when(customerService.getCustomerById(1L)).thenReturn(customer);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/customers/edit/1"))
            .andExpect(status().isOk())
            .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    Customer resultCustomer = (Customer) mav.getModel().get("customer");
    assertNotNull(resultCustomer);
    assertEquals(customer.getId(), resultCustomer.getId());
    assertEquals(customer.getFirstName(), resultCustomer.getFirstName());
    assertEquals(customer.getLastName(), resultCustomer.getLastName());
    assertEquals(customer.getEmail(), resultCustomer.getEmail());

    assertEquals("editCustomer", mav.getViewName());
}

@Order(5)
@Test
public void testUpdateCustomer() throws Exception {
   
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFirstName("harish");
    customer.setLastName("reddy");
    customer.setEmail("harish@gmail.com");

    ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

    when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

    mockMvc.perform(post("/customers/edit1")
            .param("firstName", "harish")
            .param("lastName", "reddy")
            .param("email", "harish@gmail.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/customers/list"));

    verify(customerService, times(1)).createCustomer(customerCaptor.capture());

    assertEquals(customer, customerCaptor.getValue());
}

@Order(6)
@Test
    public void testDeleteCustomer() throws Exception {
       
        Long customerId = 1L;

        mockMvc.perform(get("/customers/delete/{id}", customerId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/list"));

        verify(customerService, times(1)).deleteCustomer(customerId);
    }

    @Order(7)
    @Test
    public void testSearchCustomer_ReturnsCustomerList() throws Exception {
        Customer customer1 = new Customer(1L, "harsha", "reddy", "harsha@gmail.com");
        Customer customer2 = new Customer(2L, "harish", "reddy", "harish@gmail.com");
        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(customerRepository.findByFirstName("harsha")).thenReturn(customers);

        MvcResult mvcResult = mockMvc.perform(get("/customers/search").param("firstName", "harsha"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        List<Customer> customerList = (List<Customer>) mav.getModel().get("customers");
        assertNotNull(customerList);
        assertEquals(2, customerList.size());

        assertEquals("customer-list", mav.getViewName());
    }

    @Order(8)
    @Test
    public void testCreateCustomerAccount() throws Exception {
        
        Customer customer = new Customer();
        customer.setId(1L);
        Account account = new Account();
        account.setBranch("icici");

    
        when(customerService.accountExistsForBranch(1L, "icici")).thenReturn(false);

       
        mockMvc.perform(post("/customers/1/createAccount")
                .flashAttr("account", account))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/list"));
        verify(customerService).createCustomerAccount(1L, account);
    }

    @Order(9)
    @Test
    void testCreateCustomerAccount_AccountExistsForBranch() throws Exception {

        when(customerService.accountExistsForBranch(1L, "icici")).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/customers/1/createAccount")
                .param("branch", "icici")
                .param("accountNumber", "1234567890")
                .param("accountType", "SAVINGS")
                .param("balance", "1000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/add?error=Customer already has an account in this branch"));
    }
    

    @Order(10)
    @Test
    public void testCreateCustomerAccount_AccountExistsForBranch_RedirectsWithErrorMessage() throws Exception {
       
        when(customerService.accountExistsForBranch(anyLong(), anyString())).thenReturn(true);
        MvcResult mvcResult = mockMvc.perform(post("/customers/1/createAccount")
                .param("branch", "icici")
                .param("accountNumber", "1234567890")
                .param("accountType", "SAVINGS")
                .param("balance", "1000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/add?error=Customer already has an account in this branch"))
                .andReturn();
        verify(customerService, never()).createCustomerAccount(anyLong(), any(Account.class));
    }

    @Order(11)
    @Test
    void testShowCreateAccountForm() throws Exception {
   
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFirstName("harish");
    customer.setLastName("reddy");
    customer.setEmail("harish@gmail.com");

    when(customerService.findById(1L)).thenReturn(Optional.of(customer));

    when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer));

    MvcResult mvcResult = mockMvc.perform(get("/customers/1/createAccount"))
            .andExpect(status().isOk())
            .andExpect(view().name("account-form"))
            .andExpect(model().attributeExists("customer"))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("customers"))
            .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    assertEquals(customer, mav.getModel().get("customer"));
    assertEquals(Arrays.asList(customer), mav.getModel().get("customers"));

    String errorMessage = (String) mav.getModel().get("errorMessage");
    assertNull(errorMessage);

    customer.getAccounts().add(new Account());
    when(customerService.findById(1L)).thenReturn(Optional.of(customer));

    mvcResult = mockMvc.perform(get("/customers/1/createAccount"))
            .andExpect(status().isOk())
            .andExpect(view().name("error-page"))
            .andReturn();

    mav = mvcResult.getModelAndView();
    errorMessage = (String) mav.getModel().get("errorMessage");
    assertEquals("Customer already has an account", errorMessage);

    when(customerService.findById(1L)).thenReturn(Optional.empty());

    mvcResult = mockMvc.perform(get("/customers/1/createAccount"))
            .andExpect(status().isOk())
            .andExpect(view().name("error-page"))
            .andReturn();

    mav = mvcResult.getModelAndView();
    errorMessage = (String) mav.getModel().get("errorMessage");
    assertEquals("Customer not found", errorMessage);
}

}