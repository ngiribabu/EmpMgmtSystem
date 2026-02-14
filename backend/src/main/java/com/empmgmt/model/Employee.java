package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "EMPLOYEES", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPID")
    private Integer empId;

    @NotBlank
    @Size(max = 40)
    @Column(name = "FIRSTNAME", nullable = false, length = 40)
    private String firstName;

    @NotBlank
    @Size(max = 40)
    @Column(name = "LASTNAME", nullable = false, length = 40)
    private String lastName;

    @Size(max = 40)
    @Column(name = "MIDDLENAME", length = 40)
    private String middleName;

    @Size(max = 100)
    @Column(name = "EMAIL", length = 100)
    private String email;

    @NotNull
    @Column(name = "HIREDATE", nullable = false)
    private Date hireDate;

    @Column(name = "TERMDATE")
    private Date termDate;

    @Column(name = "DEPTID")
    private Integer deptId;

    @Column(name = "POSID")
    private Integer posId;

    @Column(name = "MANAGERID")
    private Integer managerId;

    @Size(max = 10)
    @Column(name = "EMPSTATUS", nullable = false, length = 10)
    private String empStatus = "ACTIVE";

    @Size(max = 100)
    @Column(name = "ADDR1", length = 100)
    private String addr1;

    @Size(max = 100)
    @Column(name = "ADDR2", length = 100)
    private String addr2;

    @Size(max = 50)
    @Column(name = "CITY", length = 50)
    private String city;

    @Size(max = 50)
    @Column(name = "STATE", length = 50)
    private String state;

    @Size(max = 15)
    @Column(name = "ZIPCODE", length = 15)
    private String zipCode;

    @Size(max = 50)
    @Column(name = "COUNTRY", length = 50)
    private String country = "USA";

    @Column(name = "DOB")
    private Date dob;

    @Size(max = 1)
    @Column(name = "GENDER", length = 1)
    private String gender;

    // Transient fields for display
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPTID", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSID", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Position position;

    // Getters and Setters
    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public Date getTermDate() { return termDate; }
    public void setTermDate(Date termDate) { this.termDate = termDate; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public Integer getPosId() { return posId; }
    public void setPosId(Integer posId) { this.posId = posId; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public String getEmpStatus() { return empStatus; }
    public void setEmpStatus(String empStatus) { this.empStatus = empStatus; }

    public String getAddr1() { return addr1; }
    public void setAddr1(String addr1) { this.addr1 = addr1; }

    public String getAddr2() { return addr2; }
    public void setAddr2(String addr2) { this.addr2 = addr2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
}
