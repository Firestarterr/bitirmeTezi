package org.firestarterr.bitirmeTezi.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class BitBaseEntity implements Serializable {

    private String name;

    private Date createdDate;
    private Date updatedDate;

    private Date relCreatedDate;
    private Date relUpdatedDate;

    private Integer recordCount = 0;
    private Integer relRecordCount = 0;

    /**
     * updatedDate - createdDate varsayalım ki bize 5 gün çıktı.
     * arada 5 record count var.
     * changeFrequencyPerDay = change per day iken
     * freq = 1;
     */
    private Double changeFrequencyPerDay;
    private Double relChangeFrequencyPerDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getRelCreatedDate() {
        return relCreatedDate;
    }

    public void setRelCreatedDate(Date relCreatedDate) {
        this.relCreatedDate = relCreatedDate;
    }

    public Date getRelUpdatedDate() {
        return relUpdatedDate;
    }

    public void setRelUpdatedDate(Date relUpdatedDate) {
        this.relUpdatedDate = relUpdatedDate;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public Integer getRelRecordCount() {
        return relRecordCount;
    }

    public void setRelRecordCount(Integer relRecordCount) {
        this.relRecordCount = relRecordCount;
    }

    public Double getChangeFrequencyPerDay() {
        return changeFrequencyPerDay;
    }

    public void setChangeFrequencyPerDay(Double changeFrequencyPerDay) {
        this.changeFrequencyPerDay = changeFrequencyPerDay;
    }

    public Double getRelChangeFrequencyPerDay() {
        return relChangeFrequencyPerDay;
    }

    public void setRelChangeFrequencyPerDay(Double relChangeFrequencyPerDay) {
        this.relChangeFrequencyPerDay = relChangeFrequencyPerDay;
    }

    public void increaseRecordCount() {
        recordCount++;
    }

    public void increaseRelRecordCount() {
        relRecordCount++;
    }

    public Integer getAge() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(Math.abs(createdDate.getTime() - updatedDate.getTime()));
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public Integer getRelAge() {
        if (relCreatedDate != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(Math.abs(relCreatedDate.getTime() - relUpdatedDate.getTime()));
            return cal.get(Calendar.DAY_OF_YEAR);
        } else {
            return 0;
        }
    }


    @Override
    public String toString() {
        return getName();
    }
}
