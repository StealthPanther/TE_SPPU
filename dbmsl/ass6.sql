
DROP TABLE IF EXISTS O_EmpId;
DROP TABLE IF EXISTS N_EmpId;


CREATE TABLE O_EmpId(
    Empid INT PRIMARY KEY,
    Empname  VARCHAR(100)
);

CREATE TABLE N_EmpId(
    Empid INT PRIMARY KEY,
    Empname  VARCHAR(100)
);

INSERT INTO O_EmpId(EmpId,EmpName) VALUES
(101,'Arjun'),
(102,'Aman'),
(103,'Shubham'),
(104,'Raju'),
(106,'Bob');

INSERT INTO N_EmpId(EmpId,EmpName) VALUES
(101,'Arjun'),
(102,'Harsh'),
(103,'Shubham'),
(104,'Bhavesh'),
(105,'John');

DELIMITER $$

CREATE PROCEDURE Merge_EMP()

BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE v_empid INT;
  DECLARE v_empname VARCHAR(100);

  DECLARE cur CURSOR FOR SELECT EmpId,EmpName FROM N_EmpId;
  

 
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  
  DECLARE EXIt HANDLER FOR SQLEXCEPTION
  BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error during merge operation';
  END;
 
  OPEN cur;
  
  -- Loop to fetch each row one by one
  read_loop: LOOP
    FETCH cur INTO v_empid, v_empname;
    IF done THEN
      LEAVE read_loop;  -- Exit loop if no more rows
    END IF;

    IF NOT EXISTS (SELECT 1 FROM O_EmpId WHERE EmpId = v_empid) THEN
      
      INSERT INTO O_EmpId (EmpId, EmpName) VALUES (v_empid, v_empname);
    END IF;

  END LOOP;

 
  CLOSE cur;
END $$

DELIMITER ;


CALL Merge_EMP();
    
select* from O_EmpId;