package com.ll.medium.global.exceptionHandlers;

import com.ll.medium.global.exceptions.GlobalException;
import com.ll.medium.global.rq.Rq.Rq;
import com.ll.medium.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Rq rq;

    @ExceptionHandler(GlobalException.class)
    public String handleException(GlobalException e) {
        return rq.historyBack(e.getRsData());
    }
}
