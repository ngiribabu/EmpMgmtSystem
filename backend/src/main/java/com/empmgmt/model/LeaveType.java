package com.empmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "LEAVETYPES", schema = "NGIRI4001")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LVTYPEID")
    private Integer lvTypeId;

    @NotBlank
    @Size(max = 30)
    @Column(name = "LVTYPENAME", nullable = false, length = 30)
    private String lvTypeName;

    @Size(max = 200)
    @Column(name = "LVTYPEDESC", length = 200)
    private String lvTypeDesc;

    @Column(name = "MAXDAYS")
    private Integer maxDays = 0;

    @Column(name = "ISPAID", length = 1)
    private String isPaid = "Y";

    @Column(name = "ISACTIVE", length = 1)
    private String isActive = "Y";

    public Integer getLvTypeId() { return lvTypeId; }
    public void setLvTypeId(Integer lvTypeId) { this.lvTypeId = lvTypeId; }

    public String getLvTypeName() { return lvTypeName; }
    public void setLvTypeName(String lvTypeName) { this.lvTypeName = lvTypeName; }

    public String getLvTypeDesc() { return lvTypeDesc; }
    public void setLvTypeDesc(String lvTypeDesc) { this.lvTypeDesc = lvTypeDesc; }

    public Integer getMaxDays() { return maxDays; }
    public void setMaxDays(Integer maxDays) { this.maxDays = maxDays; }

    public String getIsPaid() { return isPaid; }
    public void setIsPaid(String isPaid) { this.isPaid = isPaid; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}
