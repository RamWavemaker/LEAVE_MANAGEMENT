package services.impl;

import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LeaveRepository;
import repository.impl.LeaveRepositoryImpl;
import services.LeaveService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class LeaveServiceImpl implements LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);
    LeaveRepository leaveRepository;

    public LeaveServiceImpl(){
        leaveRepository = new LeaveRepositoryImpl();
    }
    @Override
    public int addLeaves(Date fromDate, Date toDate, Date AppliedDate, int employeeId, int loginId, String leaveType, String leaveComment) {
        return leaveRepository.addLeaves(fromDate, toDate, AppliedDate, employeeId, loginId, leaveType, leaveComment);
    }

    @Override
    public List<Leaves> getLeaves(int userid) {
        return leaveRepository.getLeaves(userid);
    }

    @Override
    public boolean statusChange(Leaves.Status newStatus, int leaveId) {
        return leaveRepository.statusChange(newStatus, leaveId);
    }

    @Override
    public List<Leaves> getLeavesByStatus(Leaves.Status status, int userid) {
        return leaveRepository.getLeavesByStatus(status, userid);
    }

    @Override
    public List<Integer> getIdOfLeavesByTypeAndUser(Leaves.LeaveType leaveType, int userid) {
        return leaveRepository.getIdOfLeavesByTypeAndUser(leaveType, userid);
    }

    @Override
    public int getNoOfDaysBetweenDates(List<Integer> leaveIds, List<LocalDate> holidays) {
        return leaveRepository.getNoOfDaysBetweenDates(leaveIds, holidays);
    }

    @Override
    public List<Leaves> getLeavesOfEmployees(List<Integer> empIds) {
        return leaveRepository.getLeavesOfEmployees(empIds);
    }
}
