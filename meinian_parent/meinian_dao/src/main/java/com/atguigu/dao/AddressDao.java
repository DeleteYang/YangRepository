package com.atguigu.dao;

import com.atguigu.pojo.Address;
import com.github.pagehelper.Page;

import java.util.List;

public interface AddressDao {
    List<Address> findAll();

    Page<Address> selectByCondition(String queryString);

    void deleteById(Integer id);

    void addAddress(Address address);
}
