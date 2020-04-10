package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.TravelItem;
import com.atguigu.service.TravelItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/travelItem")
public class TravelItemController {

    //远程调用使用@Reference
    @Reference
    TravelItemService travelItemService;

    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<TravelItem> list = travelItemService.findAll();
            return new Result(true, MessageConstant.QUERY_TRAVELITEM_SUCCESS,list);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_TRAVELITEM_FAIL);
        }
    }

    @RequestMapping("/edit")
    @PreAuthorize("hasAnyAuthority('TRAVELITEM_EDIT')")//权限校验
    public Result edit(@RequestBody TravelItem travelItem){
        travelItemService.edit(travelItem);
        return new Result(true, MessageConstant.EDIT_TRAVELITEM_SUCCESS);
    }

    @RequestMapping("/findById")
    public Result findById(Integer id) {
        TravelItem travelItem = travelItemService.findById(id);
        return new Result(true, MessageConstant.QUERY_TRAVELITEM_SUCCESS,travelItem);
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasAnyAuthority('TRAVELITEM_DELETE')")//权限校验，使用TRAVELITEM_DELETE123测试
    public Result deleteById(Integer id){
        try {
            travelItemService.deleteById(id);
            return new Result(true, MessageConstant.DELETE_TRAVELITEM_SUCCESS);
        }catch (RuntimeException e){
            //运行时异常，表示这条自由行数据和团游的关联表中存在数据（级联）
            return new Result(false, e.getMessage());
        }
        catch (Exception e) {
            return new Result(false, MessageConstant.DELETE_TRAVELITEM_FAIL);
        }

    }

    @RequestMapping("/findPage")
    @PreAuthorize("hasAnyAuthority('TRAVELITEM_QUERY')")//校验权限
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = travelItemService.findPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize(),queryPageBean.getQueryString());
        return pageResult;
    }

    /**
     * 新增自由行
     * @param travelItem
     * @return
     */
    @RequestMapping("/add")
    @PreAuthorize("hasAnyAuthority('TRAVELITEM_ADD')")//校验权限
    public Result add(@RequestBody TravelItem travelItem){
        try {
            travelItemService.add(travelItem);
        } catch (Exception e) {
            //查看方法所需参数Ctrl+p
            //第一个参数：新增成功还是失败
            //第二个参数：返回给客户端的提示消息
            e.printStackTrace();
            return  new Result(false, MessageConstant.ADD_TRAVELITEM_FAIL);
        }
        return new Result(true, MessageConstant.ADD_TRAVELITEM_SUCCESS);
    }
}
