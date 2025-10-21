package com.milk.controller.admin;

import com.milk.constant.JwtClaimsConstant;
import com.milk.dto.EmployeeDTO;
import com.milk.dto.EmployeeLoginDTO;
import com.milk.dto.EmployeePageQueryDTO;
import com.milk.dto.PasswordEditDTO;
import com.milk.entity.Employee;
import com.milk.properties.JwtProperties;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.EmployeeService;
import com.milk.utils.JwtUtil;
import com.milk.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "添加员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("添加员工 {}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "退出")
    public Result<String> logout() {
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询员工")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工 {}",employeePageQueryDTO);
        PageResult pageResult=employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用员工
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用/禁用员工")
    public Result openOrStop(@PathVariable Integer status,Long id){
        log.info("启用/禁用员工 {} {}",status,id);
        employeeService.openOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询员工")
    public Result<Employee> selectEmployeeById(@PathVariable Long id){
        log.info("查询员工id:{}",id);
        Employee employee = employeeService.selectEmployeeById(id);
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation(value = "修改员工信息")
    public Result updateEmployee( @RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息 {}",employeeDTO);
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除员工信息")
    public Result deleteEmployee( @PathVariable Long id){
        log.info("删除员工信息 {}",id);
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    @PutMapping("/editPassword")
    @ApiOperation(value = "修改员工密码")
    public Result editPassword(@RequestBody  PasswordEditDTO passwordEditDTO){
        log.info("修改员工密码 {}",passwordEditDTO);
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}
