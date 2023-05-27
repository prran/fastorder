package com.kindlesstory.www.controller;

import org.springframework.web.bind.annotation.GetMapping;
import com.kindlesstory.www.data.rest.RestData;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RejectController
{
    @GetMapping("/reject")
    public RestData rejectConnect() {
        RestData restdata = new RestData();
        restdata.setLocation("/reject");
        restdata.setStatus(421);
        restdata.setMessage("connection reject because it expected to hacking attack");
        return restdata;
    }
}