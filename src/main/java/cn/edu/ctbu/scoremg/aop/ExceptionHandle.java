package cn.edu.ctbu.scoremg.aop;

import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理, 使用ControllerAdvice(增强控制器)
 */
@RestControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    public R handle(Exception e) {
        if (e instanceof RException) {
            RException rException = (RException) e;
            return RUtil.error(rException.getCode(), rException.getMessage());
        }
        return RUtil.error(-999, e.getMessage());
    }
}