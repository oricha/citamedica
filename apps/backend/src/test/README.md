# Backend Tests - CitaMedica

This directory contains unit and integration tests for the CitaMedica backend application.

## Test Structure

```
test/
├── java/com/citamedica/backend/
│   ├── BackendApplicationTests.java          # Context loading test
│   ├── service/
│   │   ├── AppointmentServiceTest.java       # Unit tests for AppointmentService
│   │   └── SeedDataServiceTest.java          # Tests for seed data service
│   ├── integration/calcom/
│   │   ├── CalcomSignatureValidatorTest.java # Unit tests for webhook signature validation
│   │   └── CalcomWebhookHandlerTest.java     # Unit tests for webhook event handling
│   ├── domain/repository/
│   │   └── AppointmentRepositoryIntegrationTest.java # Integration tests for JPA repository
│   └── api/v1/
│       └── AppointmentControllerIntegrationTest.java # Integration tests for REST endpoints
└── resources/
    └── application-test.yml                   # Test configuration
```

## Running Tests

### Run all tests
```bash
./gradlew test
```

### Run specific test class
```bash
./gradlew test --tests AppointmentServiceTest
```

### Run tests with coverage
```bash
./gradlew test jacocoTestReport
```

### Run only unit tests
```bash
./gradlew test --tests '*Test'
```

### Run only integration tests
```bash
./gradlew test --tests '*IntegrationTest'
```

## Test Categories

### Unit Tests
- **AppointmentServiceTest**: Tests business logic for appointment management
- **CalcomSignatureValidatorTest**: Tests HMAC SHA256 signature verification
- **CalcomWebhookHandlerTest**: Tests webhook event processing logic
- **SeedDataServiceTest**: Tests seed data generation

### Integration Tests
- **AppointmentRepositoryIntegrationTest**: Tests JPA repository methods with H2 database
- **AppointmentControllerIntegrationTest**: Tests REST endpoints with MockMvc and Spring Security

## Test Configuration

Tests use:
- **H2 Database**: In-memory database for integration tests
- **Spring Boot Test**: For application context and dependency injection
- **Mockito**: For mocking dependencies in unit tests
- **MockMvc**: For testing REST endpoints
- **JUnit 5**: Test framework

## Test Coverage Goals

- Minimum 70% code coverage
- All critical business logic covered
- All REST endpoints tested
- All repository methods tested
- Webhook signature validation thoroughly tested

## Writing New Tests

### Unit Test Example
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void testMethod() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        
        // Act
        Result result = service.doSomething(1L);
        
        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
    }
}
```

### Integration Test Example
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MyControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "USER")
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/resource"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.field").value("value"));
    }
}
```

## Best Practices

1. **Follow AAA Pattern**: Arrange, Act, Assert
2. **Use Descriptive Names**: Test names should describe what they test
3. **Test One Thing**: Each test should verify one behavior
4. **Use @BeforeEach**: Set up common test data
5. **Mock External Dependencies**: Don't call real external services
6. **Use @Transactional**: Rollback database changes after each test
7. **Test Edge Cases**: Include null checks, empty lists, etc.
8. **Verify Interactions**: Use `verify()` to check mock interactions

## Troubleshooting

### Tests failing with database errors
- Check that H2 is in test dependencies
- Verify `application-test.yml` configuration
- Ensure `@Transactional` is used for integration tests

### Mockito errors
- Verify `@ExtendWith(MockitoExtension.class)` is present
- Check that mocks are properly initialized with `@Mock`
- Ensure `@InjectMocks` is used on the class under test

### Spring context not loading
- Check `@SpringBootTest` annotation
- Verify all required beans are available
- Check for circular dependencies

## CI/CD Integration

Tests run automatically on:
- Pull requests
- Commits to main branch
- Before deployment

Test reports are generated in:
- `build/reports/tests/test/index.html`
- `build/reports/jacoco/test/html/index.html` (coverage)

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)