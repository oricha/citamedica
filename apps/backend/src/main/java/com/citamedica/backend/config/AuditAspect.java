package com.citamedica.backend.config;

import com.citamedica.backend.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Pointcut("execution(* com.citamedica.backend.service.*Service.create*(..)) || " +
              "execution(* com.citamedica.backend.service.*Service.update*(..)) || " +
              "execution(* com.citamedica.backend.service.*Service.delete*(..))")
    public void serviceMethods() {}

    @After("serviceMethods()")
    public void logServiceAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String entity = className.replace("Service", "").toLowerCase();
        String action = methodName.startsWith("create") ? "CREATE" :
                        methodName.startsWith("update") ? "UPDATE" : "DELETE";
        String actor = "system"; // TODO: Get from security context
        Long entityId = null; // TODO: Extract from return value or parameters

        auditService.logAction(actor, action, entity, entityId, null);
    }
}