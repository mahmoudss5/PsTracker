package com.TrainingTracker.TraingingTracker.Config.Redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@Configuration
public class RedisConfig {

    static class CustomTypeResolverBuilder extends DefaultTypeResolverBuilder {
        public CustomTypeResolverBuilder(PolymorphicTypeValidator subtypeValidator, DefaultTyping t, JsonTypeInfo.As includeAs) {
            super(subtypeValidator, t, includeAs);
        }

        @Override
        public boolean useForType(JavaType javaType) {
            if (javaType.isJavaLangObject()) {
                return true;
            }
            if (Collection.class.isAssignableFrom(javaType.getRawClass()) || Map.class.isAssignableFrom(javaType.getRawClass())) {
                return true;
            }
            return super.useForType(javaType);
        }
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        CustomTypeResolverBuilder resolver = new CustomTypeResolverBuilder(
                typeValidator,
                DefaultTyping.NON_FINAL_AND_RECORDS,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableSpringCacheNullValueSupport()
                .customize(mapperBuilder -> {
                    mapperBuilder.setDefaultTyping(resolver)
                            .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                })
                .build();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(2))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
