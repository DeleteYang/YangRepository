package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.MemberDao;
import com.atguigu.pojo.Member;
import com.atguigu.service.MemberService;
import com.atguigu.utils.DateUtils;
import com.atguigu.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceimpl implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        //使用md5的算法进行加密
        if (member.getPassword()!= null){
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> list) {
        ArrayList<Integer> memberCountList = new ArrayList<>();
        if (list!=null && list.size()>0){
            for (String month : list) {
                //获取指定月份的最后一天
                String dayOfMonth = DateUtils.getLastDayOfMonth(month);
                //迭代过去12个月，每个月注册会员的数量，根据注册日期查询
                Integer memberCount =  memberDao.findMemberCountBeforeDate(dayOfMonth);
                memberCountList.add(memberCount);

            }
        }
        return memberCountList;
    }
}
