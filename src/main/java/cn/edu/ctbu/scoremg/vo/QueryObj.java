package cn.edu.ctbu.scoremg.vo;

import lombok.Data;

@Data
public class QueryObj <T>{
    private Integer page;
    private Integer limit;
    private T data;

    // === 新增以下两个字段用于排序 ===
    private String sortField; // 排序字段（如 id, age）
    private String sortOrder; // 排序方式（asc, desc）

    // === 【核心修复】添加这个缺失的字段 ===
    private String keyword;
}