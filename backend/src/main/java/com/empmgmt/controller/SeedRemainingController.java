package com.empmgmt.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/seed-remaining")
public class SeedRemainingController {

    private final JdbcTemplate jdbc;

    public SeedRemainingController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/phones")
    public Map<String, Object> seedPhones() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPPHONENB", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " phones"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            Random rand = new Random(42);
            int count = 0;

            // Build batch inserts (10 rows per statement)
            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int empId : empIds) {
                String mobile = String.format("(%03d) %03d-%04d", 200 + rand.nextInt(800), rand.nextInt(1000), rand.nextInt(10000));
                if (batchSize > 0) sb.append(",");
                sb.append("(").append(empId).append(",'MOBILE','").append(mobile).append("','Y')");
                batchSize++;
                if (batchSize >= 10) {
                    jdbc.execute("INSERT INTO NGIRI4001.EMPPHONENB (EMPID,PHONETYPE,PHONENUM,ISPRIMARY) VALUES " + sb);
                    count += batchSize;
                    sb = new StringBuilder();
                    batchSize = 0;
                }

                if (rand.nextInt(100) < 80) {
                    String secType = rand.nextBoolean() ? "HOME" : "WORK";
                    String sec = String.format("(%03d) %03d-%04d", 200 + rand.nextInt(800), rand.nextInt(1000), rand.nextInt(10000));
                    if (batchSize > 0) sb.append(",");
                    sb.append("(").append(empId).append(",'").append(secType).append("','").append(sec).append("','N')");
                    batchSize++;
                    if (batchSize >= 10) {
                        jdbc.execute("INSERT INTO NGIRI4001.EMPPHONENB (EMPID,PHONETYPE,PHONENUM,ISPRIMARY) VALUES " + sb);
                        count += batchSize;
                        sb = new StringBuilder();
                        batchSize = 0;
                    }
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.EMPPHONENB (EMPID,PHONETYPE,PHONENUM,ISPRIMARY) VALUES " + sb);
                count += batchSize;
            }
            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @PostMapping("/salaries")
    public Map<String, Object> seedSalaries() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.SALARIES", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " salaries"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            Random rand = new Random(42);
            String[] reasons = {"PROMOTION", "MERIT", "ADJUST"};
            int count = 0;

            // Historical salaries in batches
            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int empId : empIds) {
                double baseSalary = 40000 + rand.nextInt(100000);
                double bonus = rand.nextInt(10000);
                int hireYear = 2016 + rand.nextInt(6);

                if (batchSize > 0) sb.append(",");
                sb.append("(").append(empId).append(",").append(String.format("%.2f", baseSalary))
                  .append(",").append(String.format("%.2f", bonus))
                  .append(",'USD','ANNUAL','").append(hireYear).append("-01-15','")
                  .append(hireYear + 1).append("-12-31','N','HIRE')");
                batchSize++;

                if (batchSize >= 5) {
                    jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID,BASESALARY,BONUS,CURRENCY,PAYFREQ,EFFDATE,ENDDATE,ISCURRENT,REASON) VALUES " + sb);
                    count += batchSize;
                    sb = new StringBuilder();
                    batchSize = 0;
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID,BASESALARY,BONUS,CURRENCY,PAYFREQ,EFFDATE,ENDDATE,ISCURRENT,REASON) VALUES " + sb);
                count += batchSize;
            }

            // Current salaries
            rand = new Random(42); // reset to match
            sb = new StringBuilder();
            batchSize = 0;
            for (int empId : empIds) {
                double baseSalary = 40000 + rand.nextInt(100000);
                double bonus = rand.nextInt(10000);
                int hireYear = 2016 + rand.nextInt(6);
                double newSalary = baseSalary * (1.05 + rand.nextDouble() * 0.15);
                double newBonus = bonus * 1.1;
                String reason = reasons[rand.nextInt(reasons.length)];

                if (batchSize > 0) sb.append(",");
                sb.append("(").append(empId).append(",").append(String.format("%.2f", newSalary))
                  .append(",").append(String.format("%.2f", newBonus))
                  .append(",'USD','ANNUAL','").append(hireYear + 2).append("-01-01',NULL,'Y','").append(reason).append("')");
                batchSize++;

                if (batchSize >= 5) {
                    jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID,BASESALARY,BONUS,CURRENCY,PAYFREQ,EFFDATE,ENDDATE,ISCURRENT,REASON) VALUES " + sb);
                    count += batchSize;
                    sb = new StringBuilder();
                    batchSize = 0;
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID,BASESALARY,BONUS,CURRENCY,PAYFREQ,EFFDATE,ENDDATE,ISCURRENT,REASON) VALUES " + sb);
                count += batchSize;
            }

            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @PostMapping("/dependents")
    public Map<String, Object> seedDependents() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.DEPENDENTS", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " dependents"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            Random rand = new Random(42);
            String[] firstNames = {"Alex", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Quinn", "Harper", "Avery", "Parker"};
            String[] lastNames = {"Smith", "Jones", "Brown", "Davis", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson"};
            String[] relations = {"SPOUSE", "CHILD", "CHILD", "CHILD", "PARENT"};
            int count = 0;

            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int empId : empIds) {
                int numDeps = rand.nextInt(4);
                for (int d = 0; d < numDeps; d++) {
                    String depName = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
                    String relation = relations[rand.nextInt(relations.length)];
                    String gender = rand.nextBoolean() ? "M" : "F";
                    int depYear = 1970 + rand.nextInt(40);
                    String depDob = String.format("%04d-%02d-%02d", depYear, 1 + rand.nextInt(12), 1 + rand.nextInt(28));

                    if (batchSize > 0) sb.append(",");
                    sb.append("(").append(empId).append(",'").append(depName).append("','").append(relation)
                      .append("','").append(depDob).append("','").append(gender).append("')");
                    batchSize++;

                    if (batchSize >= 10) {
                        jdbc.execute("INSERT INTO NGIRI4001.DEPENDENTS (EMPID,DEPNAME,RELATION,DOB,GENDER) VALUES " + sb);
                        count += batchSize;
                        sb = new StringBuilder();
                        batchSize = 0;
                    }
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.DEPENDENTS (EMPID,DEPNAME,RELATION,DOB,GENDER) VALUES " + sb);
                count += batchSize;
            }

            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @PostMapping("/emphist")
    public Map<String, Object> seedEmpHist() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPHIST", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " history records"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            List<Map<String, Object>> posRows = jdbc.queryForList("SELECT POSID, DEPTID FROM NGIRI4001.POSITIONS ORDER BY POSID");
            Random rand = new Random(42);
            String[] hireDates = {"2019-03-15", "2018-06-20", "2017-01-10", "2020-02-14", "2019-08-05",
                "2018-11-30", "2016-04-22", "2021-01-15", "2019-07-08", "2020-05-20"};
            int count = 0;

            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int i = 0; i < empIds.size(); i++) {
                int posIdx = rand.nextInt(posRows.size());
                Map<String, Object> pos = posRows.get(posIdx);
                int newPosId = ((Number) pos.get("POSID")).intValue();
                int newDeptId = ((Number) pos.get("DEPTID")).intValue();
                String hireDate = hireDates[i % hireDates.length];
                int salary = 40000 + rand.nextInt(80000);

                if (batchSize > 0) sb.append(",");
                sb.append("(").append(empIds.get(i)).append(",'").append(hireDate).append("','HIRE',")
                  .append(newDeptId).append(",").append(newPosId).append(",").append(salary).append(",'Initial hire')");
                batchSize++;

                if (batchSize >= 5) {
                    jdbc.execute("INSERT INTO NGIRI4001.EMPHIST (EMPID,EFFDATE,CHANGETYPE,NEWDEPTID,NEWPOSID,NEWSALARY,NOTES) VALUES " + sb);
                    count += batchSize;
                    sb = new StringBuilder();
                    batchSize = 0;
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.EMPHIST (EMPID,EFFDATE,CHANGETYPE,NEWDEPTID,NEWPOSID,NEWSALARY,NOTES) VALUES " + sb);
                count += batchSize;
            }

            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @PostMapping("/attendance")
    public Map<String, Object> seedAttendance() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.ATTENDANCE", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " attendance records"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            Random rand = new Random(42);
            String[] statuses = {"PRESENT", "PRESENT", "PRESENT", "PRESENT", "WFH", "HALFDAY", "ABSENT"};
            String[] workDates = {"2026-02-03", "2026-02-04", "2026-02-05", "2026-02-06", "2026-02-07"};
            int count = 0;

            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int empId : empIds) {
                for (String wd : workDates) {
                    String status = statuses[rand.nextInt(statuses.length)];
                    double hrs = status.equals("PRESENT") ? 8.0 : (status.equals("HALFDAY") ? 4.0 : (status.equals("WFH") ? 8.0 : 0));
                    double ot = (status.equals("PRESENT") && rand.nextInt(100) < 20) ? (1 + rand.nextInt(3)) : 0;
                    int clockInHr = 7 + rand.nextInt(3);
                    int clockInMin = rand.nextInt(60);
                    String clockIn = status.equals("ABSENT") ? "NULL" : String.format("'%02d:%02d:00'", clockInHr, clockInMin);
                    String clockOut = status.equals("ABSENT") ? "NULL" : String.format("'%02d:%02d:00'", clockInHr + (int) hrs, clockInMin);

                    if (batchSize > 0) sb.append(",");
                    sb.append("(").append(empId).append(",'").append(wd).append("',")
                      .append(clockIn).append(",").append(clockOut).append(",").append(hrs).append(",").append(ot)
                      .append(",'").append(status).append("')");
                    batchSize++;

                    if (batchSize >= 10) {
                        jdbc.execute("INSERT INTO NGIRI4001.ATTENDANCE (EMPID,WORKDATE,CLOCKIN,CLOCKOUT,HRSWORKED,OTHRS,STATUS) VALUES " + sb);
                        count += batchSize;
                        sb = new StringBuilder();
                        batchSize = 0;
                    }
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.ATTENDANCE (EMPID,WORKDATE,CLOCKIN,CLOCKOUT,HRSWORKED,OTHRS,STATUS) VALUES " + sb);
                count += batchSize;
            }

            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @PostMapping("/leavereqs")
    public Map<String, Object> seedLeaveReqs() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.LEAVEREQS", Integer.class);
            if (existing > 0) { result.put("msg", "Already has " + existing + " leave requests"); return result; }

            List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
            List<Integer> lvTypeIds = jdbc.queryForList("SELECT LVTYPEID FROM NGIRI4001.LEAVETYPES ORDER BY LVTYPEID", Integer.class);
            if (lvTypeIds.isEmpty()) { result.put("error", "No leave types"); return result; }

            Random rand = new Random(42);
            String[] lvStatuses = {"PENDING", "APPROVED", "APPROVED", "APPROVED", "REJECTED", "CANCELLED"};
            int count = 0;

            StringBuilder sb = new StringBuilder();
            int batchSize = 0;
            for (int empId : empIds) {
                int numReqs = 1 + rand.nextInt(3);
                for (int r = 0; r < numReqs; r++) {
                    int lvTypeId = lvTypeIds.get(rand.nextInt(lvTypeIds.size()));
                    int startMonth = 1 + rand.nextInt(12);
                    int startDay = 1 + rand.nextInt(28);
                    int totalDays = 1 + rand.nextInt(5);
                    String startDate = String.format("2025-%02d-%02d", startMonth, startDay);
                    String endDate = String.format("2025-%02d-%02d", startMonth, Math.min(startDay + totalDays, 28));
                    String status = lvStatuses[rand.nextInt(lvStatuses.length)];
                    String approver = (status.equals("APPROVED") || status.equals("REJECTED")) && empIds.size() > 15 ?
                        String.valueOf(empIds.get(rand.nextInt(15))) : "NULL";

                    if (batchSize > 0) sb.append(",");
                    sb.append("(").append(empId).append(",").append(lvTypeId).append(",'").append(startDate)
                      .append("','").append(endDate).append("',").append(totalDays).append(",'Personal reasons','")
                      .append(status).append("',").append(approver).append(",")
                      .append(approver.equals("NULL") ? "NULL" : "CURRENT_TIMESTAMP").append(")");
                    batchSize++;

                    if (batchSize >= 5) {
                        jdbc.execute("INSERT INTO NGIRI4001.LEAVEREQS (EMPID,LVTYPEID,STARTDATE,ENDDATE,TOTALDAYS,REASON,STATUS,APPROVERID,APPROVEDAT) VALUES " + sb);
                        count += batchSize;
                        sb = new StringBuilder();
                        batchSize = 0;
                    }
                }
            }
            if (batchSize > 0) {
                jdbc.execute("INSERT INTO NGIRI4001.LEAVEREQS (EMPID,LVTYPEID,STARTDATE,ENDDATE,TOTALDAYS,REASON,STATUS,APPROVERID,APPROVEDAT) VALUES " + sb);
                count += batchSize;
            }

            result.put("success", true);
            result.put("inserted", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
