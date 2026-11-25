package com.TrainingTracker.TraingingTracker.unit;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RedisSerializationTest {

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

    private GenericJacksonJsonRedisSerializer serializer;

    @BeforeEach
    void setUp() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        CustomTypeResolverBuilder resolver = new CustomTypeResolverBuilder(
                typeValidator,
                DefaultTyping.NON_FINAL_AND_RECORDS,
                JsonTypeInfo.As.PROPERTY
        );

        serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableSpringCacheNullValueSupport()
                .customize(mapperBuilder -> {
                    mapperBuilder.setDefaultTyping(resolver)
                            .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                })
                .build();
    }

    @Test
    void testImmutableListSerialization() {
        TraineResponse response = new TraineResponse(
                1L, "test", "Trainee", "test@test.com", null, null,
                10L, 20L, 1L, 1L, 5L, "cfHandle", 1500L, 1600L, "specialist", "specialist"
        );

        List<TraineResponse> list = List.of(response);

        byte[] serialized = serializer.serialize(list);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        List<TraineResponse> deserialized = (List<TraineResponse>) serializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(1, deserialized.size());
        assertEquals("test", deserialized.get(0).userName());
    }

    @Test
    void testArrayListSerialization() {
        TraineResponse response = new TraineResponse(
                1L, "test", "Trainee", "test@test.com", null, null,
                10L, 20L, 1L, 1L, 5L, "cfHandle", 1500L, 1600L, "specialist", "specialist"
        );

        List<TraineResponse> list = new ArrayList<>(List.of(response));

        byte[] serialized = serializer.serialize(list);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        List<TraineResponse> deserialized = (List<TraineResponse>) serializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(1, deserialized.size());
        assertEquals("test", deserialized.get(0).userName());
    }

    @Test
    void testSingleObjectSerialization() {
        TraineResponse response = new TraineResponse(
                1L, "test", "Trainee", "test@test.com", null, null,
                10L, 20L, 1L, 1L, 5L, "cfHandle", 1500L, 1600L, "specialist", "specialist"
        );

        byte[] serialized = serializer.serialize(response);
        assertNotNull(serialized);

        TraineResponse deserialized = (TraineResponse) serializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals("test", deserialized.userName());
    }

    @Test
    void testBooleanAndLongSerialization() {
        byte[] serializedBool = serializer.serialize(true);
        assertEquals(true, serializer.deserialize(serializedBool));

        byte[] serializedLong = serializer.serialize(42L);
        assertEquals(42L, ((Number) serializer.deserialize(serializedLong)).longValue());
    }

    @Test
    void testNullValueSerialization() {
        byte[] serialized = serializer.serialize(NullValue.INSTANCE);
        Object deserialized = serializer.deserialize(serialized);
        assertInstanceOf(NullValue.class, deserialized);
    }
}
