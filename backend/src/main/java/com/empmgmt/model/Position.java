package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "POSITIONS", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Position extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSID")
    private Integer posId;

    @NotBlank
    @Size(max = 60)
    @Column(name = "POSTITLE", nullable = false, length = 60)
    private String posTitle;

    @Size(max = 200)
    @Column(name = "POSDESC", length = 200)
    private String posDesc;

    @NotNull
    @Column(name = "DEPTID", nullable = false)
    private Integer deptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPTID", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;

    @Column(name = "MINSALARY", precision = 11, scale = 2)
    private BigDecimal minSalary = BigDecimal.ZERO;

    @Column(name = "MAXSALARY", precision = 11, scale = 2)
    private BigDecimal maxSalary = BigDecimal.ZERO;

    @Column(name = "ISACTIVE", length = 1)
    private String isActive = "Y";

    public Integer getPosId() { return posId; }
    public void setPosId(Integer posId) { this.posId = posId; }

    public String getPosTitle() { return posTitle; }
    public void setPosTitle(String posTitle) { this.posTitle = posTitle; }

    public String getPosDesc() { return posDesc; }
    public void setPosDesc(String posDesc) { this.posDesc = posDesc; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }

    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}
