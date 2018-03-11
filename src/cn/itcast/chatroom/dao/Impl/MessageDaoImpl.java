package cn.itcast.chatroom.dao.Impl;

import cn.itcast.chatroom.dao.MessageDao;
import cn.itcast.chatroom.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class MessageDaoImpl implements MessageDao {

    private final static String collectionName ="message";

    @Autowired
    private MongoTemplate mongoTemplate;


    public void insert(Message object) {
        mongoTemplate.insert(object, collectionName);

    }


    public List<Message> findAll() {
        List<Message> messages = mongoTemplate.findAll(Message.class,collectionName);
        return messages;
    }


    //基本用不着
    public void removeOne(String id) {
        Criteria criteria = Criteria.where("id").in(id);

        if (criteria != null) {
            Query query = new Query(criteria);
            if (query != null
                    && mongoTemplate.findOne(query, Message.class,collectionName) != null)
                mongoTemplate.remove(mongoTemplate.findOne(query, Message.class,collectionName),collectionName);
        }
    }


    public Message findAndRemove(Message criteriaUser) {
        Query query = getQuery(criteriaUser);
        return mongoTemplate.findAndRemove(query, Message.class,collectionName);
    }


    public long count(Message criteriaUser) {
        Query query = getQuery(criteriaUser);
        return mongoTemplate.count(query, Message.class,collectionName);
    }


    public List<Message> find(Message criteriaUser, int skip, int limit) {
        Query query = getQuery(criteriaUser);
        query.skip(skip);
        query.limit(limit);
        return mongoTemplate.find(query, Message.class,collectionName);
    }


    public void createCollection(String name) {
        mongoTemplate.createCollection(name);
    }


    private Query getQuery(Message criteriaUser) {
        if (criteriaUser == null) {
            criteriaUser = new Message();
        }
        Query query = new Query();
        if (criteriaUser.getFrom() != null) {
            Criteria criteria = Criteria.where("from").is(criteriaUser.getFrom());
            query.addCriteria(criteria);
        }
        if (criteriaUser.getTo() !=null) {
            Criteria criteria = Criteria.where("to").gt(criteriaUser.getTo());
            query.addCriteria(criteria);
        }
        if (criteriaUser.getFromName() != null) {
            Criteria criteria = Criteria.where("fromName").regex(
                    "^" + criteriaUser.getFromName());
            query.addCriteria(criteria);
        }
        return query;
    }
}
