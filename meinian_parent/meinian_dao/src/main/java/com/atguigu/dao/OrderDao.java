package com.atguigu.dao;

import com.atguigu.pojo.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    Integer findByCondition(Order order);

    void add(Order order);

    int getTodayOrderNumber(String today);

    int getTodayVisitsNumber(String today);

    int getThisWeekAndMonthOrderNumber(HashMap<String, Object> paramWeek);

    int getThisWeekAndMonthVisitsNumber(HashMap<String, Object> paramWeekVisit);

    List<Map<String, Object>> findHotSetmeal();
}
