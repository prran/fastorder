package com.kindlesstory.aop;

import org.aspectj.lang.annotation.Before;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.apache.logging.log4j.LogManager;
import com.kindlesstory.www.module.UserLog;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class FlowLogger
{
    final Logger logger;
    
    public FlowLogger() {
        logger = LogManager.getLogger(UserLog.class);
    }
    
    @Before("execution(* service..*Service.*(..))")
    public void printServiceLog(final JoinPoint jp) {
        logger.info("--------FlowLogger--------");
        logger.info("param : " + Arrays.toString(jp.getArgs()));
        logger.info("action : " + jp.getKind());
        logger.info("target : " + jp.getTarget().toString());
        logger.info("method : " + jp.getSignature().getName());
        logger.info("executor" + jp.getThis().toString());
        logger.info("--------------------------");
    }
}