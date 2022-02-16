package com.lzf.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

// sqlSessionFactory --> sqlSession
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory = null;
    static {
        //获取sqlSessionFactory对象(官网）
        try {

        String resource = "mybatis-config.xml";//初始就加载
            InputStream inputStream = Resources.getResourceAsStream(resource);
            //工厂就是为了创建东西
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。
    SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。
    你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。例如：*/
//    设置自动提交事务为true
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession(true);
    }
}