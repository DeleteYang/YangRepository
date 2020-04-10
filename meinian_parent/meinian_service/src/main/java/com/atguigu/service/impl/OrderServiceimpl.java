package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.constant.MessageConstant;
import com.atguigu.dao.MemberDao;
import com.atguigu.dao.OrderDao;
import com.atguigu.dao.OrdersettingDao;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Member;
import com.atguigu.pojo.Order;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrderService;
import com.atguigu.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceimpl implements OrderService {

    @Autowired
    OrdersettingDao ordersettingDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    OrderDao orderDao;

    /**
     * 根据id查询预约信息，包括旅人信息，套餐信息
     * @param id
     * @return
     */
    @Override
    public Map findById4Detail(Integer id) throws Exception {

        Map map = memberDao.findById4Detail(id);
        if (map != null){
            Date orderDate = (Date) map.get("orderDate");
            map.put(orderDate, DateUtils.parseDate2String(orderDate));
            return map;
        }
        return map;
    }

    /**
     * 旅游预约
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public Result order(Map map) throws Exception {
        //1. 检查当前预约日期是否进行了预约设置，查看能不能预约
        //因为数据库预约设置表里面的时间是date类型，http协议传递的是字符串类型，需要转换
        String orderDate = (String) map.get("orderDate");//获取预约时间
        Date date = DateUtils.parseString2Date(orderDate);
        //使用预约时间查询预约设置表，查看是否可以预约
        OrderSetting orderSetting = ordersettingDao.findByOrderDate(date);
        //如果为空，则预约时间未进行预约设置，即未开团
        if (orderSetting==null){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2.检查预约日期是否已预约满
        int number = orderSetting.getNumber();//可预约人数
        int reservations = orderSetting.getReservations();//已预约人数
        if (reservations>= number){
            //预约已满，不能预约
            return new Result(false, MessageConstant.ORDER_FULL);
        }

        //3.检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        //获取手机号
        String telephone = (String) map.get("telephone");//获取手机号
        //根据手机号，查询会员表，判断当前预约人是否为会员
        Member member = memberDao.findByTelephone(telephone);
        Integer setmealId = Integer.parseInt((String) map.get("setmealId"));//套餐id
        //如果是会员，防止重复预约（一个会员，一个时间，一个套餐，不能重复，否则为重复预约）
        if (member != null){
            Integer id = member.getId();//会员id

            Order order = new Order(null, id, date, null, null, setmealId);
            //根据预约信息查询是否已经预约
            Integer count = orderDao.findByCondition(order);

            //判断count是否大于0，大于则为重复预约
            if (count>0){
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }else {
            //如果不是会员，自动注册为会员
            //设置会员信息
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(date);
            //添加会员
            memberDao.add(member);
        }

        //可以预约，修改已预约人数加一
        orderSetting.setReservations(orderSetting.getReservations()+1);
        ordersettingDao.editReservationsByOrderDate(orderSetting);

        //保存预约信息到预约表
        Order order = new Order(member.getId(),date,(String) map.get("orderType"),Order.ORDERSTATUS_NO,setmealId);
        orderDao.add(order);

        return new Result(true, MessageConstant.ORDER_SUCCESS,order);
    }
}
