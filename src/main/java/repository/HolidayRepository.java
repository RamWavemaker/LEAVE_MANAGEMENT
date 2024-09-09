package repository;

import models.Holidays;

import java.util.List;

public interface HolidayRepository {
    List<String> getHolidaysDates();
    List<Holidays> getHolidays();
}
