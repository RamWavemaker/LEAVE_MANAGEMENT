package services.impl;

import models.Holidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.HolidayRepository;
import repository.impl.HolidayRepositoryImpl;
import services.HolidayService;
import java.util.List;

public class HolidayServiceImpl implements HolidayService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayServiceImpl.class);
    HolidayRepository holidayRepository;

    public HolidayServiceImpl(){
        holidayRepository = new HolidayRepositoryImpl();
    }

    @Override
    public List<String> getHolidaysDates() {
        return holidayRepository.getHolidaysDates();
    }

    @Override
    public List<Holidays> getHolidays() {
        return holidayRepository.getHolidays();
    }
}
