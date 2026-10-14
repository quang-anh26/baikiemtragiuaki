package com.example.dish.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView notFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("messageKey", ex.getMessageKey());
        mav.addObject("messageArg", ex.getArg());
        return mav;
    }

    /** Id/tham so sai dinh dang (vd /dishes/abc) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView badRequest(MethodArgumentTypeMismatchException ex) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("messageKey", "error.badrequest");
        mav.addObject("messageArg", ex.getValue());
        return mav;
    }
}
