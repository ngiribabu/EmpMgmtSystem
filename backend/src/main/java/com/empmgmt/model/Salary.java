package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "SALARIES", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Salary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALARYID")
    private Integer salaryId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @NotNull
    @Column(name = "BASESALARY", nullable = false, precision = 11, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "BONUS", precision = 11, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Size(max = 3)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private String currency = "USD";

    @Size(max = 10)
    @Column(name = "PAYFREQ", nullable = false, length = 10)
    private String payFreq = "ANNUAL";

    @NotNull
    @Column(name = "EFFDATE", nullable = false)
    private Date effDate;

    @Column(name = "ENDDATE")
    private Date endDate;

    @Column(name = "ISCURRENT", length = 1)
    private String isCurrent = "Y";

    @Size(max = 50)
    @Column(name = "REASON", length = 50)
    private String reason;

    @Size(max = 300)
    @Column(name = "NOTES", length = 300)
    private String notes;

    public Integer getSalaryId() { return salaryId; }
    public void setSalaryId(Integer salaryId) { this.salaryId = salaryId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPayFreq() { return payFreq; }
    public void setPayFreq(String payFreq) { this.payFreq = payFreq; }

    public Date getEffDate() { return effDate; }
    public void setEffDate(Date effDate) { this.effDate = effDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getIsCurrent() { return isCurrent; }
    public void setIsCurrent(String isCurrent) { this.isCurrent = isCurrent; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
