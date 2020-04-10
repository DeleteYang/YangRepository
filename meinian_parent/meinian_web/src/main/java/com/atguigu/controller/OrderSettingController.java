package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrdersettingService;
import com.atguigu.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/ordersetting")
@RestController
public class OrderSettingController {

    @Reference
    OrdersettingService ordersettingService;

    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try {
            ordersettingService.editNumberByDate(orderSetting);
            //设置成功
            return new Result(true,MessageConstant.ORDER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
    }

    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date) {//参数格式为2020-3
        try {
            List<Map<String,Object>> list = ordersettingService.getOrderSettingByMonth(date);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.QUERY_ORDER_FAIL);
        }
    }

    //上传预约表格
    @RequestMapping("/upload")
    public Result upload(MultipartFile excelFile){
        try {
            // 使用poi工具类解析excel文件，读取里面的内容
            List<String[]> strings = POIUtils.readExcel(excelFile);
            // 把List<String[]> 数据转换成 List<OrderSetting>数据
            ArrayList<OrderSetting> orderSettings = new ArrayList<>();
            //  迭代里面的每一行数据，进行封装到集合里面
            for (String[] string : strings) {
                //获取每一行的数据，进行封装
                OrderSetting orderSetting = new OrderSetting(new Date(string[0]),Integer.parseInt(string[1]));
                orderSettings.add(orderSetting);
            }
            //调用业务保存到数据库中
            ordersettingService.add(orderSettings);
            //返回执行结果和提示消息
            return new Result(true, MessageConstant.ORDER_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }
}
