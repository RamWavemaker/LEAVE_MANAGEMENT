package services.impl;

import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LeaveTypeManagementRepository;
import repository.impl.LeaveTypeManagementRepositoryImpl;
import services.LeaveTypeManagementService;
import java.util.List;
import java.util.Map;

public class LeaveTypeManagementServiceImpl implements LeaveTypeManagementService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeManagementRepository.class);
    LeaveTypeManagementRepository leaveTypeManagementRepository;
    public LeaveTypeManagementServiceImpl(){
        leaveTypeManagementRepository = new LeaveTypeManagementRepositoryImpl();
    }
    @Override
    public List<Map.Entry<Leaves.LeaveType, Integer>> getLimitedLeavesByType() {
        return leaveTypeManagementRepository.getLimitedLeavesByType();
    }
}
