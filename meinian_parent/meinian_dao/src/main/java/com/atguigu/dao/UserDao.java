package com.atguigu.dao;

import com.atguigu.pojo.User;

/**
 * @author Yang
 */
public interface UserDao {
    User findUserByUsername(String username);
}
