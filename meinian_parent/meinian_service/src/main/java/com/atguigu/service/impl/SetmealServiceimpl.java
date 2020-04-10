package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.constant.RedisConstant;
import com.atguigu.dao.SetmealDao;
import com.atguigu.entity.PageResult;
import com.atguigu.pojo.Setmeal;
import com.atguigu.service.SetmealService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceimpl implements SetmealService {

    @Autowired
    SetmealDao setmealDao;
    @Autowired
    JedisPool jedisPool;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] travelgroupIds) {
        // 新增套餐
        setmealDao.add(setmeal);
        //将套餐上传的图片名称保存在redis中
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());
        //2：向套餐和跟团游的中间表中插入数据
        if (travelgroupIds != null && travelgroupIds.length>0){
            //绑定套餐和跟团游的多对多关系
            setSetmealAndTravelGroup(setmeal.getId(),travelgroupIds);
        }
    }

    //分页查询
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.findPage(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //绑定套餐和跟团游的多对多关系
    private void setSetmealAndTravelGroup(Integer id, Integer[] travelgroupIds) {
        HashMap<String, Integer> map = new HashMap<>();
        for (Integer travelgroupId : travelgroupIds) {
            map.put("setmealId",id);
            map.put("travelgroupId",travelgroupId);
            setmealDao.setSetmealAndTravelGroup(map);
        }
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    // 组织套餐名称+套餐名称对应的数据
    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }
}
