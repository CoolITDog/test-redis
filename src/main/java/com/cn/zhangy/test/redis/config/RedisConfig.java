package com.cn.zhangy.test.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 * @title RedisConfiguration
 * SpringBoot自动帮我们在容器中生成了一个RedisTemplate和一个StringRedisTemplate。但是，这个RedisTemplate的泛型是<Object,Object>，写代码不方便，需要写好多类型转换的代码；
 * 我们需要一个泛型为<String,Object>形式的RedisTemplate。并且，这个RedisTemplate没有设置数据存在Redis时，key及value的序列化方式。
 * 自动生成的RedisAutoConfiguration带有注解@ConditionalOnMissingBean。它具体作用：在当前Spring上下文中不存在某个对象时，才会实例化一个Bean。
 * 也就是说，当你依赖中加入Redis的start后，SpringBoot默认会自动给你实例化一个redisRemplate。但是，它会自动给你实例化的条件是当前上下文中没有redisRemplate对象。
 * 所以当我们自己手动给实例化一个redisRemplate对象后，SpringBoot的就不会再给你实例化一个redisRemplate对象了。
 * 为何我要自己手动实例化一个redisRemplate对象？因为我想加一些定制化的东西，比如：key的序列化方式，value的序列化方式等。
 * @author zhangyan
 * @date 2020-03-25 18:29
 * @version 1.0
 **/
@Configuration
@EnableCaching //开启注解
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
