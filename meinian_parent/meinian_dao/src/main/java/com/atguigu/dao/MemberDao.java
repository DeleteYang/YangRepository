package com.atguigu.dao;

import com.atguigu.pojo.Member;
import org.springframework.stereotype.Repository;

import java.util.Map;

public interface MemberDao {
    Member findByTelephone(String telephone);

    void add(Member member);

    Map findById4Detail(Integer id);

    Integer findMemberCountBeforeDate(String dayOfMonth);

    int getTodayNEwMember(String today);

    int getTotalMember();

    int getThisWeekAndMonthNewMember(String weekMonday);
}
