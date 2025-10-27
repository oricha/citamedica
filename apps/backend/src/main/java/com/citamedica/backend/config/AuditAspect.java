package com.citamedica.backend.config;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Pointcut("execution(* com.citamedica.backend.service.*Service.create*(..)) || " +
              "execution(* com.citamedica.backend.service.*Service.update*(..)) || " +
              "execution(* com.citamedica.backend.service.*Service.delete*(..))")
    public void serviceMethods() {}

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logServiceAction(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String entity = className.replace("Service", "");
            String action = determineAction(methodName);
            String actor = getActorFromSecurityContext();
            
            // Extract entity ID and metadata
            Long entityId = extractEntityId(result);
            String metadata = buildMetadata(joinPoint, result);

            if (entityId != null) {
                auditService.logAction(actor, action, entity, entityId, metadata);
                log.debug("Audit log created: actor={}, action={}, entity={}, entityId={}",
                    actor, action, entity, entityId);
            }
        } catch (Exception e) {
            log.error("Error creating audit log", e);
        }
    }

    private String determineAction(String methodName) {
        if (methodName.startsWith("create")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        return "UNKNOWN";
    }

    private String getActorFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Could not get actor from security context", e);
        }
        return "system";
    }

    private Long extractEntityId(Object result) {
        if (result == null) return null;
        
        try {
            // Handle common entity types
            if (result instanceof Patient) {
                return ((Patient) result).getId();
            } else if (result instanceof Appointment) {
                return ((Appointment) result).getId();
            }
            // Try reflection for getId method
            var method = result.getClass().getMethod("getId");
            Object id = method.invoke(result);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (Exception e) {
            log.debug("Could not extract entity ID from result", e);
        }
        return null;
    }

    private String buildMetadata(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("method", joinPoint.getSignature().getName());
            metadata.put("class", joinPoint.getTarget().getClass().getSimpleName());
            
            // Add parameter info
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Map<String, Object> params = new HashMap<>();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {
                        params.put("arg" + i, args[i].getClass().getSimpleName());
                    }
                }
                metadata.put("parameters", params);
            }
            
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.debug("Could not build metadata", e);
            return null;
        }
    }
}