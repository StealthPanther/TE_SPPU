--Write a MySQL block to accept Roll no & Book Name, calculate fine based on number of days from date of issue.
--If days are 15–30 → ₹5/day fine
---If days > 30 → ₹50/day fine
--After submitting book → change status from ‘I’ to ‘R’.
--If fine applies → insert record in Fine table.

--Write a MySQL block to accept Roll no & Book Name, calculate fine based on number of days from date of issue.
--If days are 15–30 → ₹5/day fine
---If days > 30 → ₹50/day fine
--After submitting book → change status from ‘I’ to ‘R’.
--If fine applies → insert record in Fine table.

create table borrower(
    roll int,
    bookname varchar(30),
    dateofissue DATE,
    status char(1)
);

create table fine(
    roll int,
    Date DATE,
    amt int
);
DELIMITER $$
create procedure calcfine(IN p_roll int, IN p_bookname varchar(30))
BEGIN
   declare v_issue_date date;
   declare v_days int;
    declare v_fine int default 0;

    declare continue handler for not found
    BEGIN
    select 'No matching record found!' as Message;
    END;
   
    select dateofissue into v_issue_date from borrower where roll = p_roll and bookname = p_bookname and status = 'I';

    set v_days = datediff(curdate(), v_issue_date);

    if v_days >30 then set v_fine = v_days * 50;
    elseif v_days >=15 then set v_fine = v_days * 5;
    else set v_fine = 0;
    end if;

    update borrower set status ='R' where roll = p_roll and bookname = p_bookname;

    insert into fine(roll, Date, amt) values(p_roll, curdate(), v_fine);

    select 'Book Returned Successfully!' as Message,
           v_days ,
           v_fine ;

END$$
DELIMITER ;

insert into borrower values
(101, 'DBMS', '2024-10-01', 'I'),
(102, 'DSA', '2024-09-20', 'I'),
(103, 'Python', '2024-10-10', 'I');

call calcfine(101, 'DBMS');

select * from borrower;
select * from fine;


=====================================================================================


-- Create Borrower Table
CREATE TABLE Borrower (
    Roll INT,
    Name VARCHAR(100),
    DateofIssue DATE,
    NameofBook VARCHAR(100),
    Status CHAR(1) -- I = Issued, R = Returned
);

-- Create Fine Table
CREATE TABLE Fine (
    Roll INT ,
    Date DATE,
    Amt INT
);


INSERT INTO Borrower VALUES
(101, 'Prateek', '2024-10-01', 'DBMS', 'I'),
(102, 'Asha', '2024-09-20', 'DSA', 'I'),
(103, 'Rohan', '2024-10-10', 'Python', 'I');



DELIMITER $$

CREATE PROCEDURE return_book(IN p_roll INT, IN p_book VARCHAR(100))
BEGIN
    DECLARE v_issue_date DATE;
    DECLARE v_days INT;
    DECLARE v_fine INT DEFAULT 0;

    -- If no record found
    DECLARE CONTINUE HANDLER FOR NOT FOUND 
    SELECT 'No matching record found!' AS Message;

    -- Get date of issue
    SELECT DateofIssue INTO v_issue_date
    FROM Borrower
    WHERE Roll = p_roll AND NameofBook = p_book AND Status = 'I';

    -- Calculate total days
    SET v_days = DATEDIFF(CURDATE(), v_issue_date);

    -- Fine calculation
    IF v_days > 30 THEN
        SET v_fine = v_days * 50;
    ELSEIF v_days >= 15 THEN
        SET v_fine = v_days * 5;
    ELSE
        SET v_fine = 0;
    END IF;

    -- Update status to Returned
    UPDATE Borrower
    SET Status = 'R'
    WHERE Roll = p_roll AND NameofBook = p_book;

    -- Insert fine if applicable
    IF v_fine > 0 THEN
        INSERT INTO Fine(Roll, Date, Amt)
        VALUES(p_roll, CURDATE(), v_fine);
    END IF;

    -- Show result
    SELECT 'Book Returned Successfully!' AS Message,
           v_days AS DaysDelayed,
           v_fine AS FineAmount;

END$$

DELIMITER ;


CALL return_book(101, 'DBMS');

SELECT * FROM Borrower;
SELECT * FROM Fine;
