package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Member;
import com.atguigu.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Reference
    MemberService memberService;

    @Autowired
    JedisPool jedisPool;

    /**
     * 使用手机号验证登录
     * @param response
     * @param map
     * @return
     */
    @RequestMapping("/check")
    public Result check(HttpServletResponse response, @RequestBody Map map){
        String telephone = (String) map.get("telephone");//手机号
        String validateCode = (String) map.get("validateCode");//验证码

        //获取发送时缓存在redis中的验证码
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        //校验验证码
        if (codeInRedis==null || !codeInRedis.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

        //验证码正确，判断是否为会员，不是则自动注册
        Member member = memberService.findByTelephone(telephone);
        if (member == null){
            //非会员，自动注册
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            memberService.add(member);
        }

        //登录成功，写入cookie，跟踪用户
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        cookie.setPath("/");//路径
        cookie.setMaxAge(15*24*60*60);//有效时间  秒
        response.addCookie(cookie);

        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }
}
