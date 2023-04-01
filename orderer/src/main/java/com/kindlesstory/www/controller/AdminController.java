package com.kindlesstory.www.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.kindlesstory.www.data.jpa.table.User;
import com.kindlesstory.www.exception.DatabaseException;
import com.kindlesstory.www.data.jpa.table.Item;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import com.kindlesstory.www.service.model.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.model.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class AdminController
{
    private static final String MAIN_PATH = "/admin";
    private static final String BAN_USER = "/admin/ban";
    private static final String DELETE_ITEM = "/admin/delete";
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    static final String PATH = "admin/index";
    
    @GetMapping(MAIN_PATH)
    public String adminMainView(@RequestParam(value = "search-text", required = false) final String text, final Model model) {
        model.addAttribute("itemNameList", null);
        model.addAttribute("itemCodeList", null);
        model.addAttribute("isBan", false);
        model.addAttribute("isFound", false);
        if (text != null && !text.equals("")) {
            try {
                final User user = userService.findByName(text);
                if (user == null) {
                    model.addAttribute("userName",  "NOT FOUND");
                    return "admin/index";
                }
                final List<Item> userItems = itemService.findById(user.getUserId());
                final String[] userItemNames = userItems.stream().map(Item::getItemName).toArray(String[]::new);
                final String[] userItemCodes = userItems.stream().map(Item::getItemRefCode).toArray(String[]::new);
                model.addAttribute("userName", text);
                model.addAttribute("itemNameList", userItemNames);
                model.addAttribute("itemCodeList", userItemCodes);
                model.addAttribute("isBan", user.getUserBan());
                model.addAttribute("isFound", true);
                return "admin/index";
            }
            catch (DatabaseException e) {
                model.addAttribute("userName", "ERROR");
                return "admin/index";
            }
        }
        model.addAttribute("userName", "검색한 항목이 여기에 출력됩니다.");
        return "admin/index";
    }
    
    @ResponseBody
    @GetMapping(BAN_USER)
    public String banUser(@RequestParam("value") final String name) {
        this.userService.reverseBan(name);
        return name;
    }
    
    @ResponseBody
    @GetMapping(DELETE_ITEM)
    public String deleteItem(@RequestParam("value") final String itemCode) {
        this.itemService.deleteItem(itemCode);
        return itemCode;
    }
}