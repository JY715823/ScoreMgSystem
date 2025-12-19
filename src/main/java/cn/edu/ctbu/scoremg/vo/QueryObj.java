package cn.edu.ctbu.scoremg.vo;

import lombok.Data;

@Data
public class QueryObj <T>{
    private Integer page;
    private Integer limit;
    private T data;
}
