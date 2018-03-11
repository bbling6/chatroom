package test;


import cn.itcast.chatroom.dao.UserDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:resources/applicationContext.xml"})
public class TestUser {

	@Autowired
	private UserDao userDaoImpl;

	        @Test
	        public void testAdd()  
	        {

				System.out.println("************************");

				//ApplicationContext app = new ClassPathXmlApplicationContext("resources/applicationContext.xml");

			//userDao =(UserDao) app.getBean("userDaoImpl");
//				collectionName ="user";
//
//				User user =new User();
//				user.setId(""+1);
//				user.setAge(1);
//				user.setName("zcy"+1);
//
//				userDao.insert(user,collectionName);
//
//	            Map<String,Object> params=new HashMap<String,Object>();
//	            params.put("maxAge", 50);
//
//	            List<User> list=userDao.findAll(params,collectionName);
//	            System.out.println("user.count()=="+list.size());
	        }  
}
