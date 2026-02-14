package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "LEAVEREQS", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveReq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LVREQID")
    private Integer lvReqId;

    @NotNull
    @Column(name = "EMPID", nullable = false)
    private Integer empId;

    @NotNull
    @Column(name = "LVTYPEID", nullable = false)
    private Integer lvTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LVTYPEID", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LeaveType leaveType;

    @NotNull
    @Column(name = "STARTDATE", nullable = false)
    private Date startDate;

    @NotNull
    @Column(name = "ENDDATE", nullable = false)
    private Date endDate;

    @NotNull
    @Column(name = "TOTALDAYS", nullable = false, precision = 5, scale = 1)
    private BigDecimal totalDays;

    @Size(max = 300)
    @Column(name = "REASON", length = 300)
    private String reason;

    @Size(max = 10)
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status = "PENDING";

    @Column(name = "APPROVERID")
    private Integer approverId;

    @Column(name = "APPROVEDAT")
    private Timestamp approvedAt;

    @Size(max = 300)
    @Column(name = "COMMENTS", length = 300)
    private String comments;

    public Integer getLvReqId() { return lvReqId; }
    public void setLvReqId(Integer lvReqId) { this.lvReqId = lvReqId; }

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    public Integer getLvTypeId() { return lvTypeId; }
    public void setLvTypeId(Integer lvTypeId) { this.lvTypeId = lvTypeId; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public BigDecimal getTotalDays() { return totalDays; }
    public void setTotalDays(BigDecimal totalDays) { this.totalDays = totalDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getApproverId() { return approverId; }
    public void setApproverId(Integer approverId) { this.approverId = approverId; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
