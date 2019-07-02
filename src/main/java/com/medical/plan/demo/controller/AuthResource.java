package com.medical.plan.demo.controller;

import com.medical.plan.demo.Tools.Utils;
import com.nimbusds.jose.JOSEException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @GetMapping("/")
    public String findAll() throws JOSEException {
        return Utils.createJWT();
    }
}
