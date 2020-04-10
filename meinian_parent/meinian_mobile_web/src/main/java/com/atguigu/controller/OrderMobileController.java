package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Order;
import com.atguigu.service.OrderService;
import com.atguigu.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderMobileController {

    @Reference
    OrderService orderService;

    @Autowired
    JedisPool jedisPool;

    /**
     * 根据订单id查找信息
     * <p>旅游人：{{orderInfo.member}}</p>
     * <p>旅游套餐：{{orderInfo.setmeal}}</p>
     * <p>旅游日期：{{orderInfo.orderDate}}</p>
     * <p>预约类型：{{orderInfo.orderType}}</p>
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map map = orderService.findById4Detail(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }

    /**
     * 提交预约
     * @param map
     * @return
     */
    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map map) {
        //1.获取页面的手机号
        String telephone = (String) map.get("telephone");
        //2.获取页面输入的验证码
        String validateCode = (String) map.get("validateCode");
        //3.获取发送的验证码,缓存在redis中，key为telephone+RedisConstant.SENDTYPE_ORDER
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //4.校验手机验证码
        if (codeInRedis == null || !codeInRedis.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

        //5.调用旅游预约服务
        Result result = null;
        try {
            //设置预约类型
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            //调用service
            result = orderService.order(map);
        } catch (Exception e) {
            e.printStackTrace();
            //预约失败
            return new Result(false, "预约失败");
        }

//        if (result.isFlag()){
//            //预约成功，发送短信通知，短信通知内容可以是“预约时间”，“预约人”，“预约地点”，“预约事项”等信息。
//            String orderDate = (String) map.get("orderDate");
//            try {
//                SMSUtils.sendShortMessage(telephone, orderDate);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        return result;
    }
}
