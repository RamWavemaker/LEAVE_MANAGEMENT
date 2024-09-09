package services;

import models.Leaves;

import java.util.List;
import java.util.Map;

public interface LeaveTypeManagementService {
    List<Map.Entry<Leaves.LeaveType, Integer>> getLimitedLeavesByType();
}
