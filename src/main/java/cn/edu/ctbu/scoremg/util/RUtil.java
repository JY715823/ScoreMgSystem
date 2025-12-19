package cn.edu.ctbu.scoremg.util;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.vo.R;

public class RUtil {

    public static R success(Object object,Long count) {
        R result = new R();
        result.setCode(0);
        result.setData(object);
        result.setCount(count);
        result.setMsg("成功");
        return result;
    }

    public static R success(Object object) {
        return success(object, null);
    }

    public static R success() {
        return success(null);
    }

    public static R error(Integer code, String msg) {
        R result = new R();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static R error(REnum rEnum) {
        R result = new R();
        result.setCode(rEnum.getCode());
        result.setMsg(rEnum.getMsg());
        return result;
    }
}