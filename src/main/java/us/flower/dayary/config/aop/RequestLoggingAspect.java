package us.flower.dayary.config.aop;

import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.Joiner;

@Component // 1
@Aspect // 2
public class RequestLoggingAspect {
  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingAspect.class);

  private String paramMapToString(Map<String, String[]> paramMap) {
    return paramMap.entrySet().stream()
        .map(entry -> String.format("%s -> (%s)",
            entry.getKey(), Joiner.on(",").join(entry.getValue())))
        .collect(Collectors.joining(", "));
  }

  @Pointcut("within(us.flower.dayary.controller..*)") // 3
  public void onRequest() {}

  @Around("us.flower.dayary.config.aop.RequestLoggingAspect.onRequest()") // 4
  public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
    HttpServletRequest request = // 5
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    Map<String, String[]> paramMap = request.getParameterMap();
    String params = "";
    if (paramMap.isEmpty() == false) {
      params = " [" + paramMapToString(paramMap) + "]";
    }

    long start = System.currentTimeMillis();
    try {
    	System.out.println("AOP테스트");
      return pjp.proceed(pjp.getArgs()); // 6
    } finally {
      long end = System.currentTimeMillis();
      logger.debug("Request: {} {}{} < {} ({}ms)", request.getMethod(), request.getRequestURI(),
          params, request.getRemoteHost(), end - start);
    }
  }
}