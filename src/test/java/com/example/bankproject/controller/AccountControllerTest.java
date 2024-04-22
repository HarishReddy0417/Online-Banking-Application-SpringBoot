package com.example.bankproject.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.AccountType;
import com.example.bankproject.entities.Customer;
import com.example.bankproject.repositorys.AccountRepository;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.CustomerService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.mockito.BDDMockito.given;



@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CustomerService customerService;


    @Order(1)
    @Test
    public void testAccountsList() throws Exception {
     
        Customer customer = new Customer(1L, "harish", "reddy", "harish@gmail.com");

        Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000), customer);

        when(accountService.getAllAccounts()).thenReturn(Collections.singletonList(account));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/accounts/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        List<Account> accountList = (List<Account>) mav.getModel().get("accounts");
        assertNotNull(accountList);
        assertEquals(1, accountList.size());
        assertEquals(account, accountList.get(0));

        assertEquals("account-list", mav.getViewName());
    }

    @Order(2)
    @Test
    public void testShowAddAccountForm() throws Exception {
        Customer customer = new Customer(1L, "harish", "reddy", "harish@gmail.com");

        List<Customer> customers = Arrays.asList(customer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/accounts/add"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        assertNotNull(mav.getModel().get("account"));
        assertNotNull(mav.getModel().get("customers"));
        assertEquals(1, ((List<Customer>) mav.getModel().get("customers")).size());

        assertEquals("account-form", mav.getViewName());
    }

    @Order(3)
    @Test
    public void testCreateAccount_ValidAccount_RedirectToList() throws Exception {
        Customer customer = new Customer(1L, "harish", "reddy", "harish@gmail.com");
        Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000), customer);

        given(customerService.getAllCustomers()).willReturn(Arrays.asList(customer));
        given(customerService.accountExistsForBranch(customer.getId(), account.getBranch())).willReturn(false);

        mockMvc.perform(post("/accounts/add")
                .flashAttr("account", account))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/list"));
    }

    @Order(4)
    @Test
    public void testCreateAccount_AccountExistsForBranch_ReturnsError() throws Exception {
        Customer customer = new Customer(1L, "harish", "reddy", "harish@gmail.com");
        Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000), customer);

        given(customerService.getAllCustomers()).willReturn(Arrays.asList(customer));
        given(customerService.accountExistsForBranch(customer.getId(), account.getBranch())).willReturn(true);

        mockMvc.perform(post("/accounts/add")
                .flashAttr("account", account))
                .andExpect(status().isOk())
                .andExpect(view().name("account-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Order(5)
    @Test
    public void testCreateAccount_AccountNumberInUse_ReturnsError() throws Exception {
        Customer customer = new Customer(1L, "harish", "reddy", "harish@gmail.com");
        Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000), customer);

        given(customerService.getAllCustomers()).willReturn(Arrays.asList(customer));
        given(customerService.accountExistsForBranch(customer.getId(), account.getBranch())).willReturn(false);
        given(accountService.createAccount(account)).willThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/accounts/add")
                .flashAttr("account", account))
                .andExpect(status().isOk())
                .andExpect(view().name("account-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Order(6)
    @Test
    public void testCreateAccount_InvalidAccount_ReturnsErrors() throws Exception {
        Account account = new Account();

        mockMvc.perform(post("/accounts/add")
                .flashAttr("account", account))
                .andExpect(status().isOk())
                .andExpect(view().name("account-form"))
                .andExpect(model().attributeExists("errors"));
    }


    @Order(7)
    @Test
    void testEditAccountForm() throws Exception {
        Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000), new Customer());
        given(accountService.findById(1L)).willReturn(account);

        Customer customer1 = new Customer(1L, "harish", "reddy", "harish@gmail.com");
        Customer customer2 = new Customer(2L,"harsha", "reddy", "harsha@gmail.com");
        given(customerService.getAllCustomers()).willReturn(Arrays.asList(customer1, customer2));

        MvcResult mvcResult = mockMvc.perform(get("/accounts/edit/1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        assertEquals("editAccount", mav.getViewName());
        assertEquals(account, mav.getModel().get("account"));
        assertEquals(Arrays.asList(customer1, customer2), mav.getModel().get("customers"));
    }

    @Order(8)
    @Test
    public void testUpdateAccount() throws Exception {

    Customer customer = new Customer();
    customer.setId(1L); 

    Account account = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000.00), customer);
    when(accountService.updateAccount(account)).thenReturn(account);

    mockMvc.perform(post("/accounts/edit1", 1L)
            .param("branch", "icici")
            .param("accountNumber", "1234567890")
            .param("accountType", "SAVINGS")
            .param("balance", "1000.0")
            .param("customer.id", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts/list"))
            .andExpect(model().hasNoErrors());

    verify(accountService, times(1)).updateAccount(any(Account.class));
}

@Order(9)
@Test
    public void testDeleteAccount() throws Exception {
        Long accountId = 1L;
        
        mockMvc.perform(get("/accounts/delete/{id}", accountId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/list"));

        verify(accountService, times(1)).deleteAccount(accountId);
    }

    @Order(10)
    @Test
    public void testSearchAccount() throws Exception {
        String accountNumber = "1234567890";
        Account account = new Account(1L,"icici", accountNumber, AccountType.SAVINGS, new BigDecimal("1000.00"), new Customer());
        List<Account> accounts = Collections.singletonList(account);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(accounts);

        mockMvc.perform(get("/accounts/search")
                .param("accountNumber", accountNumber))
                .andExpect(status().isOk())
                .andExpect(view().name("account-list"))
                .andExpect(model().attribute("accounts", accounts));
    }



}
