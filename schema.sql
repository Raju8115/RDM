create Database test1;
use test1;

-- User Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    slack_id VARCHAR(100)
);

drop table if exists users;


-- Create practice table

CREATE TABLE practice (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(512) NOT NULL UNIQUE,
    description VARCHAR(2056)
);





-- Create practice_area table

CREATE TABLE practice_area (
    id INT AUTO_INCREMENT PRIMARY KEY,
    practice_id INT NOT NULL,
    name VARCHAR(512) NOT NULL,
    description VARCHAR(2056),
    FOREIGN KEY (practice_id) REFERENCES practice(id)
);




-- Create practice_product_technology table
CREATE TABLE practice_product_technology (
    id INT AUTO_INCREMENT PRIMARY KEY,
    practice_area_id INT NOT NULL,
    product_name VARCHAR(512) NOT NULL,
    technology_name VARCHAR(512),
    FOREIGN KEY (practice_area_id) REFERENCES practice_area(id)
);






-- User_skill Table
CREATE TABLE user_skill (
  id INT AUTO_INCREMENT PRIMARY KEY,

  user_id INT NOT NULL,
  practice_id INT ,
  practice_area_id INT NOT NULL,
  practice_product_technology_id INT NOT NULL,

  projects_done VARCHAR(10),  -- Values like '1-3', '4-5', etc.
  self_assessment_level VARCHAR(5), -- e.g., 'L2', 'L3'
  professional_level VARCHAR(20),   -- e.g., 'level 2', 'level 3'

  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (practice_id) REFERENCES practice(id),
  FOREIGN KEY (practice_area_id) REFERENCES practice_area(id),
  FOREIGN KEY (practice_product_technology_id) REFERENCES practice_product_technology(id)
);




-- User_skill_info Table

CREATE TABLE user_skill_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT not null,
    user_skill_id INT not null,
    project_title VARCHAR(512),
    technologies_used VARCHAR(512),
    duration VARCHAR(50),
    responsibilities VARCHAR(1024),
    client_tier VARCHAR(50),
    client_tier_v2 VARCHAR(50),
    project_complexity VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (user_skill_id) REFERENCES user_skill(id)
);




-- User_Secondary_skill

CREATE TABLE user_secondary_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT not null,
    practice VARCHAR(100),
    practice_area VARCHAR(100),
    products_technologies VARCHAR(512),
    duration VARCHAR(50),
    roles VARCHAR(512),
    certification_level VARCHAR(100),
    recency_of_certification VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);



-- User_ancillary_skills Table
CREATE TABLE user_ancillary_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT not null,
    technology VARCHAR(512),
    product VARCHAR(512),
    certified BOOLEAN DEFAULT FALSE,
    certification_link VARCHAR(1024),
    certification_level VARCHAR(100),
    recency_of_certification VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS professional_certifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    certified BOOLEAN,
    certification_link VARCHAR(512),
    certification_level VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
