CREATE TABLE Stud_Marks (
    name VARCHAR(50),
    total_marks INT
);

CREATE TABLE Result (
    roll INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    class VARCHAR(30)
);

INSERT INTO Stud_Marks VALUES
('Amit', 1200),
('Riya', 950),
('Sahil', 880),
('Neha', 700);



DELIMITER $$

CREATE PROCEDURE proc_Grade(IN p_name VARCHAR(50), IN p_marks INT)
BEGIN
    DECLARE v_class VARCHAR(30);

    -- Error Handler: if any error occurs, show message
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SELECT 'Error occurred while inserting result!' AS ErrorMessage;
    END;

    -- Grade Logic
    IF p_marks BETWEEN 990 AND 1500 THEN
        SET v_class = 'Distinction';
    ELSEIF p_marks BETWEEN 900 AND 989 THEN
        SET v_class = 'First Class';
    ELSEIF p_marks BETWEEN 825 AND 899 THEN
        SET v_class = 'Higher Second Class';
    ELSE
        SET v_class = 'Other / Fail';
    END IF;

    -- Insert result
    INSERT INTO Result(name, class)
    VALUES(p_name, v_class);
END $$

DELIMITER ;





CALL proc_Grade('Amit', 1200);
CALL proc_Grade('Riya', 950);
CALL proc_Grade('Sahil', 880);
CALL proc_Grade('Neha', 700);

SELECT * FROM Result;
