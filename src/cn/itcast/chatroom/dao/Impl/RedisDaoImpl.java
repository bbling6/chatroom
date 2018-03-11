package cn.itcast.chatroom.dao.Impl;

import cn.itcast.chatroom.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Repository
public class RedisDaoImpl<T> implements RedisDao {

    @Autowired
    protected RedisTemplate<Serializable, Serializable> redisTemplate;

//    public void save(final T t) {
//        redisTemplate.execute(new RedisCallback<Object>() {
//
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                connection.set(redisTemplate.getStringSerializer().serialize("user.uid." + user.getId()),
//                        redisTemplate.getStringSerializer().serialize(user.getName()));
//                return null;
//            }
//        });
//    }


    private RedisSerializer<String> getStringRedisSerializable() {
        return redisTemplate.getStringSerializer();
    }

    //判断键是否存在
    public boolean exists(final String key){
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.exists(key.getBytes());
            }
        });
    }


    public void set(final String key, final String value) {

        String values = redisTemplate.execute(new RedisCallback<String>() {

            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.set(
                        redisTemplate.getStringSerializer().serialize(key),
                        redisTemplate.getStringSerializer().serialize(value));
                return value;
            }
        });
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + values);
    }

    public Set<byte[]> keys(final String pattern) {
        return redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
            public Set<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.keys(redisTemplate.getStringSerializer()
                        .serialize(pattern));
            }
        });

    }

    public String getString(final String key) {

        return redisTemplate.execute(new RedisCallback<String>() {

            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] value = connection.get(getStringRedisSerializable()
                        .serialize(key));
                if (value != null) {
                    return new String(value);
                }
                return null;
            }
        });

    }

    public String getString(final byte[] key) {
        return redisTemplate.execute(new RedisCallback<String>() {

            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] value = connection.get(key);
                if (value != null) {
                    return new String(value);
                }
                return null;
            }
        });
    }

    public byte[] get(final String key) {
        return redisTemplate.execute(new RedisCallback<byte[]>() {

            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.get(getStringRedisSerializable().serialize(
                        key));

            }
        });
    }

    public byte[] get(final byte[] key) {
        return redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.get(key);
            }
        });
    }

    public void setEx(final String key, final String value,
                      final long expireSeconds) {

        redisTemplate.execute(new RedisCallback<Object>() {

            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {

                connection.setEx(getStringRedisSerializable().serialize(key),
                        expireSeconds,
                        getStringRedisSerializable().serialize(value));
                return null;
            }
        });

    }

    public Long lPush(final String keyList, final String values) {

        return redisTemplate.execute(new RedisCallback<Long>() {

            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.lPush(
                        getStringRedisSerializable().serialize(keyList),
                        getStringRedisSerializable().serialize(values));
            }
        });

    }

    public Long lPush(final byte[] keyList, final byte[] values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.lPush(keyList, values);
            }
        });
    }

    public void lTrim(final byte[] keyList, final long start, final long end) {
        redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                connection.lTrim(keyList, start, end);
                return true;
            }
        });

    }

    public List<byte[]> lRange(final byte[] keyList, final long begin,
                               final long end) {

        return redisTemplate.execute(new RedisCallback<List<byte[]>>() {

            public List<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.lRange(keyList, begin, end);
            }
        });

    }

    public boolean zadd(final byte[] zkey, final double score,
                        final byte[] value) {

        return redisTemplate.execute(new RedisCallback<Boolean>() {

            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.zAdd(zkey, score, value);
            }

        });

    }

    public Set<byte[]> zrang(final byte[] zkey, final long begin, final long end) {

        return redisTemplate.execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.zRange(zkey, begin, end);
            }
        });

    }

    public Set<Tuple> zrevrangeWithScores(final byte[] zkey, final long begin,
                                                            final long end) {

        return redisTemplate.execute(new RedisCallback<Set<Tuple>>() {

            public Set<Tuple> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.zRevRangeWithScores(zkey, begin, end);
            }

        });

    }




}
