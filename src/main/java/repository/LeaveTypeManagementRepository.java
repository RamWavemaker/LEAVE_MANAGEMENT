package repository;

import models.Leaves;

import java.util.List;
import java.util.Map;

public interface LeaveTypeManagementRepository {
     List<Map.Entry<Leaves.LeaveType, Integer>> getLimitedLeavesByType();
}
