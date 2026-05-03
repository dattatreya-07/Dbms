--1.Create Table
CREATE TABLE client (
    client_id INT PRIMARY KEY,
    client_name VARCHAR(100),
    client_mobile VARCHAR(15),
    client_address VARCHAR(200)
);

CREATE TABLE site (
    site_id INT PRIMARY KEY,
    site_name VARCHAR(100),
    site_address VARCHAR(200),
    start_date DATE,
    expected_completion_date DATE,
    estimated_budget DECIMAL(12,2),
    current_status VARCHAR(20),
    client_id INT,
    FOREIGN KEY (client_id) REFERENCES client(client_id)
);


CREATE TABLE employee (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100),
    mobile VARCHAR(15),
    address VARCHAR(200),
    joining_date DATE,
    employment_status VARCHAR(20),
);


CREATE TABLE employee_site_assignment (
    assignment_id INT PRIMARY KEY,
    employee_id INT,
    site_id INT,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (site_id) REFERENCES site(site_id)
);

CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY,
    employee_id INT,
    site_id INT,
    work_date DATE,
    attendance_type VARCHAR(10),
    UNIQUE (employee_id, work_date),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (site_id) REFERENCES site(site_id)
);

CREATE TABLE salary (
    salary_id INT PRIMARY KEY,
    employee_id INT,
    week_start_date DATE,
    week_end_date DATE,
    full_days INT,
    half_days INT,
    total_salary DECIMAL(10,2),
    payment_date DATE,
    payment_status VARCHAR(20),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE dependent (
    dependent_id INT PRIMARY KEY,
    employee_id INT,
    dependent_name VARCHAR(100),
    relationship VARCHAR(50),
    age INT,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE advance_payment (
    advance_id INT PRIMARY KEY,
    employee_id INT,
    advance_amount DECIMAL(10,2),
    advance_date DATE,
    recovery_status VARCHAR(20),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE material_category (
    category_id INT PRIMARY KEY,
    category_name VARCHAR(50)
);

CREATE TABLE material (
    material_id INT PRIMARY KEY,
    material_name VARCHAR(100),
    unit VARCHAR(20),
    total_quantity INT,
    purchase_date DATE,
    cost_per_unit DECIMAL(10,2),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES material_category(category_id)
);

CREATE TABLE supplier (
    supplier_id INT PRIMARY KEY,
    supplier_name VARCHAR(100)
);

CREATE TABLE purchase (
    purchase_id INT PRIMARY KEY,
    supplier_id INT,
    material_id INT,
    purchase_date DATE,
    purchased_quantity INT,
    purchase_cost DECIMAL(12,2),
    invoice_number VARCHAR(50),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE material_issue (
    issue_id INT PRIMARY KEY,
    site_id INT,
    material_id INT,
    issued_quantity INT,
    issue_date DATE,
    issued_by VARCHAR(100),
    FOREIGN KEY (site_id) REFERENCES site(site_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE material_return (
    return_id INT PRIMARY KEY,
    site_id INT,
    material_id INT,
    returned_quantity INT,
    return_date DATE,
    condition_status VARCHAR(20),
    FOREIGN KEY (site_id) REFERENCES site(site_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE material_usage (
    usage_id INT PRIMARY KEY,
    site_id INT,
    material_id INT,
    usage_date DATE,
    used_quantity INT,
    recorded_by VARCHAR(100),
    FOREIGN KEY (site_id) REFERENCES site(site_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE wastage (
    wastage_id INT PRIMARY KEY,
    site_id INT,
    material_id INT,
    wastage_quantity INT,
    wastage_date DATE,
    reason VARCHAR(200),
    FOREIGN KEY (site_id) REFERENCES site(site_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE stock_ledger (
    ledger_id INT PRIMARY KEY,
    material_id INT,
    opening_stock INT,
    issued_qty INT,
    returned_qty INT,
    consumed_qty INT,
    closing_stock INT,
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE tool_allocation (
    allocation_id INT PRIMARY KEY,
    employee_id INT,
    material_id INT,
    allocation_date DATE,
    return_date DATE,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

CREATE TABLE site_supervisor (
    site_id INT,
    employee_id INT,
    from_date DATE,
    to_date DATE,
    PRIMARY KEY (site_id, employee_id, from_date),
    FOREIGN KEY (site_id) REFERENCES site(site_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);


--2.Alter Table
ALTER TABLE employee
ADD CONSTRAINT pk_employee PRIMARY KEY (employee_id);

ALTER TABLE employee
ADD CONSTRAINT fk_employee_role
FOREIGN KEY (role_id)
REFERENCES role(role_id);

--3.Comment on Table
COMMENT ON TABLE employee IS
'Stores employee master details including role and employment status';

COMMENT ON COLUMN employee.employment_status IS
'Indicates whether employee is Active, Inactive or Resigned';

ALTER TABLE employee
RENAME TO employee_master;

TRUNCATE TABLE employee_master;


DROP TABLE employee;
