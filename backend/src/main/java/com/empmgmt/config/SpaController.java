package com.empmgmt.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    /**
     * Forward all non-API, non-static routes to index.html for React Router.
     */
    @RequestMapping(value = {"/", "/employees/**", "/departments/**", "/positions/**",
            "/attendance/**", "/leave-types/**", "/leave-requests/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
