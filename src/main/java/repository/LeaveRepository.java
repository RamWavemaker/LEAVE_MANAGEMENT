package repository;

import models.Leaves;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository {
    int addLeaves(Date fromDate, Date toDate, Date AppliedDate, int employeeId, int loginId, String leaveType, String leaveComment);
    List<Leaves> getLeaves(int userid);
    boolean statusChange(Leaves.Status newStatus, int leaveId);
    List<Leaves> getLeavesByStatus(Leaves.Status status,int userid);
    List<Integer> getIdOfLeavesByTypeAndUser(Leaves.LeaveType leaveType, int userid);
    int getNoOfDaysBetweenDates(List<Integer> leaveIds, List<LocalDate> holidays);
    public List<Leaves> getLeavesOfEmployees(List<Integer> empIds);
}
