package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "ATTENDANCE", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDID")
    private Integer attendId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @NotNull
    @Column(name = "WORKDATE", nullable = false)
    private Date workDate;

    @Column(name = "CLOCKIN")
    private Time clockIn;

    @Column(name = "CLOCKOUT")
    private Time clockOut;

    @Column(name = "HRSWORKED", precision = 5, scale = 2)
    private BigDecimal hrsWorked;

    @Column(name = "OTHRS", precision = 5, scale = 2)
    private BigDecimal otHrs = BigDecimal.ZERO;

    @Size(max = 10)
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status = "PRESENT";

    @Size(max = 200)
    @Column(name = "NOTES", length = 200)
    private String notes;

    public Integer getAttendId() { return attendId; }
    public void setAttendId(Integer attendId) { this.attendId = attendId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }

    public Time getClockIn() { return clockIn; }
    public void setClockIn(Time clockIn) { this.clockIn = clockIn; }

    public Time getClockOut() { return clockOut; }
    public void setClockOut(Time clockOut) { this.clockOut = clockOut; }

    public BigDecimal getHrsWorked() { return hrsWorked; }
    public void setHrsWorked(BigDecimal hrsWorked) { this.hrsWorked = hrsWorked; }

    public BigDecimal getOtHrs() { return otHrs; }
    public void setOtHrs(BigDecimal otHrs) { this.otHrs = otHrs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
