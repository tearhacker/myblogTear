package com.star.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.star.entity.Type;
import com.star.queryvo.FirstPageBlog;
import com.star.service.BlogService;
import com.star.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 分类页面控制器
 * @Date: Created in 10:03 2020/6/24
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
public class TypeShowController {

    @Autowired
    private TypeService typeService;   //分类实现接口

    @Autowired
    private BlogService blogService;   //博客实现接口

    //    分页查询分类
    @GetMapping("/types/{id}")
    public String types(@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum, @PathVariable Long id, Model model) {
        List<Type> types = typeService.getAllTypeAndBlog();

        //id为-1表示从首页导航栏点击进入分类页面
        if (id == -1) {
            if(!types.isEmpty()){
                id = types.get(0).getId();
            }
        }
        model.addAttribute("types", types);
        List<FirstPageBlog> blogs = blogService.getByTypeId(id);  //查询分类返回博客List列表对象

        PageHelper.startPage(pageNum, 10000);
        PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogs);
        model.addAttribute("pageInfo", pageInfo);   //把查询到的博客泛型类对象（list列表）全部加载传输到前端
        model.addAttribute("activeTypeId", id);  //特别标准传输分类ID标签
        return "types";
    }

}