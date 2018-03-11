package cn.itcast.chatroom.dao;

import java.util.List;


public interface MongoBase<T> {

     void insert(T object);

     List<T> findAll();

     void removeOne(String id);

     T findAndRemove(T criteriaUser);

     long count(T criteriaUser);

     List<T> find(T criteriaUser, int skip, int limit);

    //创建集合  
     void createCollection(String collectionName);

      
}
