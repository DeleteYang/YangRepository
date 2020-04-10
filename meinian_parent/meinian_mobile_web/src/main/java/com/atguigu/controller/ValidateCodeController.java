package com.atguigu.controller;

import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.utils.SMSUtils;
import com.atguigu.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    JedisPool jedisPool;

    /**
     * 手机快速登录的验证码
     * @param telephone
     * @return
     */
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        //获取六位验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        //发送验证码
        try {
            //将验证码保存到redis中，并设置存活时间
            jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_LOGIN, 5*60, code.toString());
            System.out.println(code);
//            SMSUtils.sendShortMessage(telephone, code.toString());
            //验证码发送成功
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }

    //预约时发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        //获取六位随机验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        //发送验证码
        try {
            //将验证码保存到redis中，并设置存活时间
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code.toString());
            System.out.println(code);
//            SMSUtils.sendShortMessage(telephone,code.toString());
            //验证码发送成功
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }


    }
}
