# Security Documentation

## CSRF (Cross-Site Request Forgery) Protection

### Current Configuration
CSRF protection is **disabled** in this application.
```java
.csrf(csrf -> csrf.disable())
```

### Justification
This is acceptable because:

1. **Stateless JWT Authentication**: The application uses JWT tokens for authentication, which are stateless and stored client-side (not in cookies).

2. **No Session Cookies**: CSRF attacks exploit session cookies sent automatically by browsers. Since this application doesn't use session cookies (stateless JWT), CSRF attacks are not applicable.

3. **Bearer Token Authentication**: JWT tokens must be explicitly included in the `Authorization: Bearer <token>` header for each request, which cannot be exploited via CSRF attacks.

### Security Measures in Place
Despite disabled CSRF, the application maintains security through:

- **JWT Token Validation**: Every request requires a valid, signed JWT token
- **Token Expiration**: Tokens expire after 24 hours
- **Stateless Sessions**: No server-side session storage
- **HTTPS Recommended**: All production deployments should use HTTPS
- **CORS Configuration**: Can be configured to restrict allowed origins

### When CSRF Would Be Required
CSRF protection should be enabled if:
- The application uses cookie-based session authentication
- Form-based authentication with session cookies is implemented
- The application moves away from stateless JWT architecture

### Reference
- [OWASP CSRF Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Spring Security CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
