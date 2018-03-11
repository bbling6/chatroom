package cn.itcast.chatroom.dao.Impl;

import cn.itcast.chatroom.dao.MongoBase;
import cn.itcast.chatroom.domain.Records;
import org.aspectj.weaver.ast.Instanceof;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/*
 * 还没成功实现，不可用
 */
public class MongoBaseImpl<T> implements MongoBase<T> {

    @Resource
    private MongoTemplate mongoTemplate;

    private  static String collectionName = null;

    T t;

    {
        if("Records".equals(t.getClass().getName())) {
            collectionName = "records";
        }else if("Users".equals(t.getClass().getName())){
            collectionName = "users";
        }
    }

    //获取当前参数化类型中实际定义class
    private Class getClazz(){
        Type type = this.getClass().getGenericSuperclass();
        return (Class) ((ParameterizedType)type).getActualTypeArguments()[0];
    }


    public void insert(T object) {
        mongoTemplate.insert(object, collectionName);
    }



    public List<T> findAll() {
        List<T> user = mongoTemplate.findAll(getClazz(),collectionName);
        return user;

    }

    public void removeOne(String id) {

    }

    public T findAndRemove(T criteriaUser) {
        return null;
    }

    public long count(T criteriaUser) {
        return 0;
    }

    public List find(T criteriaUser, int skip, int limit) {
        return null;
    }

    public void createCollection(String collName) {
        mongoTemplate.createCollection(collName);

    }
}
