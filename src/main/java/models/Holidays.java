package models;

public class Holidays {
    int Id;
    String HolidayDate;
    String Occasion;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getHolidayDate() {
        return HolidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        HolidayDate = holidayDate;
    }

    public String getOccasion() {
        return Occasion;
    }

    public void setOccasion(String occasion) {
        Occasion = occasion;
    }
}
