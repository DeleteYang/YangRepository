package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.TravelGroup;
import com.atguigu.service.TravelGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/travelgroup")
public class TravelGroupController {

    @Reference
    TravelGroupService travelGroupService;

    //查找所有跟团游
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<TravelGroup> list = travelGroupService.findAll();
            if (list != null && list.size()>0){
                return new Result(true, MessageConstant.QUERY_TRAVELGROUP_SUCCESS, list);
            }else {
                return new Result(false, "目前无跟团游");
            }
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_TRAVELGROUP_FAIL);
        }
    }

    // 编辑跟团游（返回 public Result(boolean flag, String message)）
    @RequestMapping("/edit")
    public Result edit(@RequestBody TravelGroup travelGroup,Integer[] travelItemIds){
        try {
            travelGroupService.edit(travelGroup,travelItemIds);
            return new Result(true, MessageConstant.EDIT_TRAVELGROUP_SUCCESS);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_TRAVELGROUP_FAIL);
        }
    }

    // 使用id查询跟团游，进行表单回显
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            TravelGroup travelGroup = travelGroupService.findById(id);
            return new Result(true, MessageConstant.QUERY_TRAVELGROUP_SUCCESS,travelGroup);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_TRAVELGROUP_FAIL);
        }
    }

    // 使用组团游id，查询跟团游和自由行的中间表，获取自由行的集合，存放id的值
    @RequestMapping("/findTravelItemIdByTravelgroupId")
    public List<Integer> findTravelItemIdByTravelgroupId(Integer id){
        return travelGroupService.findTravelItemIdByTravelgroupId(id);
    }

    // 传递当前页，每页显示的记录数，查询条件
    // 响应PageResult，封装总记录数，结果集
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = travelGroupService.findPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize(),queryPageBean.getQueryString());
        return pageResult;
    }

    //
    @RequestMapping("/add")
    public Result add(@RequestBody TravelGroup travelGroup, Integer[] travelItemIds){
        try {
            travelGroupService.add(travelGroup,travelItemIds);
            return  new Result(true, MessageConstant.ADD_TRAVELGROUP_SUCCESS);
        } catch (Exception e) {
            return  new Result(false, MessageConstant.ADD_TRAVELGROUP_FAIL);
        }
    }
}
