-- ============================================================
-- Employee Management System - Database Creation Script
-- Target:  DB2 for IBM i (PUB400.COM)
-- Library: NGIRI4001
-- ============================================================
-- Run this script using the Db2 for i extension in Cursor.
-- Make sure your connection is set to PUB400.COM and the
-- current schema / library is NGIRI4001.
--
-- Tables are created in dependency order (parents first).
-- Each table is preceded by DROP TABLE IF EXISTS so the
-- script is fully re-runnable.
-- ============================================================

SET SCHEMA NGIRI4001;

-- ============================================================
-- Drop tables in reverse dependency order (children first)
-- Note: DB2 for i syntax is DROP TABLE name IF EXISTS
-- ============================================================
DROP TABLE NGIRI4001.LEAVEREQS IF EXISTS;
DROP TABLE NGIRI4001.LEAVETYPES IF EXISTS;
DROP TABLE NGIRI4001.ATTENDANCE IF EXISTS;
DROP TABLE NGIRI4001.EMPHIST IF EXISTS;
DROP TABLE NGIRI4001.DEPENDENTS IF EXISTS;
DROP TABLE NGIRI4001.SALARIES IF EXISTS;
DROP TABLE NGIRI4001.EMPPHONENB IF EXISTS;
DROP TABLE NGIRI4001.EMPLOYEES IF EXISTS;
DROP TABLE NGIRI4001.POSITIONS IF EXISTS;
DROP TABLE NGIRI4001.DEPARTMENTS IF EXISTS;

-- ============================================================
-- 1. DEPARTMENTS - Department master
--    Columns: dept ID, name, location, manager, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.DEPARTMENTS (
    DEPTID       INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    DEPTNAME     VARCHAR(50)   NOT NULL,
    DEPTDESC     VARCHAR(200)  DEFAULT NULL,
    LOCATION     VARCHAR(100)  DEFAULT NULL,
    MANAGERID    INTEGER       DEFAULT NULL,
    ISACTIVE     CHAR(1)       NOT NULL DEFAULT 'Y',
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT DEPT_PK   PRIMARY KEY (DEPTID),
    CONSTRAINT DEPT_NM_U UNIQUE (DEPTNAME),
    CONSTRAINT DEPT_ACT  CHECK (ISACTIVE IN ('Y', 'N'))
);

LABEL ON TABLE NGIRI4001.DEPARTMENTS IS 'Department Master';

LABEL ON COLUMN NGIRI4001.DEPARTMENTS (
    DEPTID    IS 'Dept ID',
    DEPTNAME  IS 'Dept Name',
    DEPTDESC  IS 'Description',
    LOCATION  IS 'Location',
    MANAGERID IS 'Manager EmpID',
    ISACTIVE  IS 'Active Y/N',
    CREATEDAT IS 'Created At',
    UPDATEDAT IS 'Updated At',
    CREATEDBY IS 'Created By',
    UPDATEDBY IS 'Updated By'
);

-- ============================================================
-- 2. POSITIONS - Job positions / titles
--    Columns: position ID, title, dept FK, min/max salary,
--             description, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.POSITIONS (
    POSID        INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    POSTITLE     VARCHAR(60)   NOT NULL,
    POSDESC      VARCHAR(200)  DEFAULT NULL,
    DEPTID       INTEGER       NOT NULL,
    MINSALARY    DECIMAL(11,2) NOT NULL DEFAULT 0,
    MAXSALARY    DECIMAL(11,2) NOT NULL DEFAULT 0,
    ISACTIVE     CHAR(1)       NOT NULL DEFAULT 'Y',
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT POS_PK    PRIMARY KEY (POSID),
    CONSTRAINT POS_ACT   CHECK (ISACTIVE IN ('Y', 'N')),
    CONSTRAINT POS_SAL   CHECK (MAXSALARY >= MINSALARY),
    CONSTRAINT POS_DPT_FK FOREIGN KEY (DEPTID)
        REFERENCES NGIRI4001.DEPARTMENTS (DEPTID)
);

LABEL ON TABLE NGIRI4001.POSITIONS IS 'Job Positions';

LABEL ON COLUMN NGIRI4001.POSITIONS (
    POSID     IS 'Position ID',
    POSTITLE  IS 'Position Title',
    POSDESC   IS 'Description',
    DEPTID    IS 'Dept ID',
    MINSALARY IS 'Min Salary',
    MAXSALARY IS 'Max Salary',
    ISACTIVE  IS 'Active Y/N',
    CREATEDAT IS 'Created At',
    UPDATEDAT IS 'Updated At',
    CREATEDBY IS 'Created By',
    UPDATEDBY IS 'Updated By'
);

-- ============================================================
-- 3. EMPLOYEES - Employee master
--    Columns: emp ID, name, email, hire date, dept, position,
--             status, address fields, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.EMPLOYEES (
    EMPID        INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1000 INCREMENT BY 1),
    FIRSTNAME    VARCHAR(40)   NOT NULL,
    LASTNAME     VARCHAR(40)   NOT NULL,
    MIDDLENAME   VARCHAR(40)   DEFAULT NULL,
    EMAIL        VARCHAR(100)  DEFAULT NULL,
    HIREDATE     DATE          NOT NULL,
    TERMDATE     DATE          DEFAULT NULL,
    DEPTID       INTEGER       DEFAULT NULL,
    POSID        INTEGER       DEFAULT NULL,
    MANAGERID    INTEGER       DEFAULT NULL,
    EMPSTATUS    VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE',
    ADDR1        VARCHAR(100)  DEFAULT NULL,
    ADDR2        VARCHAR(100)  DEFAULT NULL,
    CITY         VARCHAR(50)   DEFAULT NULL,
    STATE        VARCHAR(50)   DEFAULT NULL,
    ZIPCODE      VARCHAR(15)   DEFAULT NULL,
    COUNTRY      VARCHAR(50)   DEFAULT 'USA',
    DOB          DATE          DEFAULT NULL,
    GENDER       CHAR(1)       DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT EMP_PK    PRIMARY KEY (EMPID),
    CONSTRAINT EMP_STAT  CHECK (EMPSTATUS IN
        ('ACTIVE', 'INACTIVE', 'TERMINATE', 'ONLEAVE', 'SUSPENDED')),
    CONSTRAINT EMP_GEN   CHECK (GENDER IN ('M', 'F', 'O')
        OR GENDER IS NULL),
    CONSTRAINT EMP_DPT_FK FOREIGN KEY (DEPTID)
        REFERENCES NGIRI4001.DEPARTMENTS (DEPTID),
    CONSTRAINT EMP_POS_FK FOREIGN KEY (POSID)
        REFERENCES NGIRI4001.POSITIONS (POSID),
    CONSTRAINT EMP_MGR_FK FOREIGN KEY (MANAGERID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.EMPLOYEES IS 'Employee Master';

LABEL ON COLUMN NGIRI4001.EMPLOYEES (
    EMPID      IS 'Employee ID',
    FIRSTNAME  IS 'First Name',
    LASTNAME   IS 'Last Name',
    MIDDLENAME IS 'Middle Name',
    EMAIL      IS 'Email',
    HIREDATE   IS 'Hire Date',
    TERMDATE   IS 'Term Date',
    DEPTID     IS 'Dept ID',
    POSID      IS 'Position ID',
    MANAGERID  IS 'Manager EmpID',
    EMPSTATUS  IS 'Status',
    ADDR1      IS 'Address Line1',
    ADDR2      IS 'Address Line2',
    CITY       IS 'City',
    STATE      IS 'State/Prov',
    ZIPCODE    IS 'Zip/Postal',
    COUNTRY    IS 'Country',
    DOB        IS 'Date of Birth',
    GENDER     IS 'Gender M/F/O',
    CREATEDAT  IS 'Created At',
    UPDATEDAT  IS 'Updated At',
    CREATEDBY  IS 'Created By',
    UPDATEDBY  IS 'Updated By'
);

-- Now that EMPLOYEES exists, add the manager FK on DEPARTMENTS
ALTER TABLE NGIRI4001.DEPARTMENTS
    ADD CONSTRAINT DEPT_MGR_FK FOREIGN KEY (MANAGERID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID);

-- ============================================================
-- 4. EMPPHONENB - Employee phone numbers
--    Supports multiple phones per employee (home, mobile, work)
-- ============================================================
CREATE TABLE NGIRI4001.EMPPHONENB (
    PHONEID      INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    PHONETYPE    VARCHAR(10)   NOT NULL DEFAULT 'MOBILE',
    PHONENUM     VARCHAR(20)   NOT NULL,
    ISPRIMARY    CHAR(1)       NOT NULL DEFAULT 'N',
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT PHN_PK    PRIMARY KEY (PHONEID),
    CONSTRAINT PHN_TYPE  CHECK (PHONETYPE IN
        ('HOME', 'MOBILE', 'WORK', 'FAX', 'OTHER')),
    CONSTRAINT PHN_PRI   CHECK (ISPRIMARY IN ('Y', 'N')),
    CONSTRAINT PHN_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.EMPPHONENB IS 'Employee Phones';

LABEL ON COLUMN NGIRI4001.EMPPHONENB (
    PHONEID   IS 'Phone ID',
    EMPID     IS 'Employee ID',
    PHONETYPE IS 'Phone Type',
    PHONENUM  IS 'Phone Number',
    ISPRIMARY IS 'Primary Y/N',
    CREATEDAT IS 'Created At',
    UPDATEDAT IS 'Updated At',
    CREATEDBY IS 'Created By',
    UPDATEDBY IS 'Updated By'
);

-- ============================================================
-- 5. SALARIES - Current and historical salary records
--    Columns: emp ID, base salary, bonus, effective date,
--             end date, is-current flag, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.SALARIES (
    SALARYID     INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    BASESALARY   DECIMAL(11,2) NOT NULL,
    BONUS        DECIMAL(11,2) DEFAULT 0,
    CURRENCY     CHAR(3)       NOT NULL DEFAULT 'USD',
    PAYFREQ      VARCHAR(10)   NOT NULL DEFAULT 'ANNUAL',
    EFFDATE      DATE          NOT NULL,
    ENDDATE      DATE          DEFAULT NULL,
    ISCURRENT    CHAR(1)       NOT NULL DEFAULT 'Y',
    REASON       VARCHAR(50)   DEFAULT NULL,
    NOTES        VARCHAR(300)  DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT SAL_PK    PRIMARY KEY (SALARYID),
    CONSTRAINT SAL_CUR   CHECK (ISCURRENT IN ('Y', 'N')),
    CONSTRAINT SAL_FREQ  CHECK (PAYFREQ IN
        ('ANNUAL', 'MONTHLY', 'BIWEEKLY', 'WEEKLY', 'HOURLY')),
    CONSTRAINT SAL_RSN   CHECK (REASON IN
        ('HIRE', 'PROMOTION', 'MERIT', 'ADJUST', 'TRANSFER', 'OTHER')
        OR REASON IS NULL),
    CONSTRAINT SAL_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.SALARIES IS 'Salary Records';

LABEL ON COLUMN NGIRI4001.SALARIES (
    SALARYID   IS 'Salary ID',
    EMPID      IS 'Employee ID',
    BASESALARY IS 'Base Salary',
    BONUS      IS 'Bonus Amount',
    CURRENCY   IS 'Currency',
    PAYFREQ    IS 'Pay Frequency',
    EFFDATE    IS 'Effective Date',
    ENDDATE    IS 'End Date',
    ISCURRENT  IS 'Current Y/N',
    REASON     IS 'Change Reason',
    NOTES      IS 'Notes',
    CREATEDAT  IS 'Created At',
    UPDATEDAT  IS 'Updated At',
    CREATEDBY  IS 'Created By',
    UPDATEDBY  IS 'Updated By'
);

-- ============================================================
-- 6. DEPENDENTS - Employee dependents
--    Columns: name, relationship, DOB, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.DEPENDENTS (
    DEPID        INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    DEPNAME      VARCHAR(80)   NOT NULL,
    RELATION     VARCHAR(20)   NOT NULL,
    DOB          DATE          DEFAULT NULL,
    GENDER       CHAR(1)       DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT DEP_PK    PRIMARY KEY (DEPID),
    CONSTRAINT DEP_REL   CHECK (RELATION IN
        ('SPOUSE', 'CHILD', 'PARENT', 'SIBLING', 'OTHER')),
    CONSTRAINT DEP_GEN   CHECK (GENDER IN ('M', 'F', 'O')
        OR GENDER IS NULL),
    CONSTRAINT DEP_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.DEPENDENTS IS 'Employee Dependents';

LABEL ON COLUMN NGIRI4001.DEPENDENTS (
    DEPID     IS 'Dependent ID',
    EMPID     IS 'Employee ID',
    DEPNAME   IS 'Dependent Name',
    RELATION  IS 'Relationship',
    DOB       IS 'Date of Birth',
    GENDER    IS 'Gender M/F/O',
    CREATEDAT IS 'Created At',
    UPDATEDAT IS 'Updated At',
    CREATEDBY IS 'Created By',
    UPDATEDBY IS 'Updated By'
);

-- ============================================================
-- 7. EMPHIST - Employment history / job changes
--    Tracks position changes, dept transfers, promotions
-- ============================================================
CREATE TABLE NGIRI4001.EMPHIST (
    HISTID       INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    EFFDATE      DATE          NOT NULL,
    ENDDATE      DATE          DEFAULT NULL,
    CHANGETYPE   VARCHAR(15)   NOT NULL,
    OLDDEPTID    INTEGER       DEFAULT NULL,
    NEWDEPTID    INTEGER       DEFAULT NULL,
    OLDPOSID     INTEGER       DEFAULT NULL,
    NEWPOSID     INTEGER       DEFAULT NULL,
    OLDSALARY    DECIMAL(11,2) DEFAULT NULL,
    NEWSALARY    DECIMAL(11,2) DEFAULT NULL,
    NOTES        VARCHAR(300)  DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT HST_PK    PRIMARY KEY (HISTID),
    CONSTRAINT HST_CHG   CHECK (CHANGETYPE IN
        ('HIRE', 'PROMOTION', 'DEMOTION', 'TRANSFER',
         'TITLE_CHG', 'REHIRE', 'TERMINATE', 'OTHER')),
    CONSTRAINT HST_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID),
    CONSTRAINT HST_ODPT_FK FOREIGN KEY (OLDDEPTID)
        REFERENCES NGIRI4001.DEPARTMENTS (DEPTID),
    CONSTRAINT HST_NDPT_FK FOREIGN KEY (NEWDEPTID)
        REFERENCES NGIRI4001.DEPARTMENTS (DEPTID),
    CONSTRAINT HST_OPOS_FK FOREIGN KEY (OLDPOSID)
        REFERENCES NGIRI4001.POSITIONS (POSID),
    CONSTRAINT HST_NPOS_FK FOREIGN KEY (NEWPOSID)
        REFERENCES NGIRI4001.POSITIONS (POSID)
);

LABEL ON TABLE NGIRI4001.EMPHIST IS 'Employment History';

LABEL ON COLUMN NGIRI4001.EMPHIST (
    HISTID     IS 'History ID',
    EMPID      IS 'Employee ID',
    EFFDATE    IS 'Effective Date',
    ENDDATE    IS 'End Date',
    CHANGETYPE IS 'Change Type',
    OLDDEPTID  IS 'Old Dept ID',
    NEWDEPTID  IS 'New Dept ID',
    OLDPOSID   IS 'Old Position',
    NEWPOSID   IS 'New Position',
    OLDSALARY  IS 'Old Salary',
    NEWSALARY  IS 'New Salary',
    NOTES      IS 'Notes',
    CREATEDAT  IS 'Created At',
    UPDATEDAT  IS 'Updated At',
    CREATEDBY  IS 'Created By',
    UPDATEDBY  IS 'Updated By'
);

-- ============================================================
-- 8. ATTENDANCE - Daily attendance tracking
--    Columns: clock in/out, hours worked, status, audit cols
-- ============================================================
CREATE TABLE NGIRI4001.ATTENDANCE (
    ATTENDID     INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    WORKDATE     DATE          NOT NULL,
    CLOCKIN      TIME          DEFAULT NULL,
    CLOCKOUT     TIME          DEFAULT NULL,
    HRSWORKED    DECIMAL(5,2)  DEFAULT NULL,
    OTHRS        DECIMAL(5,2)  DEFAULT 0,
    STATUS       VARCHAR(10)   NOT NULL DEFAULT 'PRESENT',
    NOTES        VARCHAR(200)  DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT ATT_PK    PRIMARY KEY (ATTENDID),
    CONSTRAINT ATT_STAT  CHECK (STATUS IN
        ('PRESENT', 'ABSENT', 'HALFDAY', 'WFH', 'HOLIDAY', 'OTHER')),
    CONSTRAINT ATT_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.ATTENDANCE IS 'Attendance Tracking';

LABEL ON COLUMN NGIRI4001.ATTENDANCE (
    ATTENDID  IS 'Attendance ID',
    EMPID     IS 'Employee ID',
    WORKDATE  IS 'Work Date',
    CLOCKIN   IS 'Clock In',
    CLOCKOUT  IS 'Clock Out',
    HRSWORKED IS 'Hours Worked',
    OTHRS     IS 'Overtime Hrs',
    STATUS    IS 'Status',
    NOTES     IS 'Notes',
    CREATEDAT IS 'Created At',
    UPDATEDAT IS 'Updated At',
    CREATEDBY IS 'Created By',
    UPDATEDBY IS 'Updated By'
);

-- ============================================================
-- 9. LEAVETYPES - Leave type master
--    Defines vacation, sick, personal, etc.
-- ============================================================
CREATE TABLE NGIRI4001.LEAVETYPES (
    LVTYPEID     INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    LVTYPENAME   VARCHAR(30)   NOT NULL,
    LVTYPEDESC   VARCHAR(200)  DEFAULT NULL,
    MAXDAYS      INTEGER       DEFAULT 0,
    ISPAID       CHAR(1)       NOT NULL DEFAULT 'Y',
    ISACTIVE     CHAR(1)       NOT NULL DEFAULT 'Y',
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT LVT_PK    PRIMARY KEY (LVTYPEID),
    CONSTRAINT LVT_NM_U  UNIQUE (LVTYPENAME),
    CONSTRAINT LVT_PAID  CHECK (ISPAID IN ('Y', 'N')),
    CONSTRAINT LVT_ACT   CHECK (ISACTIVE IN ('Y', 'N'))
);

LABEL ON TABLE NGIRI4001.LEAVETYPES IS 'Leave Type Master';

LABEL ON COLUMN NGIRI4001.LEAVETYPES (
    LVTYPEID   IS 'Leave Type ID',
    LVTYPENAME IS 'Leave Type',
    LVTYPEDESC IS 'Description',
    MAXDAYS    IS 'Max Days/Year',
    ISPAID     IS 'Paid Y/N',
    ISACTIVE   IS 'Active Y/N',
    CREATEDAT  IS 'Created At',
    UPDATEDAT  IS 'Updated At',
    CREATEDBY  IS 'Created By',
    UPDATEDBY  IS 'Updated By'
);

-- ============================================================
-- 10. LEAVEREQS - Leave requests
--     Columns: employee, type, dates, status, approver
-- ============================================================
CREATE TABLE NGIRI4001.LEAVEREQS (
    LVREQID      INTEGER       GENERATED ALWAYS AS IDENTITY
                               (START WITH 1 INCREMENT BY 1),
    EMPID        INTEGER       NOT NULL,
    LVTYPEID     INTEGER       NOT NULL,
    STARTDATE    DATE          NOT NULL,
    ENDDATE      DATE          NOT NULL,
    TOTALDAYS    DECIMAL(5,1)  NOT NULL,
    REASON       VARCHAR(300)  DEFAULT NULL,
    STATUS       VARCHAR(10)   NOT NULL DEFAULT 'PENDING',
    APPROVERID   INTEGER       DEFAULT NULL,
    APPROVEDAT   TIMESTAMP     DEFAULT NULL,
    COMMENTS     VARCHAR(300)  DEFAULT NULL,
    CREATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATEDAT    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    UPDATEDBY    VARCHAR(30)   NOT NULL DEFAULT USER,
    CONSTRAINT LVR_PK    PRIMARY KEY (LVREQID),
    CONSTRAINT LVR_STAT  CHECK (STATUS IN
        ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT LVR_DATES CHECK (ENDDATE >= STARTDATE),
    CONSTRAINT LVR_EMP_FK FOREIGN KEY (EMPID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID),
    CONSTRAINT LVR_TYP_FK FOREIGN KEY (LVTYPEID)
        REFERENCES NGIRI4001.LEAVETYPES (LVTYPEID),
    CONSTRAINT LVR_APR_FK FOREIGN KEY (APPROVERID)
        REFERENCES NGIRI4001.EMPLOYEES (EMPID)
);

LABEL ON TABLE NGIRI4001.LEAVEREQS IS 'Leave Requests';

LABEL ON COLUMN NGIRI4001.LEAVEREQS (
    LVREQID    IS 'Leave Req ID',
    EMPID      IS 'Employee ID',
    LVTYPEID   IS 'Leave Type ID',
    STARTDATE  IS 'Start Date',
    ENDDATE    IS 'End Date',
    TOTALDAYS  IS 'Total Days',
    REASON     IS 'Reason',
    STATUS     IS 'Status',
    APPROVERID IS 'Approver EmpID',
    APPROVEDAT IS 'Approved At',
    COMMENTS   IS 'Comments',
    CREATEDAT  IS 'Created At',
    UPDATEDAT  IS 'Updated At',
    CREATEDBY  IS 'Created By',
    UPDATEDBY  IS 'Updated By'
);

-- ============================================================
-- INDEXES for query performance
-- ============================================================

-- Employee indexes
CREATE INDEX NGIRI4001.EMP_NAME_IX
    ON NGIRI4001.EMPLOYEES (LASTNAME, FIRSTNAME);
CREATE INDEX NGIRI4001.EMP_DEPT_IX
    ON NGIRI4001.EMPLOYEES (DEPTID);
CREATE INDEX NGIRI4001.EMP_POS_IX
    ON NGIRI4001.EMPLOYEES (POSID);
CREATE INDEX NGIRI4001.EMP_MGR_IX
    ON NGIRI4001.EMPLOYEES (MANAGERID);
CREATE INDEX NGIRI4001.EMP_STAT_IX
    ON NGIRI4001.EMPLOYEES (EMPSTATUS);
CREATE INDEX NGIRI4001.EMP_HIRE_IX
    ON NGIRI4001.EMPLOYEES (HIREDATE);

-- Phone indexes
CREATE INDEX NGIRI4001.PHN_EMP_IX
    ON NGIRI4001.EMPPHONENB (EMPID);

-- Salary indexes
CREATE INDEX NGIRI4001.SAL_EMP_IX
    ON NGIRI4001.SALARIES (EMPID);
CREATE INDEX NGIRI4001.SAL_CUR_IX
    ON NGIRI4001.SALARIES (EMPID, ISCURRENT);
CREATE INDEX NGIRI4001.SAL_EFF_IX
    ON NGIRI4001.SALARIES (EFFDATE);

-- Dependent indexes
CREATE INDEX NGIRI4001.DEP_EMP_IX
    ON NGIRI4001.DEPENDENTS (EMPID);

-- Employment history indexes
CREATE INDEX NGIRI4001.HST_EMP_IX
    ON NGIRI4001.EMPHIST (EMPID);
CREATE INDEX NGIRI4001.HST_EFF_IX
    ON NGIRI4001.EMPHIST (EFFDATE);

-- Attendance indexes
CREATE INDEX NGIRI4001.ATT_EMP_IX
    ON NGIRI4001.ATTENDANCE (EMPID);
CREATE INDEX NGIRI4001.ATT_DT_IX
    ON NGIRI4001.ATTENDANCE (WORKDATE);
CREATE INDEX NGIRI4001.ATT_EMPDT_IX
    ON NGIRI4001.ATTENDANCE (EMPID, WORKDATE);

-- Leave request indexes
CREATE INDEX NGIRI4001.LVR_EMP_IX
    ON NGIRI4001.LEAVEREQS (EMPID);
CREATE INDEX NGIRI4001.LVR_TYP_IX
    ON NGIRI4001.LEAVEREQS (LVTYPEID);
CREATE INDEX NGIRI4001.LVR_STAT_IX
    ON NGIRI4001.LEAVEREQS (STATUS);
CREATE INDEX NGIRI4001.LVR_APR_IX
    ON NGIRI4001.LEAVEREQS (APPROVERID);

-- ============================================================
-- Seed data: Leave Types
-- ============================================================
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Vacation',  'Annual vacation / PTO',             20, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Sick',      'Sick leave',                        10, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Personal',  'Personal day off',                   5, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Bereavement','Bereavement leave',                 5, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Jury Duty', 'Jury duty leave',                   10, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Maternity', 'Maternity / paternity leave',       60, 'Y');
INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID)
    VALUES ('Unpaid',    'Unpaid leave of absence',           30, 'N');

-- ============================================================
-- End of script
-- ============================================================
