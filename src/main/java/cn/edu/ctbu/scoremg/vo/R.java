package cn.edu.ctbu.scoremg.vo;
import lombok.Data;

/**
 * 通用返回对象
 * @param <T>
 */
@Data
public class R<T> {

    /**错误代码*/
    private Integer code;
    /**提示信息*/
    private String msg;

    /**计数**/
    private Long count;

    /***具体的内容*/
    private T data;

}