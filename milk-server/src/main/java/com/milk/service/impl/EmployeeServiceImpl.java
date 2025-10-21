package com.milk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.milk.constant.MessageConstant;
import com.milk.constant.PasswordConstant;
import com.milk.constant.StatusConstant;
import com.milk.context.BaseContext;
import com.milk.dto.EmployeeDTO;
import com.milk.dto.EmployeeLoginDTO;
import com.milk.dto.EmployeePageQueryDTO;
import com.milk.dto.PasswordEditDTO;
import com.milk.entity.Employee;
import com.milk.exception.AccountExistException;
import com.milk.exception.AccountLockedException;
import com.milk.exception.AccountNotFoundException;
import com.milk.exception.PasswordErrorException;
import com.milk.mapper.EmployeeMapper;
import com.milk.result.PageResult;
import com.milk.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_ACCOUNT_PWD_ERROR);
        }

        //密码比对
        //进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.ACCOUNT_ACCOUNT_PWD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee byUsername = employeeMapper.getByUsername(employeeDTO.getUsername());
        if(byUsername!=null){
            throw new AccountExistException(MessageConstant.ACCOUNT_EXIST);
        }
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(StatusConstant.DISABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void openOrStop(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        employeeMapper.update(employee);
    }

    @Override
    public Employee selectEmployeeById(Long id) {
        Employee employee=employeeMapper.selectEmployeeById(id);
        employee.setPassword("********");
        return employee;
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Employee employee = employeeMapper.selectEmployeeById(BaseContext.getCurrentId());
        //处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String oldPassword = passwordEditDTO.getOldPassword();
        //密码比对
        //进行md5加密
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if(!oldPassword.equals(employee.getPassword())){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
        employeeMapper.update(employee);

    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeMapper.selectEmployeeById(id);
        if(employee==null){
            throw new AccountNotFoundException("员工不存在");
        }
        if(employee.getStatus()==StatusConstant.ENABLE){
            throw new AccountLockedException("启用状态的员工不能删除");
        }
        employeeMapper.deleteById(id);
    }

}
