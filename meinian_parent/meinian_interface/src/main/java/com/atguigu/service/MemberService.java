package com.atguigu.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.pojo.Member;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberService {
    Member findByTelephone(String telephone);

    void add(Member member);

    List<Integer> findMemberCountByMonth(List<String> list);
}
