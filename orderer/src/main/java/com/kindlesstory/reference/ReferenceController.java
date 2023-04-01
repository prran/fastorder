package com.kindlesstory.reference;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

@Controller
@Profile({ "dev,!real" })
public class ReferenceController
{
    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
    }
    
    @GetMapping("/")
    public String getRoot(final Model model, final Error err) {
        return "index";
    }
}