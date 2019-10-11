package com.yp.controller;

import com.yp.vo.IndexVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fengzheng
 * @create 2019-10-11 21:19
 * @desc 入口
 **/
@Controller
public class IndexController {

    /**
     * 处理入口
     */
    @PostMapping("/create")
    @ResponseBody
    public String doCreateFile(IndexVO vo) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
        return "200";
    }

    /**
     * 返回成功跳转页面
     * @return
     */
    @RequestMapping("/success")
    public String doCreateFile() {
        return "success";
    }


    /**
     * 进入首页
     * @return
     */
    @RequestMapping("/")
    public String firstIndex(){
        return "index";
    }


}