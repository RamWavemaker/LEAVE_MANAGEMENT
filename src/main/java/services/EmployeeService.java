package services;

import models.Employees;

import java.util.List;

public interface EmployeeService {
    List<Integer> getEmpUnderManager(int managerId);
    List<Employees> getEmployeedetails(int loginid);
}
