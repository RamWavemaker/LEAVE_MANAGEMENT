package services.impl;

import models.Employees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.EmployeeRepository;
import repository.impl.EmployeeRepositoryImpl;
import services.EmployeeService;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    EmployeeRepository employeeRepository;
    public EmployeeServiceImpl(){
        employeeRepository = new EmployeeRepositoryImpl();
    }
    @Override
    public List<Integer> getEmpUnderManager(int managerId) {
        return employeeRepository.getEmpUnderManager(managerId);
    }

    @Override
    public List<Employees> getEmployeedetails(int loginid) {
        return employeeRepository.getEmployeedetails(loginid);
    }
}
