CREATE TABLE userlogin (
  email nvarchar(255) UNIQUE not null,
  pass varchar(30) not null,
  no_hp varchar(15) not null,
  PRIMARY KEY (no_hp)
);

INSERT INTO userlogin VALUES ('abcd@gmail.com', 'abcd', '+6281234567891' );
INSERT INTO userlogin VALUES ('efgh@gmail.com', 'abcd', '+6281234562121' );

gcloud sql connect frescodb -u root
cap0468
use user_data;

CREATE TABLE fruitinfo ( 
  scandate DATE not null,
  fruitname varchar(30) not null,
  fruitstatus varchar(30) not null,
  prohib nvarchar(255) not null,
  negative nvarchar(255) not null,
  no_hp varchar(15) not null,
  FOREIGN KEY (no_hp) REFERENCES userlogin(no_hp)
);

INSERT INTO fruitinfo VALUES ('2021-05-12', 'orange', 'fresh', 'the skin is all black', 'can cause a dhiarrea', '+6281234567891' );
INSERT INTO fruitinfo VALUES ('2021-05-12', 'strawberry', 'rotten', 'the fruit smell bad', 'can cause a dhiarrea', '+6281234567891' );
INSERT INTO fruitinfo VALUES ('2021-05-24', 'pumpkin', 'fresh', 'the fruit turns black', 'can cause a dhiarrea', '+6281234567891' );
INSERT INTO fruitinfo VALUES ('2021-05-12', 'watermelon', 'fresh', 'the fruit turns black', 'can cause a dhiarrea', '+6281234562121' );
INSERT INTO fruitinfo VALUES ('2021-05-16', 'lemon', 'fresh', 'none', 'none', '+6281234562121' );

SELECT * FROM userlogin;
SELECT * FROM fruitinfo;

SELECT fruitinfo.scandate as ScanDate, fruitinfo.fruitname as FruitName, fruitinfo.fruitstatus as Status
FROM userlogin
LEFT JOIN fruitinfo 
ON userlogin.no_hp=fruitinfo.no_hp
WHERE fruitinfo.no_hp='+6281234567891';
