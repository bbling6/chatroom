package cn.itcast.chatroom.dao;

import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;

import java.util.List;
import java.util.Set;

public interface RedisDao  {

     boolean exists(final String key);

     void set(final String key, final String value);

     void setEx(final String key, final String value,
                      final long expireSeconds);

     Set<byte[]> keys(String pattern);

     String getString(String key);

     String getString(byte[] key);

     byte[] get(String key);

     byte[] get(byte[] key);

    /* List operate. */
    /* LPUSH 操作 获取最新的N条记录.每次只能操作一个元素 */
     Long lPush(String keyList, String values);

     Long lPush(byte[] keyList, byte[] values);

    /* start from 0 begin. */
     void lTrim(byte[] keyList, long start, long end);

     List<byte[]> lRange(byte[] keyList, long begin, long end);

    /* 添加有续集.SortedSet */

     boolean zadd(byte[] zkey, double score, byte[] value);

    /* get value from sortedset. */
     Set<byte[]> zrang(byte[] zkey, long begin, long end);

     Set<Tuple> zrevrangeWithScores(byte[] zkey , long begin , long end);
}
