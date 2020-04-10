package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.TravelItemDao;
import com.atguigu.entity.PageResult;
import com.atguigu.pojo.TravelItem;
import com.atguigu.service.TravelItemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Yang
 */
@Service(interfaceClass = TravelItemService.class)
@Transactional
public class TravelItemServiceImpl implements TravelItemService {

    @Autowired
    private TravelItemDao travelItemDao;

    @Override
    public void add(TravelItem travelItem) {
        travelItemDao.add(travelItem);
    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //1.初始化分页操作，
        PageHelper.startPage(currentPage, pageSize);
        //2.使用SQL语句查询，有查询范围时查询对应数据，无默认查询所有，
        Page<TravelItem> page = travelItemDao.findPage(queryString);
        //3.将查询结果封装为PageResult
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(Integer id) {
        //在删除自由行数据时，先判断自由行的id，在中间表中是否存在
        long count = travelItemDao.findCountByTravelItemItemId(id);
        if (count > 0) {
            throw new RuntimeException("此自由行不允许删除");
        } else {
            //根据id删除自由行
            travelItemDao.deleteById(id);
        }

    }

    @Override
    public TravelItem findById(Integer id) {
        //根据id查询自由行信息
        TravelItem travelItem = travelItemDao.findById(id);
        return travelItem;
    }

    @Override
    public void edit(TravelItem travelItem) {
        travelItemDao.edit(travelItem);
    }

    @Override
    public List<TravelItem> findAll() {
        return travelItemDao.findAll();
    }
}
