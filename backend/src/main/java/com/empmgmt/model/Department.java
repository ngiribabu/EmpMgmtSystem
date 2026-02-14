package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "DEPARTMENTS", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPTID")
    private Integer deptId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "DEPTNAME", nullable = false, length = 50)
    private String deptName;

    @Size(max = 200)
    @Column(name = "DEPTDESC", length = 200)
    private String deptDesc;

    @Size(max = 100)
    @Column(name = "LOCATION", length = 100)
    private String location;

    @Column(name = "MANAGERID")
    private Integer managerId;

    @Column(name = "ISACTIVE", length = 1)
    private String isActive = "Y";

    // Getters and Setters
    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getDeptDesc() { return deptDesc; }
    public void setDeptDesc(String deptDesc) { this.deptDesc = deptDesc; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}
