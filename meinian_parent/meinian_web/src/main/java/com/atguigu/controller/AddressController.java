package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Address;
import com.atguigu.service.AddressService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/address")
@RestController
public class AddressController {

    @Reference
    AddressService addressService;

    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){
        try {
            addressService.deleteById(id);
            return new Result(true, "分公司地址删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "分公司地址删除失败");

        }
    }

    /**
     * 添加分公司地址
     * @param address
     * @return
     */
    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Address address){
        try {
            addressService.addAddress(address);
            return new Result(true,"分公司地址添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"分公司地址添加失败");
        }
    }

    @RequestMapping("/findPage")
    public PageResult PageResult(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = null;
        try {
            pageResult = addressService.findPage(queryPageBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageResult;
    }

    /**
     * 获取所有分公司地址
     * @return
     */
    @RequestMapping("/findAllMaps")
    public Map findAllMaps() {
        HashMap<String, Object> map = new HashMap<>();
        //调用服务层查询
        List<Address> addressList = addressService.findAll();

        //1、定义分店坐标集合
        ArrayList<Map> gridnMaps = new ArrayList<>();
        //2、定义分店名称集合
        ArrayList<Map> nameMaps = new ArrayList<>();

        for (Address address : addressList) {
            Map gridnMap = new HashMap();
            // 获取经度
            gridnMap.put("lng", address.getLng());
            // 获取纬度
            gridnMap.put("lat", address.getLat());

            gridnMaps.add(gridnMap);

            Map nameMap = new HashMap();
            // 获取地址的名字
            nameMap.put("addressName", address.getAddressName());
            nameMaps.add(nameMap);
        }

        map.put("gridnMaps", gridnMaps);
        map.put("nameMaps",nameMaps);

        return map;
    }
}
