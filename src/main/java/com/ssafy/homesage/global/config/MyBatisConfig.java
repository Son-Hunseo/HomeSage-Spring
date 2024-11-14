package com.ssafy.homesage.global.config;

import com.ssafy.homesage.domain.user.mapper.typehandler.UserRoleTypeHandler;
import com.ssafy.homesage.domain.user.model.enums.UserRole;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // UserRoleTypeHandler를 전역 설정에 추가합니다.
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.getTypeHandlerRegistry().register(UserRole.class, new UserRoleTypeHandler());
        factoryBean.setConfiguration(configuration);

        return factoryBean.getObject();
    }
}
