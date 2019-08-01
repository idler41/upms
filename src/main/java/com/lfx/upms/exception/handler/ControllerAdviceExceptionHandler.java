package com.lfx.upms.exception.handler;

import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.result.ResultBuilder;
import com.lfx.upms.result.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdviceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public WebResult<String> unknown(Exception e) {
        log.error("未知异常: ", e);
        return ResultBuilder.buildFailed(ResultEnum.UNKNOWN_ERROR);
    }

    @ExceptionHandler(AuthorizationException.class)
    public WebResult<String> authorization(AuthorizationException ex, HttpServletRequest request) {
        if (log.isWarnEnabled()) {
            log.warn("权限异常警告,客户端: [{}],请求URL: [{}],异常信息: [{}]",
                    request.getRemoteHost(), request.getRequestURI(), ex);
        }
        return ResultBuilder.buildFailed(ResultEnum.NO_PERMISSION);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        if (logger.isDebugEnabled()) {
            logger.debug(objectError);
        }
        StringBuilder errorMessage = resolveErrorMessage(objectError);
        return handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        if (logger.isDebugEnabled()) {
            logger.debug(objectError);
        }
        StringBuilder errorMessage = resolveErrorMessage(objectError);
        return handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

    private StringBuilder resolveErrorMessage(ObjectError objectError) {
        StringBuilder errorMessage = new StringBuilder();

        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            errorMessage.append("参数'").append(fieldError.getField()).append("': ");
        }

        errorMessage.append(objectError.getDefaultMessage());
        return errorMessage;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        if (logger.isDebugEnabled()) {
            logger.debug("standard Spring MVC exceptions: ", ex);
        }
        return new ResponseEntity<>(
                ResultBuilder.buildFailed(status.value(), body == null ? ex.getMessage() : body.toString()),
                headers, status
        );
    }

}
