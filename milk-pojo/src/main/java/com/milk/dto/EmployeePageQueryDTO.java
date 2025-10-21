package com.milk.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;
    private String phone;
    private Integer status;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
