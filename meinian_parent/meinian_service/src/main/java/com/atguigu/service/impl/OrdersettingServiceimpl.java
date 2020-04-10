package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.OrdersettingDao;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrdersettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrdersettingService.class)
@Transactional
public class OrdersettingServiceimpl implements OrdersettingService {

    @Autowired
    OrdersettingDao ordersettingDao;

    @Override
    public void add(ArrayList<OrderSetting> orderSettings) {
        //遍历list，
        for (OrderSetting orderSetting : orderSettings) {
            //判断当前的日期之前是否已经被设置过预约日期，使用当前时间作为条件查询数量
            long count = ordersettingDao.findCountByOrderDate(orderSetting.getOrderDate());
            // 如果设置过预约日期，更新number数量
            if(count>0){
                ordersettingDao.editNumberByOrderDate(orderSetting);
            }else {
                // 如果没有设置过预约日期，执行保存
                ordersettingDao.add(orderSetting);
            }

        }
    }

    /**  传递的参数
     *   date（格式：2019-8）
     *  构建的数据List<Map>
     *    map.put(“date”,1);
     map.put(“number”,120);
     map.put(“reservations”,10);

     *  查询方案：SELECT * FROM t_ordersetting WHERE orderDate LIKE '2019-08-%'
     *  查询方案：SELECT * FROM t_ordersetting WHERE orderDate BETWEEN '2019-9-1' AND '2019-9-31'
     */
    //根据日期查询预约设置数据
    @Override
    public List<Map<String, Object>> getOrderSettingByMonth(String date) {
        // 1.组织查询Map，dateBegin表示月份开始时间，dateEnd月份结束时间
        String dateBegin = date+"-1";
        String dateEnd = date+"-31";
        // 2.查询当前月份的预约设置
        HashMap<String, Object> map = new HashMap<>();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        List<OrderSetting> lists = ordersettingDao.getOrderSettingByMonth(map);
        // 3.将List<OrderSetting>，组织成List<Map>
        List<Map<String, Object>> data = new ArrayList<>();
        for (OrderSetting orderSetting : lists) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("date", orderSetting.getOrderDate().getDate());
            hashMap.put("number", orderSetting.getNumber());
            hashMap.put("reservations", orderSetting.getReservations());
            data.add(hashMap);
        }
        return data;
    }

    //根据日期修改预约人数
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        long count = ordersettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //当前日期已经进行了预约设置，需要进行修改操作
            ordersettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //当前日期没有进行预约设置，进行添加操作
            ordersettingDao.add(orderSetting);
        }
    }
}
