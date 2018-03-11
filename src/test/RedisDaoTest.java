package test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import cn.itcast.chatroom.dao.RedisDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;


@ContextConfiguration(locations = { "classpath*:resources/applicationContext.xml" })
public class RedisDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private RedisDao redisService;

    @Test
    public void testSet() {
        String key = "name4";
        redisService.set(key, "wendy");
    }

    @Test
    public void testKeys() {

        Set<byte[]> keys = redisService.keys("*");

        if (!CollectionUtils.isEmpty(keys)) {
            for (byte[] bs : keys) {
                System.out.println(new String(bs));
            }
        }
    }

    @Test
    public void testGetString() {
        System.out.println(redisService.getString("name"));

        try {
            System.out.println(redisService.getString("name1".getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSetEx(){
        redisService.setEx("name5", "zhangsan", 5);

    }

    @Test
    public void testGet() {



    }
    @Test
    public void testLpush(){
        redisService.lPush("languelist", "GO,JAVA");
        redisService.lPush("languelist".getBytes(), "swift,php,shell".getBytes());
    }

    @Test
    public void testLtrim(){
        redisService.lTrim("languelist".getBytes(), 0, 9);
    }


    /*最新的N条记录.*/
    @Test
    public void testLeastN(){
        byte[] keyList = "languelist".getBytes();
        //在原有的基础上存入20条数据
        for (int i = 0; i < 20; i++) {
            redisService.lPush(keyList, ("id-"+i).getBytes());
        }
        //执行Ltrim 保持languelist里只有10条最新的记录.
        redisService.lTrim(keyList, 0, 9);

        //通过Lrange 获取最新的10条记录
        List<byte[]> list = redisService.lRange(keyList, 0, -1);
        if(list!=null&&!list.isEmpty()){
            for (byte[] bs : list) {
                System.out.println(new String(bs));
            }
        }
    }


    @Test
    public void testTopN(){
        byte[] zkey = "topic".getBytes();

        //通过各种手段把数据处理成 id  score 的样式，存入redis里.
        for (int i = 0; i < 20; i++) {
            redisService.zadd(zkey, Math.random(), ("wenzhang-id:"+i).getBytes());
        }
        //获取top 15.
        Set<byte[]> set = redisService.zrang(zkey, 0, 14);
        if(set!=null&&!set.isEmpty()){
            for (byte[] bs : set) {
                System.out.println(new String(bs));
            }
        }

        //获取倒叙的top 15
        Set<Tuple> setTop = redisService.zrevrangeWithScores(zkey, 0, 14);
        for (Tuple tuple : setTop) {
            System.out.println("socre="+tuple.getScore()+"value="+new String(tuple.getValue()));

        }
    }


}