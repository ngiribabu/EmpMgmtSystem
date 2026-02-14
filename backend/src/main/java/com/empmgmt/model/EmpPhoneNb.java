package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "EMPPHONENB", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EmpPhoneNb extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PHONEID")
    private Integer phoneId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @Size(max = 10)
    @Column(name = "PHONETYPE", nullable = false, length = 10)
    private String phoneType = "MOBILE";

    @NotBlank
    @Size(max = 20)
    @Column(name = "PHONENUM", nullable = false, length = 20)
    private String phoneNum;

    @Column(name = "ISPRIMARY", length = 1)
    private String isPrimary = "N";

    public Integer getPhoneId() { return phoneId; }
    public void setPhoneId(Integer phoneId) { this.phoneId = phoneId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public String getPhoneType() { return phoneType; }
    public void setPhoneType(String phoneType) { this.phoneType = phoneType; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public String getIsPrimary() { return isPrimary; }
    public void setIsPrimary(String isPrimary) { this.isPrimary = isPrimary; }
}
