package cn.itcast.chatroom.service;

import cn.itcast.chatroom.dao.RedisDao;
import cn.itcast.utils.ConstantValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class RedisService {
    /**
     * redis 存储最新的20条群聊消息，
     */

    @Autowired
    private RedisDao redisDao;

    //存最新100数据,
    public boolean set(List<String> values){

        try {
            String key = ConstantValue.REDIS_NEW_KEY;
            Long listSize = 0l;

            if (!CollectionUtils.isEmpty(values)) {
                for (String value : values) {
                    listSize = redisDao.lPush(key,value);
                }
                if(listSize > 99){
                    Ltrim(key);
                }
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    //保持固定长度100
    public void Ltrim(String key){
        redisDao.lTrim(key.getBytes(), 0, 99);
    }

    //取 最新的10条 redis数据
    public List get(){
        String key = ConstantValue.REDIS_NEW_KEY;
        //通过Lrange 获取最新的10条记录
        return redisDao.lRange(key.getBytes(), 0, 9);
    }

}
