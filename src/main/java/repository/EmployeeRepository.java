package repository;

import models.Employees;

import java.util.List;

public interface EmployeeRepository {
    List<Integer> getEmpUnderManager(int managerId);
    List<Employees> getEmployeedetails(int loginid);
}
