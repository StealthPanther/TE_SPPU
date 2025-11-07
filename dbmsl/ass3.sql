-- Assignment 3

-- Create Department table
CREATE TABLE Department (
 deptId INT PRIMARY KEY,
 deptName VARCHAR(100) NOT NULL,
 deptLocation VARCHAR(100)
);

-- Create Employee table
CREATE TABLE Employee (
empId INT PRIMARY KEY,
deptId INT,
empFName VARCHAR(100) NOT NULL, -- Employee first name 
empPosition VARCHAR(100),        -- Job position of employee
empSalary BIGINT NOT NULL DEFAULT 0,
empJoinDate DATE NOT NULL DEFAULT (CURRENT_DATE), -- Joining date (default: today's date)
FOREIGN KEY (deptId) REFERENCES Department(deptId) ON DELETE CASCADE
);

-- Create Project table
CREATE TABLE Project(
projId INT PRIMARY KEY,
deptId INT,
projName VARCHAR(100) NOT NULL,
projLocation VARCHAR(100),
projCost BIGINT NOT NULL DEFAULT 0,
projYear DATE NOT NULL DEFAULT (CURRENT_DATE),
FOREIGN KEY (deptId) REFERENCES Department(deptId) ON DELETE CASCADE
);

-- Insert records into Department table
INSERT INTO Department (deptId, deptName, deptLocation) VALUES
(101, 'Computer', 'New York'),
(102, 'IT', 'London'),
(103, 'HR', 'Paris'),
(104, 'Finance', 'Tokyo'),
(105, 'Marketing', 'Sydney'),
(106, 'Operations', 'Berlin'),
(107, 'Research', 'San Francisco'),
(108, 'Sales', 'Dubai'),
(109, 'Legal', 'Rome'),
(110, 'Admin', 'Madrid');

-- Insert records into Employee table
INSERT INTO Employee (empId, deptId, empFName, empPosition, empSalary, empJoinDate) VALUES
(1, 101, 'Prateek', 'Software Engineer', 75000, '2000-03-15'),
(2, 101, 'Harsh', 'Data Scientist', 85000, '2002-07-22'),
(3, 102, 'Priya', 'Network Administrator', 60000, '2005-11-01'),
(4, 102, 'Rahul', 'IT Support', 45000, '2008-01-10'),
(5, 103, 'Heena', 'HR Manager', 90000, '1983-05-20'),
(6, 103, 'Preeti', 'HR Assistant', 40000, '2010-09-01'),
(7, 104, 'Parth', 'Financial Analyst', 70000, '1980-02-28'),
(8, 104, 'Hitesh', 'Accountant', 55000, '2012-04-12'),
(9, 105, 'Pooja', 'Marketing Specialist', 65000, '2015-06-01'),
(10, 105, 'Himanshu', 'Marketing Manager', 95000, '2018-08-01'),
(11, 101, 'Peter', 'Project Manager', 100000, '1984-11-11'),
(12, 102, 'Henry', 'System Analyst', 70000, '1990-03-25');

-- Insert records into Project table
INSERT INTO Project (projId, deptId, projName, projLocation, projCost, projYear) VALUES
(1001, 101, 'Website Redesign', 'Pune', 150000, '2020-01-01'),
(1002, 101, 'Database Migration', 'Bangalore', 300000, '2005-06-15'),
(1003, 102, 'Network Upgrade', 'Pune', 80000, '2006-03-20'),
(1004, 102, 'Security Audit', 'Hyderabad', 120000, '2007-09-10'),
(1005, 103, 'Recruitment Drive', 'Mumbai', 50000, '2020-02-01'),
(1006, 104, 'Annual Budgeting', 'Delhi', 200000, '2009-04-01'),
(1007, 105, 'New Product Launch', 'Pune', 450000, '2010-07-01'),
(1008, 101, 'Cloud Infrastructure', 'Chennai', 600000, '2011-01-01'),
(1009, 102, 'Software Development', 'Pune', 250000, '2004-11-01'),
(1010, 103, 'Employee Training', 'Kolkata', 75000, '2005-05-01'),
(1011, 101, 'Mobile App Development', 'Pune', 180000, '2007-03-01'),
(1012, 104, 'Financial Reporting System', 'Ahmedabad', 350000, '2020-08-01');

-- Answers

-- [1]. Find Employee details and Department details using NATURAL JOIN.
SELECT * FROM Employee NATURAL JOIN Department;

-- [2]. Find the emp_fname,Emp_position,location,Emp_JoinDate who have same Dept id.
SELECT e.deptId, e.empFName, e.empPosition, d.deptLocation, e.empJoinDate
FROM Employee e
JOIN Department d ON e.deptId = d.deptId
ORDER BY e.deptId;

-- [3]. Employee details + proj_id + cost where project location is not Hyderabad
SELECT * 
FROM Employee e 
JOIN Project p ON e.deptId = p.deptId
WHERE p.projLocation != 'Hyderabad' 
ORDER BY p.projId;

-- [4]. Dept name ,employee name, Emp_position for which project year is 2020
SELECT d.deptName, e.empFName, e.empPosition, YEAR(p.projYear), p.projName AS ProjectYear
FROM Project p
JOIN Department d ON p.deptId = d.deptId
JOIN Employee e ON e.deptId = d.deptId
WHERE YEAR(p.projYear) = 2020;

-- [5]. emp_position,D_name who have Project cost >30000
SELECT e.empPosition, d.deptName, p.projCost 
FROM Employee e
JOIN Department d ON e.deptId = d.deptId
JOIN Project p ON p.deptId = d.deptId 
WHERE p.projCost > 30000;

-- [6]. Projects started in 2015
SELECT p.projName AS Project_Name 
FROM Project p 
WHERE YEAR(p.projYear) = 2015;

-- [7]. Dept_name having no_of_emp = 10
SELECT d.deptName, COUNT(e.empId) 
FROM Employee e 
JOIN Department d ON e.deptId = d.deptId 
GROUP BY d.deptName 
HAVING COUNT(e.empID) = 10;

-- [8]. total employee who joined any project before 2009
SELECT e.empFName, p.projName, e.empJoinDate, p.projYear 
FROM Employee e
JOIN Department d ON e.deptId = d.deptId
JOIN Project p ON p.deptId = d.deptId 
WHERE e.empJoinDate < p.projYear AND YEAR(p.projYear) < 2009;

-- [9]. Create a view showing employee & department details
CREATE VIEW emp_and_depart_data AS
SELECT 
 e.empId,
 e.empFName,
 e.deptId AS empDeptId,
 d.deptId AS deptDeptId,
 d.deptName
FROM Employee e
JOIN Department d ON e.deptId = d.deptId;

-- [10]. drop view
DROP VIEW IF EXISTS emp_and_depart_data;
