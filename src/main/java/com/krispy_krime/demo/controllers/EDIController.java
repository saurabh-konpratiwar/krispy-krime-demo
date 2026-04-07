package com.krispy_krime.demo.controllers;

import com.krispy_krime.demo.services.EdiToCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edi")
public class EDIController {

    @Autowired
    private EdiToCsvService service;

    @GetMapping("/convert")
    public String convert() {
        try {
            service.convertEdiToCsv("input.edi", "output.csv");
            return "Conversion Successful!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}