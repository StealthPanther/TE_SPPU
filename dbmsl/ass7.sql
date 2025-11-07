CREATE TABLE Lib(
 bookId INT PRIMARY KEY,
 bookName VARCHAR(100),
 author VARCHAR(100)
);

CREATE TABLE Library_Audit(
 auditId INT AUTO_INCREMENT PRIMARY KEY,
 bookId INT,
 bookName VARCHAR(100),
 author VARCHAR(100),
 actionType VARCHAR(10),
 actionDate DATETIME
);

DELIMITER $$

CREATE TRIGGER trg_Insert_Library
BEFORE INSERT ON Lib
FOR EACH ROW
BEGIN
 INSERT INTO Library_Audit
 (bookId, bookName, author, actionType, actionDate)
 VALUES (NEW.bookId, NEW.bookName, NEW.author, 'INSERT', NOW());
END$$

DELIMITER ;




DELIMITER $$

CREATE TRIGGER trg_Update_Library
BEFORE UPDATE ON Lib
FOR EACH ROW
BEGIN
 INSERT INTO Library_Audit
 (bookId, bookName, author, actionType, actionDate)
 VALUES (OLD.bookId, OLD.bookName, OLD.author, 'UPDATE', NOW());
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_Delete_Library
BEFORE DELETE ON Lib
FOR EACH ROW
BEGIN
 INSERT INTO Library_Audit
 (bookId, bookName, author, actionType, actionDate)
 VALUES (OLD.bookId, OLD.bookName, OLD.author, 'DELETE', NOW());
END$$

DELIMITER ;

INSERT INTO Lib VALUES (1,'DBMS','Navathe'),
(2,'Data Structures','Schaum'),
(3,'Algorithms','Cormen');

UPDATE Lib SET bookName='DBMS 2' WHERE bookId=1;

DELETE FROM Lib WHERE bookId=2;

SELECT * FROM Library_Audit;
