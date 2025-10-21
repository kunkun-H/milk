package com.milk.service;

import com.milk.dto.EmployeeDTO;
import com.milk.dto.EmployeeLoginDTO;
import com.milk.dto.EmployeePageQueryDTO;
import com.milk.dto.PasswordEditDTO;
import com.milk.entity.Employee;
import com.milk.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    void save(EmployeeDTO employeeDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    void openOrStop(Integer status, Long id);

    void updateEmployee(EmployeeDTO employeeDTO);

    Employee selectEmployeeById(Long id);

    void editPassword(PasswordEditDTO passwordEditDTO);

    void deleteEmployee(Long id);
}
