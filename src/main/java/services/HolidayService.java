package services;

import models.Holidays;

import java.util.List;

public interface HolidayService {
    List<String> getHolidaysDates();
    List<Holidays> getHolidays();
}
