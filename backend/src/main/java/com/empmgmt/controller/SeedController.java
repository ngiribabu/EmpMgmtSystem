package com.empmgmt.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/seed")
public class SeedController {

    private final JdbcTemplate jdbc;

    public SeedController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping
    public Map<String, Object> seedData() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> log = new ArrayList<>();

        try {
            // Check if data already exists
            int existingEmps = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPLOYEES", Integer.class);
            if (existingEmps > 0) {
                result.put("success", false);
                result.put("error", "Data already exists (" + existingEmps + " employees). Use /api/seed/reset first to clear, then seed again.");
                return result;
            }
        } catch (Exception e) {
            // Table might not exist yet, continue
        }

        try {
            // Check if departments already exist
            int existingDepts = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.DEPARTMENTS", Integer.class);
            if (existingDepts > 0) {
                log.add("Departments already exist (" + existingDepts + "), skipping department insert");
            } else {
                seedDepartments(log);
            }
        } catch (Exception e) {
            seedDepartments(log);
        }

        try { seedPositions(log); } catch (Exception e) { log.add("Positions error: " + e.getMessage()); }
        try { seedEmployees(log); } catch (Exception e) { log.add("Employees error: " + e.getMessage()); }
        try { seedPhones(log); } catch (Exception e) { log.add("Phones error: " + e.getMessage()); }
        try { seedSalaries(log); } catch (Exception e) { log.add("Salaries error: " + e.getMessage()); }
        try { seedDependents(log); } catch (Exception e) { log.add("Dependents error: " + e.getMessage()); }
        try { seedEmpHist(log); } catch (Exception e) { log.add("EmpHist error: " + e.getMessage()); }
        try { seedAttendance(log); } catch (Exception e) { log.add("Attendance error: " + e.getMessage()); }
        try { seedLeaveReqs(log); } catch (Exception e) { log.add("LeaveReqs error: " + e.getMessage()); }

        result.put("success", true);
        result.put("log", log);
        return result;
    }

    @PostMapping("/journal")
    public Map<String, Object> setupJournaling() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> log = new ArrayList<>();

        // Step 1: Create journal receiver
        try {
            runCLCommand("CRTJRNRCV JRNRCV(NGIRI4001/QSQJRNRCV) THRESHOLD(100000) TEXT('EMS Journal Receiver')");
            log.add("Created journal receiver QSQJRNRCV");
        } catch (Exception e) {
            log.add("Journal receiver: " + e.getMessage());
        }

        // Step 2: Create journal
        try {
            runCLCommand("CRTJRN JRN(NGIRI4001/QSQJRN) JRNRCV(NGIRI4001/QSQJRNRCV) MNGRCV(*SYSTEM) DLTRCV(*YES) TEXT('EMS Journal')");
            log.add("Created journal QSQJRN");
        } catch (Exception e) {
            log.add("Journal: " + e.getMessage());
        }

        // Step 3: Start journaling on all tables
        String[] tables = {"DEPARTMENTS", "POSITIONS", "EMPLOYEES", "EMPPHONENB", "SALARIES", "DEPENDENTS", "EMPHIST", "ATTENDANCE", "LEAVETYPES", "LEAVEREQS"};
        for (String t : tables) {
            try {
                // DB2 for i uses system name which may differ; try the table name directly
                runCLCommand("STRJRNPF FILE(NGIRI4001/" + t + ") JRN(NGIRI4001/QSQJRN) IMAGES(*BOTH) OMTJRNE(*OPNCLO)");
                log.add("Started journaling on " + t);
            } catch (Exception e) {
                // Try with system name (first 10 chars)
                String sysName = t.length() > 10 ? t.substring(0, 10) : t;
                if (!sysName.equals(t)) {
                    try {
                        runCLCommand("STRJRNPF FILE(NGIRI4001/" + sysName + ") JRN(NGIRI4001/QSQJRN) IMAGES(*BOTH) OMTJRNE(*OPNCLO)");
                        log.add("Started journaling on " + sysName + " (system name for " + t + ")");
                    } catch (Exception e2) {
                        log.add("Journal " + t + ": " + e2.getMessage());
                    }
                } else {
                    log.add("Journal " + t + ": " + e.getMessage());
                }
            }
        }

        // Also try DEPAR00001 which is the system name for DEPARTMENTS
        try {
            runCLCommand("STRJRNPF FILE(NGIRI4001/DEPAR00001) JRN(NGIRI4001/QSQJRN) IMAGES(*BOTH) OMTJRNE(*OPNCLO)");
            log.add("Started journaling on DEPAR00001 (system name)");
        } catch (Exception e) {
            log.add("DEPAR00001: " + e.getMessage());
        }

        result.put("success", true);
        result.put("log", log);
        return result;
    }

    private void runCLCommand(String command) {
        jdbc.execute("CALL QSYS.QCMDEXC('" + command.replace("'", "''") + "', " + String.format("%010d", command.length()) + ".00000)");
    }

    @PostMapping("/reset")
    public Map<String, Object> resetData() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> log = new ArrayList<>();
        String[] tables = {"LEAVEREQS", "ATTENDANCE", "EMPHIST", "DEPENDENTS", "SALARIES", "EMPPHONENB", "EMPLOYEES", "POSITIONS", "DEPARTMENTS", "LEAVETYPES"};
        for (String t : tables) {
            try {
                int count = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001." + t, Integer.class);
                if (count > 0) {
                    jdbc.execute("DELETE FROM NGIRI4001." + t);
                    log.add("Deleted " + count + " rows from " + t);
                }
            } catch (Exception e) {
                log.add("Error clearing " + t + ": " + e.getMessage());
            }
        }
        // Re-seed leave types
        try {
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Vacation', 'Annual vacation / PTO', 20, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Sick', 'Sick leave', 10, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Personal', 'Personal day off', 5, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Bereavement', 'Bereavement leave', 5, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Jury Duty', 'Jury duty leave', 10, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Maternity', 'Maternity / paternity leave', 60, 'Y')");
            jdbc.execute("INSERT INTO NGIRI4001.LEAVETYPES (LVTYPENAME, LVTYPEDESC, MAXDAYS, ISPAID) VALUES ('Unpaid', 'Unpaid leave of absence', 30, 'N')");
            log.add("Re-seeded 7 leave types");
        } catch (Exception e) {
            log.add("Leave types error: " + e.getMessage());
        }
        result.put("success", true);
        result.put("log", log);
        return result;
    }

    private void seedDepartments(List<String> log) {
        String[][] depts = {
            {"Engineering", "Software development and engineering", "New York"},
            {"Human Resources", "Employee relations and recruiting", "New York"},
            {"Finance", "Financial planning and accounting", "Chicago"},
            {"Marketing", "Brand management and marketing", "Los Angeles"},
            {"Sales", "Sales and business development", "Dallas"},
            {"Operations", "Business operations and logistics", "Chicago"},
            {"IT Support", "Technical support and infrastructure", "New York"},
            {"Legal", "Corporate legal affairs", "Boston"},
            {"Research", "Research and development", "San Francisco"},
            {"Customer Service", "Customer support and success", "Dallas"},
            {"Quality Assurance", "Testing and quality control", "New York"},
            {"Product Management", "Product strategy and roadmap", "San Francisco"},
            {"Data Analytics", "Business intelligence and analytics", "Chicago"},
            {"Security", "Information security and compliance", "Boston"},
            {"Administration", "General administration", "New York"}
        };
        int count = 0;
        for (String[] d : depts) {
            try {
                jdbc.execute("INSERT INTO NGIRI4001.DEPARTMENTS (DEPTNAME, DEPTDESC, LOCATION, ISACTIVE) VALUES ('" +
                    d[0].replace("'", "''") + "', '" + d[1].replace("'", "''") + "', '" + d[2] + "', 'Y')");
                count++;
            } catch (Exception e) { /* skip duplicates */ }
        }
        log.add("Inserted " + count + " departments");
    }

    private void seedPositions(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.POSITIONS", Integer.class);
        if (existing > 0) { log.add("Positions already exist (" + existing + "), skipping"); return; }

        List<Integer> deptIds = jdbc.queryForList("SELECT DEPTID FROM NGIRI4001.DEPARTMENTS ORDER BY DEPTID", Integer.class);
        if (deptIds.isEmpty()) { log.add("No departments found, skipping positions"); return; }

        String[][] positions = {
            {"Software Engineer", "Develops software applications", "0", "70000", "120000"},
            {"Senior Software Engineer", "Senior developer and tech lead", "0", "100000", "160000"},
            {"Principal Engineer", "Technical architecture and design", "0", "140000", "200000"},
            {"Engineering Manager", "Manages engineering teams", "0", "130000", "180000"},
            {"DevOps Engineer", "CI/CD and infrastructure automation", "0", "85000", "140000"},
            {"Frontend Developer", "UI/UX development", "0", "65000", "115000"},
            {"Backend Developer", "Server-side development", "0", "70000", "125000"},
            {"HR Specialist", "Handles employee relations", "1", "50000", "80000"},
            {"HR Manager", "Manages HR department", "1", "80000", "120000"},
            {"Recruiter", "Talent acquisition", "1", "45000", "75000"},
            {"HR Coordinator", "Coordinates HR activities", "1", "40000", "65000"},
            {"Benefits Analyst", "Employee benefits administration", "1", "55000", "85000"},
            {"Training Specialist", "Employee training programs", "1", "50000", "80000"},
            {"Accountant", "Financial accounting and reporting", "2", "55000", "90000"},
            {"Senior Accountant", "Senior financial reporting", "2", "75000", "110000"},
            {"Financial Analyst", "Financial analysis and planning", "2", "65000", "100000"},
            {"Finance Manager", "Manages finance team", "2", "100000", "145000"},
            {"Controller", "Financial controller", "2", "110000", "160000"},
            {"Payroll Specialist", "Payroll processing", "2", "45000", "70000"},
            {"Tax Analyst", "Tax compliance and planning", "2", "60000", "95000"},
            {"Marketing Specialist", "Marketing campaigns", "3", "50000", "80000"},
            {"Marketing Manager", "Manages marketing team", "3", "85000", "130000"},
            {"Content Writer", "Content creation and copywriting", "3", "45000", "70000"},
            {"SEO Specialist", "Search engine optimization", "3", "50000", "80000"},
            {"Brand Manager", "Brand strategy management", "3", "75000", "115000"},
            {"Digital Marketing Analyst", "Digital campaign analytics", "3", "55000", "85000"},
            {"Graphic Designer", "Visual design and branding", "3", "50000", "80000"},
            {"Sales Representative", "Direct sales and client relations", "4", "45000", "80000"},
            {"Sales Manager", "Manages sales team", "4", "80000", "130000"},
            {"Account Executive", "Key account management", "4", "60000", "100000"},
            {"Business Dev Manager", "Business development strategy", "4", "90000", "140000"},
            {"Sales Analyst", "Sales data and forecasting", "4", "55000", "85000"},
            {"Inside Sales Rep", "Inside sales and lead gen", "4", "40000", "65000"},
            {"Regional Sales Director", "Regional sales leadership", "4", "110000", "170000"},
            {"Operations Manager", "Daily operations management", "5", "80000", "120000"},
            {"Operations Analyst", "Operations data analysis", "5", "55000", "85000"},
            {"Supply Chain Specialist", "Supply chain management", "5", "60000", "90000"},
            {"Logistics Coordinator", "Logistics and shipping", "5", "45000", "70000"},
            {"Warehouse Manager", "Warehouse operations", "5", "55000", "85000"},
            {"Process Improvement Analyst", "Process optimization", "5", "65000", "95000"},
            {"Help Desk Analyst", "First line tech support", "6", "40000", "60000"},
            {"System Administrator", "Server and network admin", "6", "65000", "100000"},
            {"Network Engineer", "Network infrastructure", "6", "70000", "110000"},
            {"IT Manager", "Manages IT support team", "6", "90000", "135000"},
            {"Database Administrator", "Database management", "6", "75000", "115000"},
            {"Cloud Engineer", "Cloud infrastructure management", "6", "80000", "130000"},
            {"IT Security Analyst", "IT security monitoring", "6", "70000", "105000"},
            {"Corporate Counsel", "Legal advisory", "7", "100000", "160000"},
            {"Paralegal", "Legal research and support", "7", "45000", "70000"},
            {"Legal Secretary", "Legal administrative support", "7", "40000", "60000"},
            {"Compliance Officer", "Regulatory compliance", "7", "80000", "120000"},
            {"Contract Manager", "Contract negotiation and management", "7", "75000", "110000"},
            {"Research Scientist", "Scientific research", "8", "80000", "130000"},
            {"Research Engineer", "Applied research engineering", "8", "85000", "135000"},
            {"Lab Technician", "Laboratory operations", "8", "45000", "70000"},
            {"R and D Manager", "Research and development leadership", "8", "110000", "160000"},
            {"Data Scientist", "Data analysis and modeling", "8", "90000", "145000"},
            {"Research Analyst", "Research data analysis", "8", "60000", "90000"},
            {"Customer Service Rep", "Customer phone and email support", "9", "35000", "55000"},
            {"Customer Service Manager", "Manages CS team", "9", "65000", "95000"},
            {"Customer Success Manager", "Client relationship management", "9", "70000", "105000"},
            {"Technical Support Specialist", "Advanced technical support", "9", "50000", "80000"},
            {"Call Center Supervisor", "Supervises call center ops", "9", "55000", "80000"},
            {"QA Engineer", "Software quality assurance", "10", "60000", "100000"},
            {"Senior QA Engineer", "Senior testing and automation", "10", "85000", "130000"},
            {"QA Manager", "QA team management", "10", "95000", "140000"},
            {"Test Automation Engineer", "Automated testing frameworks", "10", "75000", "120000"},
            {"Performance Tester", "Performance and load testing", "10", "70000", "110000"},
            {"Product Manager", "Product strategy and execution", "11", "90000", "145000"},
            {"Senior Product Manager", "Senior product leadership", "11", "120000", "175000"},
            {"Product Analyst", "Product metrics and analysis", "11", "65000", "95000"},
            {"UX Designer", "User experience design", "11", "70000", "110000"},
            {"UX Researcher", "User research and testing", "11", "65000", "100000"},
            {"Data Analyst", "Business data analysis", "12", "55000", "90000"},
            {"Senior Data Analyst", "Advanced analytics", "12", "80000", "120000"},
            {"BI Developer", "Business intelligence development", "12", "75000", "115000"},
            {"Analytics Manager", "Analytics team leadership", "12", "100000", "150000"},
            {"Data Engineer", "Data pipeline engineering", "12", "80000", "130000"},
            {"ETL Developer", "Data integration development", "12", "70000", "110000"},
            {"Security Analyst", "Cybersecurity analysis", "13", "70000", "110000"},
            {"Security Engineer", "Security systems engineering", "13", "85000", "135000"},
            {"CISO", "Chief Information Security Officer", "13", "150000", "220000"},
            {"Penetration Tester", "Security pen testing", "13", "80000", "125000"},
            {"SOC Analyst", "Security operations center", "13", "60000", "95000"},
            {"Office Manager", "Office operations management", "14", "50000", "75000"},
            {"Executive Assistant", "Executive admin support", "14", "45000", "70000"},
            {"Receptionist", "Front desk operations", "14", "30000", "45000"},
            {"Facilities Coordinator", "Building and facilities mgmt", "14", "45000", "70000"},
            {"Administrative Assistant", "General admin support", "14", "35000", "55000"},
            {"Mail Room Clerk", "Mail and package handling", "14", "28000", "40000"}
        };

        int count = 0;
        for (String[] p : positions) {
            int deptIdx = Integer.parseInt(p[2]);
            if (deptIdx < deptIds.size()) {
                jdbc.execute("INSERT INTO NGIRI4001.POSITIONS (POSTITLE, POSDESC, DEPTID, MINSALARY, MAXSALARY, ISACTIVE) VALUES ('" +
                    p[0].replace("'", "''") + "', '" + p[1].replace("'", "''") + "', " + deptIds.get(deptIdx) + ", " + p[3] + ", " + p[4] + ", 'Y')");
                count++;
            }
        }
        log.add("Inserted " + count + " positions");
    }

    private void seedEmployees(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPLOYEES", Integer.class);
        if (existing > 0) { log.add("Employees already exist (" + existing + "), skipping"); return; }

        List<Map<String, Object>> posRows = jdbc.queryForList("SELECT POSID, DEPTID FROM NGIRI4001.POSITIONS ORDER BY POSID");
        if (posRows.isEmpty()) { log.add("No positions found, skipping employees"); return; }

        String[][] employees = {
            {"James", "Anderson", "R", "2019-03-15", "M", "123 Oak Street", "New York", "NY", "10001"},
            {"Sarah", "Mitchell", "L", "2018-06-20", "F", "456 Maple Ave", "New York", "NY", "10002"},
            {"Michael", "Thompson", "J", "2017-01-10", "M", "789 Pine Road", "Chicago", "IL", "60601"},
            {"Emily", "Williams", "A", "2020-02-14", "F", "321 Elm Drive", "Los Angeles", "CA", "90001"},
            {"David", "Brown", "K", "2019-08-05", "M", "654 Cedar Lane", "Dallas", "TX", "75201"},
            {"Jennifer", "Davis", "M", "2018-11-30", "F", "987 Birch Blvd", "Chicago", "IL", "60602"},
            {"Robert", "Miller", "T", "2016-04-22", "M", "147 Walnut St", "New York", "NY", "10003"},
            {"Jessica", "Wilson", "E", "2021-01-15", "F", "258 Cherry Way", "Boston", "MA", "02101"},
            {"Christopher", "Moore", "D", "2019-07-08", "M", "369 Spruce Ct", "San Francisco", "CA", "94101"},
            {"Amanda", "Taylor", "N", "2020-05-20", "F", "741 Ash Place", "Dallas", "TX", "75202"},
            {"Daniel", "Jackson", "P", "2018-09-12", "M", "852 Poplar Ave", "New York", "NY", "10004"},
            {"Stephanie", "White", "C", "2019-12-01", "F", "963 Willow Rd", "Chicago", "IL", "60603"},
            {"Matthew", "Harris", "B", "2017-06-15", "M", "159 Hickory Ln", "Los Angeles", "CA", "90002"},
            {"Lauren", "Martin", "S", "2020-08-28", "F", "267 Sycamore Dr", "Boston", "MA", "02102"},
            {"Andrew", "Garcia", "F", "2019-04-10", "M", "378 Magnolia St", "San Francisco", "CA", "94102"},
            {"Rachel", "Martinez", "G", "2018-03-25", "F", "489 Dogwood Way", "Dallas", "TX", "75203"},
            {"Joshua", "Robinson", "H", "2021-06-01", "M", "591 Redwood Ct", "New York", "NY", "10005"},
            {"Nicole", "Clark", "I", "2017-10-18", "F", "602 Sequoia Pl", "Chicago", "IL", "60604"},
            {"Ryan", "Rodriguez", "W", "2020-01-07", "M", "713 Cypress Ave", "Los Angeles", "CA", "90003"},
            {"Megan", "Lewis", "V", "2019-09-22", "F", "824 Juniper Rd", "Boston", "MA", "02103"},
            {"Kevin", "Lee", "U", "2018-05-14", "M", "935 Palm Blvd", "San Francisco", "CA", "94103"},
            {"Ashley", "Walker", "Q", "2020-11-10", "F", "146 Olive Dr", "Dallas", "TX", "75204"},
            {"Brandon", "Hall", "X", "2019-02-28", "M", "257 Laurel St", "New York", "NY", "10006"},
            {"Samantha", "Allen", "Y", "2017-12-05", "F", "368 Hazel Way", "Chicago", "IL", "60605"},
            {"Tyler", "Young", "Z", "2021-04-15", "M", "479 Ivy Ct", "Los Angeles", "CA", "90004"},
            {"Brittany", "Hernandez", "A", "2018-07-20", "F", "581 Fern Place", "Boston", "MA", "02104"},
            {"Nathan", "King", "B", "2019-10-30", "M", "692 Moss Lane", "San Francisco", "CA", "94104"},
            {"Kayla", "Wright", "C", "2020-03-12", "F", "703 Sage Road", "Dallas", "TX", "75205"},
            {"Justin", "Lopez", "D", "2017-08-25", "M", "814 Thyme Ave", "New York", "NY", "10007"},
            {"Hannah", "Hill", "E", "2019-06-18", "F", "925 Basil Blvd", "Chicago", "IL", "60606"},
            {"Alexander", "Scott", "F", "2018-01-30", "M", "136 Mint Dr", "Los Angeles", "CA", "90005"},
            {"Victoria", "Green", "G", "2020-09-05", "F", "247 Dill St", "Boston", "MA", "02105"},
            {"Jacob", "Adams", "H", "2019-05-22", "M", "358 Clove Way", "San Francisco", "CA", "94105"},
            {"Olivia", "Baker", "I", "2021-02-14", "F", "469 Anise Ct", "Dallas", "TX", "75206"},
            {"Ethan", "Gonzalez", "J", "2017-11-08", "M", "571 Cumin Pl", "New York", "NY", "10008"},
            {"Sophia", "Nelson", "K", "2018-04-15", "F", "682 Coriander Ave", "Chicago", "IL", "60607"},
            {"Logan", "Carter", "L", "2020-07-20", "M", "793 Turmeric Rd", "Los Angeles", "CA", "90006"},
            {"Isabella", "Perez", "M", "2019-03-05", "F", "804 Paprika Blvd", "Boston", "MA", "02106"},
            {"William", "Roberts", "N", "2018-10-25", "M", "915 Oregano Dr", "San Francisco", "CA", "94106"},
            {"Abigail", "Turner", "O", "2021-08-12", "F", "126 Parsley St", "Dallas", "TX", "75207"},
            {"Benjamin", "Phillips", "P", "2017-05-30", "M", "237 Chili Way", "New York", "NY", "10009"},
            {"Madison", "Campbell", "Q", "2019-11-18", "F", "348 Nutmeg Ct", "Chicago", "IL", "60608"},
            {"Mason", "Parker", "R", "2020-04-08", "M", "459 Ginger Pl", "Los Angeles", "CA", "90007"},
            {"Chloe", "Evans", "S", "2018-08-22", "F", "561 Vanilla Ave", "Boston", "MA", "02107"},
            {"Elijah", "Edwards", "T", "2019-01-15", "M", "672 Saffron Rd", "San Francisco", "CA", "94107"},
            {"Grace", "Collins", "U", "2021-05-28", "F", "783 Cardamom Blvd", "Dallas", "TX", "75208"},
            {"Lucas", "Stewart", "V", "2017-09-10", "M", "894 Fennel Dr", "New York", "NY", "10010"},
            {"Lily", "Sanchez", "W", "2020-12-02", "F", "105 Rosemary St", "Chicago", "IL", "60609"},
            {"Henry", "Morris", "X", "2018-02-18", "M", "216 Tarragon Way", "Los Angeles", "CA", "90008"},
            {"Zoe", "Rogers", "Y", "2019-07-25", "F", "327 Chive Ct", "Boston", "MA", "02108"},
            {"Jack", "Reed", "Z", "2020-10-15", "M", "438 Bay Leaf Pl", "San Francisco", "CA", "94108"},
            {"Natalie", "Cook", "A", "2017-03-08", "F", "549 Celery Ave", "Dallas", "TX", "75209"},
            {"Owen", "Morgan", "B", "2019-08-20", "M", "651 Leek Road", "New York", "NY", "10011"},
            {"Audrey", "Bell", "C", "2021-03-15", "F", "762 Endive Blvd", "Chicago", "IL", "60610"},
            {"Sebastian", "Murphy", "D", "2018-06-28", "M", "873 Arugula Dr", "Los Angeles", "CA", "90009"},
            {"Penelope", "Bailey", "E", "2020-01-18", "F", "984 Kale St", "Boston", "MA", "02109"},
            {"Gabriel", "Rivera", "F", "2017-07-05", "M", "195 Spinach Way", "San Francisco", "CA", "94109"},
            {"Aria", "Cooper", "G", "2019-12-10", "F", "206 Lettuce Ct", "Dallas", "TX", "75210"},
            {"Carter", "Richardson", "H", "2020-06-22", "M", "317 Cabbage Pl", "New York", "NY", "10012"},
            {"Scarlett", "Cox", "I", "2018-09-30", "F", "428 Broccoli Ave", "Chicago", "IL", "60611"},
            {"Jayden", "Howard", "J", "2021-07-14", "M", "539 Carrot Rd", "Los Angeles", "CA", "90010"},
            {"Eleanor", "Ward", "K", "2017-02-20", "F", "641 Beet Blvd", "Boston", "MA", "02110"},
            {"Levi", "Torres", "L", "2019-04-25", "M", "752 Turnip Dr", "San Francisco", "CA", "94110"},
            {"Hazel", "Peterson", "M", "2020-08-08", "F", "863 Radish St", "Dallas", "TX", "75211"},
            {"Isaac", "Gray", "N", "2018-12-15", "M", "974 Squash Way", "New York", "NY", "10013"},
            {"Violet", "Ramirez", "O", "2019-06-30", "F", "185 Pumpkin Ct", "Chicago", "IL", "60612"},
            {"Nolan", "James", "P", "2021-09-05", "M", "296 Melon Pl", "Los Angeles", "CA", "90011"},
            {"Stella", "Watson", "Q", "2017-04-18", "F", "307 Berry Ave", "Boston", "MA", "02111"},
            {"Caleb", "Brooks", "R", "2020-02-25", "M", "418 Grape Rd", "San Francisco", "CA", "94111"},
            {"Aurora", "Kelly", "S", "2018-07-10", "F", "529 Apple Blvd", "Dallas", "TX", "75212"},
            {"Leo", "Sanders", "T", "2019-11-22", "M", "631 Peach Dr", "New York", "NY", "10014"},
            {"Savannah", "Price", "U", "2021-01-08", "F", "742 Plum St", "Chicago", "IL", "60613"},
            {"Adam", "Bennett", "V", "2017-06-25", "M", "853 Fig Way", "Los Angeles", "CA", "90012"},
            {"Bella", "Wood", "W", "2020-05-12", "F", "964 Date Ct", "Boston", "MA", "02112"},
            {"Luke", "Barnes", "X", "2018-10-30", "M", "175 Mango Pl", "San Francisco", "CA", "94112"},
            {"Lucy", "Ross", "Y", "2019-03-15", "F", "286 Papaya Ave", "Dallas", "TX", "75213"},
            {"Hunter", "Henderson", "Z", "2020-09-20", "M", "397 Guava Rd", "New York", "NY", "10015"},
            {"Paisley", "Coleman", "A", "2017-11-05", "F", "408 Lychee Blvd", "Chicago", "IL", "60614"},
            {"Adrian", "Jenkins", "B", "2019-02-18", "M", "519 Kiwi Dr", "Los Angeles", "CA", "90013"},
            {"Skylar", "Perry", "C", "2021-06-28", "F", "621 Coconut St", "Boston", "MA", "02113"},
            {"Thomas", "Powell", "D", "2018-05-10", "M", "732 Lime Way", "San Francisco", "CA", "94113"},
            {"Ellie", "Long", "E", "2020-11-25", "F", "843 Lemon Ct", "Dallas", "TX", "75214"},
            {"Christian", "Patterson", "F", "2017-08-15", "M", "954 Orange Pl", "New York", "NY", "10016"},
            {"Naomi", "Hughes", "G", "2019-10-08", "F", "165 Cherry Ave", "Chicago", "IL", "60615"},
            {"Jonathan", "Flores", "H", "2020-03-22", "M", "276 Banana Rd", "Los Angeles", "CA", "90014"},
            {"Ruby", "Washington", "I", "2018-01-15", "F", "387 Pineapple Blvd", "Boston", "MA", "02114"},
            {"Aaron", "Butler", "J", "2021-04-30", "M", "498 Avocado Dr", "San Francisco", "CA", "94114"},
            {"Leah", "Simmons", "K", "2019-09-12", "F", "509 Olive St", "Dallas", "TX", "75215"},
            {"Charles", "Foster", "L", "2017-12-28", "M", "611 Almond Way", "New York", "NY", "10017"},
            {"Addison", "Gonzales", "M", "2020-07-15", "F", "722 Walnut Ct", "Chicago", "IL", "60616"},
            {"Eli", "Bryant", "N", "2018-04-08", "M", "833 Pecan Pl", "Los Angeles", "CA", "90015"},
            {"Aubrey", "Alexander", "O", "2019-12-20", "F", "944 Cashew Ave", "Boston", "MA", "02115"},
            {"Hudson", "Russell", "P", "2021-03-05", "M", "155 Hazelnut Rd", "San Francisco", "CA", "94115"},
            {"Claire", "Griffin", "Q", "2020-06-18", "F", "266 Pistachio Blvd", "Dallas", "TX", "75216"},
            {"Lincoln", "Diaz", "R", "2017-10-25", "M", "377 Macadamia Dr", "New York", "NY", "10018"},
            {"Sadie", "Hayes", "S", "2019-05-10", "F", "488 Chestnut St", "Chicago", "IL", "60617"},
            {"Miles", "Myers", "T", "2020-12-30", "M", "599 Peanut Way", "Los Angeles", "CA", "90016"},
            {"Willow", "Ford", "U", "2018-03-15", "F", "601 Acorn Ct", "Boston", "MA", "02116"},
            {"Dominic", "Hamilton", "V", "2019-08-28", "M", "712 Pinecone Pl", "San Francisco", "CA", "94116"},
            {"Piper", "Graham", "W", "2021-11-05", "F", "823 Birdseed Ave", "Dallas", "TX", "75217"},
            {"Jaxon", "Sullivan", "X", "2017-01-20", "M", "934 Sunflower Rd", "New York", "NY", "10019"},
            {"Maya", "Wallace", "Y", "2020-04-12", "F", "145 Daisy Blvd", "Chicago", "IL", "60618"},
            {"Grayson", "West", "Z", "2018-11-08", "M", "256 Tulip Dr", "Los Angeles", "CA", "90017"},
            {"Elena", "Cole", "A", "2019-06-22", "F", "367 Rose St", "Boston", "MA", "02117"},
            {"Ezra", "Hunt", "B", "2021-02-14", "M", "478 Lily Way", "San Francisco", "CA", "94117"},
            {"Autumn", "Stone", "C", "2020-08-28", "F", "589 Iris Ct", "Dallas", "TX", "75218"},
            {"Colton", "Dean", "D", "2017-05-15", "M", "691 Orchid Pl", "New York", "NY", "10020"},
            {"Emery", "Hart", "E", "2019-01-08", "F", "702 Violet Ave", "Chicago", "IL", "60619"},
            {"Kai", "Burke", "F", "2020-10-20", "M", "813 Jasmine Rd", "Los Angeles", "CA", "90018"},
            {"Ivy", "Mason", "G", "2018-06-02", "F", "924 Peony Blvd", "Boston", "MA", "02118"},
            {"Xavier", "Dixon", "H", "2019-11-15", "M", "135 Dahlia Dr", "San Francisco", "CA", "94118"},
            {"Clara", "Hunt", "I", "2021-07-22", "F", "246 Azalea St", "Dallas", "TX", "75219"},
            {"Micah", "Webb", "J", "2017-09-30", "M", "357 Camellia Way", "New York", "NY", "10021"},
            {"Athena", "Simpson", "K", "2020-01-25", "F", "468 Begonia Ct", "Chicago", "IL", "60620"},
            {"Cooper", "Stevens", "L", "2018-08-12", "M", "579 Zinnia Pl", "Los Angeles", "CA", "90019"},
            {"Kennedy", "Tucker", "M", "2019-04-05", "F", "681 Pansy Ave", "Boston", "MA", "02119"},
            {"Maxwell", "Porter", "N", "2021-10-18", "M", "792 Poppy Rd", "San Francisco", "CA", "94119"},
            {"Madeline", "Hunter", "O", "2020-05-30", "F", "803 Heather Blvd", "Dallas", "TX", "75220"}
        };

        Random rand = new Random(42);
        int count = 0;
        for (String[] e : employees) {
            int posIdx = rand.nextInt(posRows.size());
            Map<String, Object> pos = posRows.get(posIdx);
            int posId = ((Number) pos.get("POSID")).intValue();
            int deptId = ((Number) pos.get("DEPTID")).intValue();
            String email = e[0].toLowerCase() + "." + e[1].toLowerCase() + "@company.com";
            int birthYear = 1965 + rand.nextInt(30);
            int birthMonth = 1 + rand.nextInt(12);
            int birthDay = 1 + rand.nextInt(28);
            String dob = String.format("%04d-%02d-%02d", birthYear, birthMonth, birthDay);

            jdbc.execute("INSERT INTO NGIRI4001.EMPLOYEES (FIRSTNAME, LASTNAME, MIDDLENAME, EMAIL, HIREDATE, DEPTID, POSID, EMPSTATUS, ADDR1, CITY, STATE, ZIPCODE, COUNTRY, DOB, GENDER) VALUES ('" +
                e[0] + "', '" + e[1] + "', '" + e[2] + "', '" + email + "', '" + e[3] + "', " +
                deptId + ", " + posId + ", 'ACTIVE', '" + e[5].replace("'", "''") + "', '" + e[6] + "', '" + e[7] + "', '" + e[8] + "', 'USA', '" + dob + "', '" + e[4] + "')");
            count++;
        }
        log.add("Inserted " + count + " employees");
    }

    private void seedPhones(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPPHONENB", Integer.class);
        if (existing > 0) { log.add("Phones already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        Random rand = new Random(42);
        int count = 0;
        for (int empId : empIds) {
            String mobile = String.format("(%03d) %03d-%04d", 200 + rand.nextInt(800), rand.nextInt(1000), rand.nextInt(10000));
            jdbc.execute("INSERT INTO NGIRI4001.EMPPHONENB (EMPID, PHONETYPE, PHONENUM, ISPRIMARY) VALUES (" + empId + ", 'MOBILE', '" + mobile + "', 'Y')");
            count++;
            if (rand.nextInt(100) < 80) {
                String secType = rand.nextBoolean() ? "HOME" : "WORK";
                String sec = String.format("(%03d) %03d-%04d", 200 + rand.nextInt(800), rand.nextInt(1000), rand.nextInt(10000));
                jdbc.execute("INSERT INTO NGIRI4001.EMPPHONENB (EMPID, PHONETYPE, PHONENUM, ISPRIMARY) VALUES (" + empId + ", '" + secType + "', '" + sec + "', 'N')");
                count++;
            }
        }
        log.add("Inserted " + count + " phone records");
    }

    private void seedSalaries(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.SALARIES", Integer.class);
        if (existing > 0) { log.add("Salaries already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        Random rand = new Random(42);
        String[] reasons = {"PROMOTION", "MERIT", "ADJUST"};
        int count = 0;
        for (int empId : empIds) {
            double baseSalary = 40000 + rand.nextInt(100000);
            double bonus = rand.nextInt(10000);
            int hireYear = 2016 + rand.nextInt(6);
            String hireDate = hireYear + "-01-15";
            String endDate = (hireYear + 1) + "-12-31";

            jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID, BASESALARY, BONUS, CURRENCY, PAYFREQ, EFFDATE, ENDDATE, ISCURRENT, REASON) VALUES (" +
                empId + ", " + String.format("%.2f", baseSalary) + ", " + String.format("%.2f", bonus) + ", 'USD', 'ANNUAL', '" + hireDate + "', '" + endDate + "', 'N', 'HIRE')");
            count++;

            double newSalary = baseSalary * (1.05 + rand.nextDouble() * 0.15);
            double newBonus = bonus * 1.1;
            String effDate = (hireYear + 2) + "-01-01";
            String reason = reasons[rand.nextInt(reasons.length)];

            jdbc.execute("INSERT INTO NGIRI4001.SALARIES (EMPID, BASESALARY, BONUS, CURRENCY, PAYFREQ, EFFDATE, ISCURRENT, REASON) VALUES (" +
                empId + ", " + String.format("%.2f", newSalary) + ", " + String.format("%.2f", newBonus) + ", 'USD', 'ANNUAL', '" + effDate + "', 'Y', '" + reason + "')");
            count++;
        }
        log.add("Inserted " + count + " salary records");
    }

    private void seedDependents(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.DEPENDENTS", Integer.class);
        if (existing > 0) { log.add("Dependents already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        Random rand = new Random(42);
        String[] firstNames = {"Alex", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Quinn", "Harper", "Avery", "Parker",
            "Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan", "Sophia", "Mason", "Mia", "Lucas"};
        String[] lastNames = {"Anderson", "Mitchell", "Thompson", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
        String[] relations = {"SPOUSE", "CHILD", "CHILD", "CHILD", "PARENT"};
        int count = 0;
        for (int empId : empIds) {
            int numDeps = rand.nextInt(4);
            for (int d = 0; d < numDeps; d++) {
                String depName = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
                String relation = relations[rand.nextInt(relations.length)];
                String gender = rand.nextBoolean() ? "M" : "F";
                int depYear = 1970 + rand.nextInt(40);
                String depDob = String.format("%04d-%02d-%02d", depYear, 1 + rand.nextInt(12), 1 + rand.nextInt(28));

                jdbc.execute("INSERT INTO NGIRI4001.DEPENDENTS (EMPID, DEPNAME, RELATION, DOB, GENDER) VALUES (" +
                    empId + ", '" + depName.replace("'", "''") + "', '" + relation + "', '" + depDob + "', '" + gender + "')");
                count++;
            }
        }
        log.add("Inserted " + count + " dependent records");
    }

    private void seedEmpHist(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.EMPHIST", Integer.class);
        if (existing > 0) { log.add("EmpHist already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        List<Map<String, Object>> posRows = jdbc.queryForList("SELECT POSID, DEPTID FROM NGIRI4001.POSITIONS ORDER BY POSID");
        Random rand = new Random(42);
        int count = 0;

        String[] hireDates = {"2019-03-15", "2018-06-20", "2017-01-10", "2020-02-14", "2019-08-05",
            "2018-11-30", "2016-04-22", "2021-01-15", "2019-07-08", "2020-05-20"};

        for (int i = 0; i < empIds.size(); i++) {
            int empId = empIds.get(i);
            int posIdx = rand.nextInt(posRows.size());
            Map<String, Object> pos = posRows.get(posIdx);
            int newPosId = ((Number) pos.get("POSID")).intValue();
            int newDeptId = ((Number) pos.get("DEPTID")).intValue();
            String hireDate = hireDates[i % hireDates.length];

            jdbc.execute("INSERT INTO NGIRI4001.EMPHIST (EMPID, EFFDATE, CHANGETYPE, NEWDEPTID, NEWPOSID, NEWSALARY, NOTES) VALUES (" +
                empId + ", '" + hireDate + "', 'HIRE', " + newDeptId + ", " + newPosId + ", " +
                (40000 + rand.nextInt(80000)) + ", 'Initial hire')");
            count++;
        }

        String[] changeTypes = {"PROMOTION", "TRANSFER", "TITLE_CHG"};
        for (int i = 0; i < 30 && i < empIds.size(); i++) {
            int empId = empIds.get(i);
            int posIdx = rand.nextInt(posRows.size());
            Map<String, Object> pos = posRows.get(posIdx);
            int newPosId = ((Number) pos.get("POSID")).intValue();
            int newDeptId = ((Number) pos.get("DEPTID")).intValue();

            jdbc.execute("INSERT INTO NGIRI4001.EMPHIST (EMPID, EFFDATE, CHANGETYPE, NEWDEPTID, NEWPOSID, NEWSALARY, NOTES) VALUES (" +
                empId + ", '2023-06-01', '" + changeTypes[rand.nextInt(changeTypes.length)] + "', " +
                newDeptId + ", " + newPosId + ", " + (60000 + rand.nextInt(80000)) + ", 'Career progression')");
            count++;
        }
        log.add("Inserted " + count + " employment history records");
    }

    private void seedAttendance(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.ATTENDANCE", Integer.class);
        if (existing > 0) { log.add("Attendance already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        Random rand = new Random(42);
        String[] statuses = {"PRESENT", "PRESENT", "PRESENT", "PRESENT", "WFH", "HALFDAY", "ABSENT"};
        String[] workDates = {"2026-02-03", "2026-02-04", "2026-02-05", "2026-02-06", "2026-02-07",
                              "2026-02-10", "2026-02-11"};
        int count = 0;
        for (int empId : empIds) {
            for (String wd : workDates) {
                String status = statuses[rand.nextInt(statuses.length)];
                double hrs = status.equals("PRESENT") ? 8.0 : (status.equals("HALFDAY") ? 4.0 : (status.equals("WFH") ? 8.0 : 0));
                double ot = (status.equals("PRESENT") && rand.nextInt(100) < 20) ? (1 + rand.nextInt(3)) : 0;
                int clockInHr = 7 + rand.nextInt(3);
                int clockInMin = rand.nextInt(60);
                String clockIn = status.equals("ABSENT") ? "NULL" : String.format("'%02d:%02d:00'", clockInHr, clockInMin);
                String clockOut = status.equals("ABSENT") ? "NULL" : String.format("'%02d:%02d:00'", clockInHr + (int) hrs, clockInMin);

                jdbc.execute("INSERT INTO NGIRI4001.ATTENDANCE (EMPID, WORKDATE, CLOCKIN, CLOCKOUT, HRSWORKED, OTHRS, STATUS) VALUES (" +
                    empId + ", '" + wd + "', " + clockIn + ", " + clockOut + ", " + hrs + ", " + ot + ", '" + status + "')");
                count++;
            }
        }
        log.add("Inserted " + count + " attendance records");
    }

    private void seedLeaveReqs(List<String> log) {
        int existing = jdbc.queryForObject("SELECT COUNT(*) FROM NGIRI4001.LEAVEREQS", Integer.class);
        if (existing > 0) { log.add("LeaveReqs already exist (" + existing + "), skipping"); return; }

        List<Integer> empIds = jdbc.queryForList("SELECT EMPID FROM NGIRI4001.EMPLOYEES ORDER BY EMPID", Integer.class);
        List<Integer> lvTypeIds = jdbc.queryForList("SELECT LVTYPEID FROM NGIRI4001.LEAVETYPES ORDER BY LVTYPEID", Integer.class);
        if (lvTypeIds.isEmpty()) { log.add("No leave types found, skipping leave requests"); return; }

        Random rand = new Random(42);
        String[] lvStatuses = {"PENDING", "APPROVED", "APPROVED", "APPROVED", "REJECTED", "CANCELLED"};
        int count = 0;
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
                String approvedAt = approver.equals("NULL") ? "NULL" : "CURRENT_TIMESTAMP";

                jdbc.execute("INSERT INTO NGIRI4001.LEAVEREQS (EMPID, LVTYPEID, STARTDATE, ENDDATE, TOTALDAYS, REASON, STATUS, APPROVERID, APPROVEDAT) VALUES (" +
                    empId + ", " + lvTypeId + ", '" + startDate + "', '" + endDate + "', " + totalDays + ", 'Personal reasons', '" +
                    status + "', " + approver + ", " + approvedAt + ")");
                count++;
            }
        }
        log.add("Inserted " + count + " leave request records");
    }
}
