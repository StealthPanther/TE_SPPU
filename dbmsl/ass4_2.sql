
DROP TABLE IF EXISTS Areas;


CREATE TABLE Areas (
    radius INT PRIMARY KEY,
    area DOUBLE NOT NULL
);

DELIMITER $$

CREATE PROCEDURE calcAreaSimpleNoLog(IN startRadius INT, IN endRadius INT)
BEGIN
    DECLARE r INT;
    DECLARE areaVal DOUBLE;

    
    IF startRadius > endRadius THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Start radius must be less than or equal to end radius.';
    END IF;

    IF startRadius <= 0 OR endRadius <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Radius values must be positive.';
    END IF;

    SET r = startRadius;

    WHILE r <= endRadius DO
        SET areaVal = PI() * POW(r, 2);
        INSERT INTO Areas(radius, area) VALUES (r, areaVal);
        SET r = r + 1;
    END WHILE;
END$$

DELIMITER ;


CALL calcAreaSimpleNoLog(5, 9);



SELECT * FROM Areas;
