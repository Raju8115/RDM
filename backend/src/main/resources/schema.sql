-- Drop tables in correct order (child tables first, then parent tables)
DROP TABLE IF EXISTS user_skill_info;
DROP TABLE IF EXISTS user_secondary_skills;
DROP TABLE IF EXISTS user_ancillary_skills;
DROP TABLE IF EXISTS user_skill;
DROP TABLE IF EXISTS practice_product_technology;
DROP TABLE IF EXISTS practice_area;
DROP TABLE IF EXISTS practice;
DROP TABLE IF EXISTS users;

-- User Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    slack_id VARCHAR(100)
);

-- Create practice table

CREATE TABLE IF NOT EXISTS practice (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(512) NOT NULL UNIQUE,
    description VARCHAR(2056)
);





-- Create practice_area table

CREATE TABLE IF NOT EXISTS practice_area (
    id INT AUTO_INCREMENT PRIMARY KEY,
    practice_id INT NOT NULL,
    name VARCHAR(512) NOT NULL,
    description VARCHAR(2056),
    FOREIGN KEY (practice_id) REFERENCES practice(id)
);




-- Create practice_product_technology table
CREATE TABLE IF NOT EXISTS practice_product_technology (
    id INT AUTO_INCREMENT PRIMARY KEY,
    practice_area_id INT NOT NULL,
    product_name VARCHAR(512) NOT NULL,
    technology_name VARCHAR(512),
    FOREIGN KEY (practice_area_id) REFERENCES practice_area(id)
);






-- User_skill Table
CREATE TABLE IF NOT EXISTS user_skill (
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

CREATE TABLE IF NOT EXISTS user_skill_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT not null,
    user_skill_id INT not null,
    project_title VARCHAR(512),
    technologies_used VARCHAR(512),
    duration VARCHAR(50),
    responsibilities VARCHAR(1024),
    client_tier VARCHAR(50),
    client_tier_v2 VARCHAR(50),
    pending_delete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (user_skill_id) REFERENCES user_skill(id)
);




-- User_Secondary_skill

CREATE TABLE IF NOT EXISTS user_secondary_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT not null,
    practice VARCHAR(100),
    practice_area VARCHAR(100),
    products_technologies VARCHAR(512),
    duration VARCHAR(50),
    roles VARCHAR(512),
    pending_delete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);



-- User_ancillary_skills Table
CREATE TABLE IF NOT EXISTS user_ancillary_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT not null,
    technology VARCHAR(512),
    product VARCHAR(512),
    certified BOOLEAN DEFAULT FALSE,
    certification_link VARCHAR(1024),
    pending_delete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- User_messages Table
CREATE TABLE IF NOT EXISTS user_messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL,
    reason TEXT NOT NULL,
    message_type VARCHAR(50) DEFAULT 'REJECTION',
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    manager_email VARCHAR(100)
);

-- Professional Certifications Table
CREATE TABLE IF NOT EXISTS professional_certifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    certified BOOLEAN,
    certification_link VARCHAR(512),
    pending_delete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- High Impact Assets and Accelerators Table
CREATE TABLE IF NOT EXISTS high_impact_assets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255),
    business_impact VARCHAR(100),
    visibility_adoption VARCHAR(100),
    description TEXT,
    pending_delete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert for users

INSERT INTO users (name, email, slack_id) VALUES
('Name1', 'Name1@somedummydata.com', 'slk_4f9AqZ'),
('Name2', 'Name2@somedummydata.com', 'slk_p8KdXt'),
('Name3', 'Name3@somedummydata.com', 'slk_v5LmRt'),
('Name4', 'Name4@somedummydata.com', 'slk_q9BzNm'),
('Name5', 'Name5@somedummydata.com', 'slk_d3RtXy'),
('Name6', 'Name6@somedummydata.com', 'slk_j1KpAz'),
('Name7', 'Name7@somedummydata.com', 'slk_b8TnWy'),
('Name8', 'Name8@somedummydata.com', 'slk_h2KrLo'),
('Name9', 'Name9@somedummydata.com', 'slk_x4WqEm'),
('Pawan Kumar','PawanKumar@gmail.com','Pawan@slackid');


-- Insert values for Practice
INSERT INTO practice (name) VALUES
('Data and AI'),
('Automation'),
('SCBN'),
('Infrastructure'),
('EC and EM'),
('Network and Security');

-- Insert values for Practice_area

INSERT INTO practice_area (name, practice_id) VALUES
('Additional products', 1),
('AI Assistants', 1),
('AI Governance', 1),
('AI Tools', 1),
('Application Development', 2),
('Application Integration', 2),
('Asset Lifecycle. Mgtm.', 2),
('Asset LifeCylce Mgmt. & B2B Integration', 2),
('Business Analytics', 2),
('Content Management', 2),
('Data Integration', 3),
('Data Intelligence', 3),
('Data Intelligence2', 3),
('Data Lakehouse', 3),
('Data Security', 3),
('DataBases', 3),
('IBM Cloud', 4),
('IBM Storage', 4),
('Indentity & Access Mgmt.', 4),
('Network Mgmt.', 4),
('Observability', 4),
('Power', 5),
('Security', 6),
('Workflow Automation', 6),
('zAIOps', 6),
('zDevOps', 6),
('zHW', 6),
('zHybrid Cloud', 6);

-- Values for Practice_product_techmology
-- Additional_product
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('OpenPages', 1);

-- Products under AI Assistants

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('watsonx Assistant', 2),
('Watsonx Orchestrate', 2);


-- Products under AI Governance

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('watsonx.gov', 3);


-- Products under AI Tools

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('watsonx.ai', 4);


-- Application_Development

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Runtime for Java', 5),
('WebSphere', 5);


-- Application Integration

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('API Connect 2.0', 6),
('App Connect', 6),
('Cloud Pak for Integration', 6),
('DataPower Solutions', 6),
('MQ', 6),
('webMethods', 6);


-- Asset Lifecycle. Mgtm.

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Envizi', 7),
('Maximo', 7);


-- Asset LifeCylce Mgmt. & B2B Integration

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Engineering Lifecylce Management', 8),
('Maximo',8),
('Order Management System (OMS)', 8),
('Sterling', 8),
('Sterling, ELM', 8),
('Tririga', 8);


-- Business Analytics

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Cognos', 9),
('Planning Analytics', 9);


-- Content Management

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('CMOD', 10),
('Filenet Content Manager', 10);


-- Data Integration

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('DataStage', 11),
('StreamSets', 11);


-- Data Intelligence

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Cloud Pak for Data', 12),
('Data Lineage', 12),
('Knowledge Catalog', 12),
('Master Data Management', 12);


-- Data Intelligence2

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Cloud Pak for Data', 13),
('Data Replication', 13),
('MDM', 13);


-- Data Lakehouse

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('watsonx.data', 14);


-- Data Security

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Gaurdium', 15);


-- DataBases

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Database Ecosystem', 16),
('DB2', 16),
('DB2 , OCP', 16),
('Netezza', 16),
('Oracle', 16);


-- IBM Cloud

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('IBM Cloud - ROKS/IKS', 17),
('IBM Cloud - VMWare', 17),
('IBM Virtual Private Cloud / Classic', 17);


-- IBM Storage

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Spectrum Symphony', 18);


-- Indentity & Access Mgmt.

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Verify', 19),
('Verify, Guardium', 19);


-- Network Mgmt.

INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Hybrid Cloud Mesh', 20),
('SevOne', 20);


-- Observability
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Cloud Pak for AIOps', 21),
('Instana', 21),
('Operations Insights', 21),
('Turbonomic', 21);

-- Power
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('AIX', 22),
('IBM i', 22);

-- Security
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Qradar', 23);

-- Workflow Automation
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('BAMOE', 24),
('Business Automation Workflow', 24),
('Cloud Pak for Bus. Automation', 24),
('ODM', 24);

-- zAIOps
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('Intellimagic', 25),
('System Automation', 25);

-- zDevOps
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('ABO', 26),
('Additional Products', 26),
('Ansible', 26),
('IDzEE', 26),
('WCA4Z', 26);

-- zHW
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('CICS', 27),
('DB2', 27),
('MQ for z/OS', 27),
('z/OS Core', 27),
('zPCA', 27),
('zStorage', 27);

-- zHybrid Cloud
INSERT INTO practice_product_technology (product_name, practice_area_id) VALUES
('watsonx Code Assistant for z', 28),
('Z Compilers', 28),
('Z Security', 28);


-- Insert values for user_skill



-- Name1 - Power > AIX
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name1@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'Power'),
  (SELECT id FROM practice_area WHERE name = 'Power'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'AIX'),
  '1-3', 'L2', 'level 2'
);

-- Name2 - zHybrid Cloud > watsonx Code Assistant for z
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name2@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'watsonx Code Assistant for z'),
  '0', 'L2', 'level 0'
);

-- Name3 - zHybrid Cloud > Intellimagic
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name3@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Intellimagic'),
  '4-5', 'L3', 'level 3'
);

-- Name4 - zHybrid Cloud > watsonx Code Assistant for z
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name4@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_area WHERE name = 'zHybrid Cloud'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'watsonx Code Assistant for z'),
  '4-5', 'L2', 'level 3'
);

-- Name5 - AI Tools > watsonx.ai
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name5@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'AI Tools'),
  (SELECT id FROM practice_area WHERE name = 'AI Tools'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'watsonx.ai'),
  '1-3', 'L2', 'level 2'
);

-- Name6 - Workflow Automation > Cloud Pak for Bus. Automation
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name6@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'Workflow Automation'),
  (SELECT id FROM practice_area WHERE name = 'Workflow Automation'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Cloud Pak for Bus. Automation'),
  '1-3', 'L3', 'level 2'
);

-- Name7 - Identity & Access Mgmt. > Verify
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name7@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'Indentity & Access Mgmt.'),
  (SELECT id FROM practice_area WHERE name = 'Indentity & Access Mgmt.'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Verify'),
  '1-3', 'L2', 'level 2'
);

-- Name8 - Application Integration > Cloud Pak for Integration
INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name8@somedummydata.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'Application Integration'),
  (SELECT id FROM practice_area WHERE name = 'Application Integration'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Cloud Pak for Integration'),
  '1-3', 'L3', 'level 2'
);

INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id, 
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'Name9@somedummydata.com' LIMIT 1),
  (SELECT practice_id FROM practice_area WHERE name = 'Asset Lifecycle. Mgtm.' LIMIT 1),
  (SELECT id FROM practice_area WHERE name = 'Asset Lifecycle. Mgtm.' LIMIT 1),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Maximo' LIMIT 1),
  '0', 'L3', 'level 1'
);



INSERT INTO user_skill (
  user_id, practice_id, practice_area_id, practice_product_technology_id,
  projects_done, self_assessment_level, professional_level
) VALUES (
  (SELECT id FROM users WHERE email = 'PawanKumar@gmail.com'),
  (SELECT practice_id FROM practice_area WHERE name = 'Application Integration'),
  (SELECT id FROM practice_area WHERE name = 'Application Integration'),
  (SELECT id FROM practice_product_technology WHERE product_name = 'Cloud Pak for Integration'),
  '1-3', 'L3', 'level 2'
);

-- Select Query Commands

select * from users;

select * from practice;

select * from practice_area;

select * from practice_product_technology;

select * from user_skill;

select * from user_skill_info;

select * from user_secondary_skills;

select * from user_ancillary_skills;

