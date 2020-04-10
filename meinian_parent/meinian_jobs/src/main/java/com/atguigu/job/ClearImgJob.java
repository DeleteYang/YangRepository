package com.atguigu.job;

import com.atguigu.constant.RedisConstant;
import com.atguigu.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;
    //清理垃圾图片文件
    public void clearImg(){
        //计算redis中两个集合的差值，获取垃圾图片名称的集合
        //在比较的时候，数据多的放在前面，如果key1多，key1放到前面，key2数据多，key2放在前面
        System.out.println("测试----------------------");
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()){
            String pic = iterator.next();
            System.out.println("删除的图片是："+pic);
            //删除图片服务器上的图片文件
            QiniuUtils.deleteFileFromQiniu(pic);
            //删除redis中的数据
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, pic);
        }
    }
}
