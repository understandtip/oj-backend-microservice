package com.oj.ojbackenduserservice.controller.inner;

import com.oj.ojbackendmodel.model.entity.User;
import com.oj.ojbackendserviceclient.service.UserFeignClient;
import com.oj.ojbackenduserservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author jackqiu
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Autowired
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
