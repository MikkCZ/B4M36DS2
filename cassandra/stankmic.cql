CREATE KEYSPACE IF NOT EXISTS stankmic WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
USE stankmic;

DROP TABLE visitors;
CREATE TABLE visitors (
    id INT PRIMARY KEY,
    name TUPLE<TEXT,TEXT>,
    city TEXT,
    username TEXT,
    history LIST<TEXT>
);
CREATE INDEX index_visitors_name ON visitors (name);
CREATE INDEX index_visitors_username ON visitors (username);
CREATE INDEX index_visitors_history ON visitors (history);

INSERT INTO visitors (id, name, city, username) VALUES (1, ('Michal','Stanke'), 'Praha', 'stankmic');
INSERT INTO visitors (id, name, city, username) VALUES (2, ('John','Doe'), 'Mexico City', 'jdoe');
INSERT INTO visitors (id, name, city, username) VALUES (3, ('Alice',''), 'Praha', 'aluska');
INSERT INTO visitors (id, name, city, username) VALUES (4, ('Bob',''), 'Mexico City', 'bobby');
INSERT INTO visitors (id, name, city, username) VALUES (5, ('Bilbo','Baggins'), 'Hobbiton', 'mrbaggins');
INSERT INTO visitors (id, name, city, username) VALUES (6, ('Peregrin','Took'), 'Shire', 'pippin');

DROP TABLE parks;
CREATE TABLE parks (
    id INT PRIMARY KEY,
    name TEXT,
    year INT,
    visitors SET<INT>,
    amusements_last_visits MAP<TEXT, INT>
);
CREATE INDEX index_parks_year ON parks (year);

INSERT INTO parks (id, name, year) VALUES (1, 'Legoland Billund Resort', 1968);
INSERT INTO parks (id, name, year) VALUES (2, 'Legoland Windsor Resort', 1996);
INSERT INTO parks (id, name, year) VALUES (3, 'Legoland California', 1999);
INSERT INTO parks (id, name, year) VALUES (4, 'Legoland Deutschland Resort', 20002);
INSERT INTO parks (id, name, year) VALUES (5, 'Legoland Florida Resort', 2011);
INSERT INTO parks (id, name, year) VALUES (6, 'Legoland Malaysia Resort', 2012);
INSERT INTO parks (id, name, year) VALUES (7, 'Walt Disney World Resort', 1971);

UPDATE parks SET year=2002 WHERE id=4;
UPDATE parks SET visitors={200,12,6547} WHERE id=4;
UPDATE parks SET visitors={2} WHERE id=4;
UPDATE parks SET visitors=visitors+{1} WHERE id=4;
UPDATE parks SET visitors=visitors-{2} WHERE id=4;

UPDATE parks SET amusements_last_visits={'restaurant':-1, 'Pirate Boat':-1} WHERE id=4;

UPDATE visitors SET history=['gate:ENTRANCE'] where id=1;

UPDATE visitors SET history=history+['attraction:Pirate Boat', 'attraction:Lego cinema'] where id=1;
UPDATE parks SET amusements_last_visits=amusements_last_visits+{'Pirate Boat':1352505600, 'Lego cinema':1352512800} WHERE id=4;

UPDATE visitors SET history=history+['restaurant:Hot dog'] where id=1;
UPDATE visitors SET history=history+['gate:Exit'] where id=1;
UPDATE visitors SET history=history-['restaurant:Hot dog'] where id=1;
UPDATE visitors SET history=history+['restaurant:Hot dog|cancelled'] where id=1;

DELETE amusements_last_visits['restaurant'] FROM parks WHERE id=4;

SELECT name FROM visitors WHERE city='Mexico City' ALLOW FILTERING;
SELECT MAX(year) FROM parks;
SELECT name,year FROM parks WHERE year < 2000 ALLOW FILTERING;
SELECT COUNT(name) FROM parks WHERE year < 1970 ALLOW FILTERING;
SELECT name FROM visitors WHERE history CONTAINS 'gate:ENTRANCE' ALLOW FILTERING;
SELECT * FROM visitors WHERE name = ('Michal', 'Stanke') ALLOW FILTERING;

