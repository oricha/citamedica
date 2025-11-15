# Redis Removal Documentation

This document describes all changes made to remove Redis from the CitaMedica project. These changes can be reversed in the future if Redis caching needs to be re-introduced.

## Date of Removal
Removed on: 2025-01-27

## Rationale
Redis was removed to simplify the system architecture. The application did not have active Redis usage in the codebase (no RedisTemplate, no @Cacheable annotations, no Redis configuration classes), making it safe to remove without impacting functionality.

## Changes Made

### 1. Docker Compose Configuration (`docker-compose.yml`)

#### Removed Services

**1.1. Backend Redis Service (redis)**
- **Location**: Lines 22-34
- **Removed Service Definition**:
```yaml
# Redis for caching
redis:
  image: redis:7-alpine
  container_name: citamedica-redis
  ports:
    - "6379:6379"
  networks:
    - citamedica-network
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**1.2. Cal.com Redis Service (redis-cal)**
- **Location**: Lines 108-120
- **Removed Service Definition**:
```yaml
# Redis for Cal.com
redis-cal:
  image: redis:7-alpine
  container_name: calcom-redis
  ports:
    - "6380:6379"
  networks:
    - citamedica-network
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
```

#### Removed Environment Variables

**1.3. Backend API Service - Redis Environment Variables**
- **Location**: Lines 50-51 (in backend-api service)
- **Removed Variables**:
```yaml
REDIS_HOST: redis
REDIS_PORT: 6379
```

**1.4. Cal.com Service - Redis Environment Variable**
- **Location**: Line 129 (in calcom service)
- **Removed Variable**:
```yaml
REDIS_URL: redis://redis-cal:6379
```

#### Removed Dependencies

**1.5. Backend API Service - Redis Dependency**
- **Location**: Lines 58-59 (in backend-api depends_on section)
- **Removed Dependency**:
```yaml
depends_on:
  postgres-clinic:
    condition: service_healthy
  redis:                    # REMOVED
    condition: service_healthy
```

**1.6. Cal.com Service - Redis Dependency**
- **Location**: Lines 141-142 (in calcom depends_on section)
- **Removed Dependency**:
```yaml
depends_on:
  postgres-cal:
    condition: service_healthy
  redis-cal:                # REMOVED
    condition: service_healthy
```

### 2. Documentation Updates

#### 2.1. README.md

**2.1.1. Architecture Diagram**
- **Location**: Line 68
- **Change**: Removed Redis from external services diagram
- **Before**:
```
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  PostgreSQL  │  │   Cal.com    │  │    Redis     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
```
- **After**:
```
│  ┌──────────────┐  ┌──────────────┐                        │
│  │  PostgreSQL  │  │   Cal.com    │                        │
│  └──────────────┘  └──────────────┘                        │
```

**2.1.2. Technology Stack - Infrastructure Section**
- **Location**: Line 105
- **Change**: Removed "Redis: Caching and session management" from infrastructure list
- **Before**:
```markdown
- **Redis**: Caching and session management
```
- **After**: (removed entirely)

**2.1.3. Service Status List**
- **Location**: Lines 197-198
- **Change**: Removed Redis services from expected service list
- **Before**:
```markdown
- `redis` - Cache for backend
- `redis-cal` - Cache for Cal.com
```
- **After**: (removed entirely)

**2.1.4. Configuration Section - Cal.com Redis Configuration**
- **Location**: Lines 249-250
- **Change**: Removed Redis URL configuration from Cal.com configuration example
- **Before**:
```bash
# Cal.com Redis
REDIS_URL=redis://redis-cal:6379
```
- **After**: (removed entirely)

#### 2.2. Architecture Documentation (`docs/architecture/hexagonal-architecture.md`)

**2.2.1. Architecture Diagram**
- **Location**: Line 102
- **Change**: Removed Redis from external systems diagram
- **Before**:
```
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │  PostgreSQL  │  │   Cal.com    │  │    Redis     │             │
│  │   Database   │  │     API      │  │    Cache     │             │
│  └──────────────┘  └──────────────┘  └──────────────┘             │
```
- **After**:
```
│  ┌──────────────┐  ┌──────────────┐                               │
│  │  PostgreSQL  │  │   Cal.com    │                               │
│  │   Database   │  │     API      │                               │
│  └──────────────┘  └──────────────┘                               │
```

## Verification

### Code Analysis
- ✅ No Redis dependencies found in `apps/backend/build.gradle`
- ✅ No Redis configuration classes found in backend codebase
- ✅ No `RedisTemplate` usage found in backend code
- ✅ No `@Cacheable` annotations found in backend code
- ✅ No Redis dependencies in `apps/landing/package.json`

### Impact Assessment
- ✅ **Backend**: No functional impact - Redis was not actively used in code
- ⚠️ **Cal.com**: Cal.com may require Redis for certain features. Monitor Cal.com logs for any Redis-related errors. If issues occur, Redis may need to be re-added for Cal.com only.

## How to Revert These Changes

If Redis needs to be re-added in the future, follow these steps:

### Step 1: Restore Docker Compose Services

Add back the Redis services to `docker-compose.yml`:

1. Add the backend Redis service after the `postgres-clinic` service (around line 22):
```yaml
  # Redis for caching
  redis:
    image: redis:7-alpine
    container_name: citamedica-redis
    ports:
      - "6379:6379"
    networks:
      - citamedica-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
```

2. Add the Cal.com Redis service after the `postgres-cal` service (around line 108):
```yaml
  # Redis for Cal.com
  redis-cal:
    image: redis:7-alpine
    container_name: calcom-redis
    ports:
      - "6380:6379"
    networks:
      - citamedica-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
```

### Step 2: Restore Environment Variables

1. In `backend-api` service, add:
```yaml
      REDIS_HOST: redis
      REDIS_PORT: 6379
```

2. In `calcom` service, add:
```yaml
      REDIS_URL: redis://redis-cal:6379
```

### Step 3: Restore Dependencies

1. In `backend-api` service `depends_on` section, add:
```yaml
    depends_on:
      postgres-clinic:
        condition: service_healthy
      redis:
        condition: service_healthy
```

2. In `calcom` service `depends_on` section, add:
```yaml
    depends_on:
      postgres-cal:
        condition: service_healthy
      redis-cal:
        condition: service_healthy
```

### Step 4: Restore Documentation

1. Update `README.md`:
   - Restore Redis in architecture diagram (line 68)
   - Add "Redis: Caching and session management" to infrastructure section (line 105)
   - Add Redis services to service status list (lines 197-198)
   - Add Redis URL configuration to Cal.com configuration section (lines 249-250)

2. Update `docs/architecture/hexagonal-architecture.md`:
   - Restore Redis in architecture diagram (line 102)

### Step 5: Add Redis Dependencies (if needed for backend)

If you plan to use Redis in the Spring Boot backend, add to `apps/backend/build.gradle`:
```gradle
dependencies {
    // ... existing dependencies ...
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
}
```

### Step 6: Add Redis Configuration (if needed for backend)

If you plan to use Redis in the Spring Boot backend, create a configuration class:
```java
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.redis.port:6379}")
    private int redisPort;
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        return factory;
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

And add to `application.yml`:
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
```

## Notes

- Cal.com may have Redis as an optional dependency. Monitor Cal.com functionality after removal to ensure no features are broken.
- If Cal.com requires Redis, consider re-adding only the `redis-cal` service and leaving backend Redis removed.
- The backend application had no Redis integration code, so removing it had zero functional impact on the Spring Boot application.
- Port mappings:
  - Backend Redis was on port `6379:6379`
  - Cal.com Redis was on port `6380:6379`

## Future Considerations

If Redis is re-added in the future:
1. Consider whether both services are needed or if Cal.com can share the backend Redis instance
2. Evaluate if caching is actually needed - the backend performed well without it
3. Consider using Redis for session management if needed for horizontal scaling
4. Consider using Redis for rate limiting if API protection is needed

