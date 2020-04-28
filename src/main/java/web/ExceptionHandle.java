package web;

import com.perfma.model.common.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @auther echo
 * @create 2018-05-15 15:39
 */
@RestControllerAdvice
@RequestMapping("/error")
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    /**
     * 系统级别异常
     */
    @ExceptionHandler({Exception.class})
    public ResultInfo exceptionHandler(Exception ex) {
        logger.error("系统发生异常,异常信息为:", ex);
        return ResultInfo.buildFail("服务暂时不可用，正在恢复中，请稍等！");
    }
}
