package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "EMPHIST", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EmpHist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTID")
    private Integer histId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @NotNull
    @Column(name = "EFFDATE", nullable = false)
    private Date effDate;

    @Column(name = "ENDDATE")
    private Date endDate;

    @NotBlank
    @Size(max = 15)
    @Column(name = "CHANGETYPE", nullable = false, length = 15)
    private String changeType;

    @Column(name = "OLDDEPTID")
    private Integer oldDeptId;

    @Column(name = "NEWDEPTID")
    private Integer newDeptId;

    @Column(name = "OLDPOSID")
    private Integer oldPosId;

    @Column(name = "NEWPOSID")
    private Integer newPosId;

    @Column(name = "OLDSALARY", precision = 11, scale = 2)
    private BigDecimal oldSalary;

    @Column(name = "NEWSALARY", precision = 11, scale = 2)
    private BigDecimal newSalary;

    @Size(max = 300)
    @Column(name = "NOTES", length = 300)
    private String notes;

    public Integer getHistId() { return histId; }
    public void setHistId(Integer histId) { this.histId = histId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public Date getEffDate() { return effDate; }
    public void setEffDate(Date effDate) { this.effDate = effDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public Integer getOldDeptId() { return oldDeptId; }
    public void setOldDeptId(Integer oldDeptId) { this.oldDeptId = oldDeptId; }

    public Integer getNewDeptId() { return newDeptId; }
    public void setNewDeptId(Integer newDeptId) { this.newDeptId = newDeptId; }

    public Integer getOldPosId() { return oldPosId; }
    public void setOldPosId(Integer oldPosId) { this.oldPosId = oldPosId; }

    public Integer getNewPosId() { return newPosId; }
    public void setNewPosId(Integer newPosId) { this.newPosId = newPosId; }

    public BigDecimal getOldSalary() { return oldSalary; }
    public void setOldSalary(BigDecimal oldSalary) { this.oldSalary = oldSalary; }

    public BigDecimal getNewSalary() { return newSalary; }
    public void setNewSalary(BigDecimal newSalary) { this.newSalary = newSalary; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
