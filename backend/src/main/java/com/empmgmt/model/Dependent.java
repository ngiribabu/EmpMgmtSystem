package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "DEPENDENTS", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dependent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPID")
    private Integer depId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @NotBlank
    @Size(max = 80)
    @Column(name = "DEPNAME", nullable = false, length = 80)
    private String depName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "RELATION", nullable = false, length = 20)
    private String relation;

    @Column(name = "DOB")
    private Date dob;

    @Size(max = 1)
    @Column(name = "GENDER", length = 1)
    private String gender;

    public Integer getDepId() { return depId; }
    public void setDepId(Integer depId) { this.depId = depId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public String getDepName() { return depName; }
    public void setDepName(String depName) { this.depName = depName; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
