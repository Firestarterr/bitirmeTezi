package org.firestarterr.bitirmeTezi.model;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class BaseEntity implements Serializable {

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

    public long getAge() {
        long diff = updatedDate.getTime() - createdDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public long getRelAge() {
        if (relCreatedDate != null) {
            long diff = relUpdatedDate.getTime() - relCreatedDate.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } else {
            return 0L;
        }
    }


    @Override
    public String toString() {
        return getName();
    }
}
