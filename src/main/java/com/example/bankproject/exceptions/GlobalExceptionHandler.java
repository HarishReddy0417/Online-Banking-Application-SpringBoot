package com.example.bankproject.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.example.bankproject.entities.FundTransferDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ModelAndView handleInsufficientBalanceException(InsufficientBalanceException ex) {
        ModelAndView modelAndView = new ModelAndView("fundTransferForm");
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.addObject("transfer", new FundTransferDto());
        return modelAndView;
    }
}
