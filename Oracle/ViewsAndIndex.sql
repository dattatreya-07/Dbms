--Create Simple View
CREATE VIEW material_view AS
SELECT material_id, material_name, unit, total_quantity, cost_per_unit
FROM material;

--INSERT Through View
--(Works only if view is simple and based on one table)
INSERT INTO material_view
VALUES (101, 'Cement', 'Bags', 500, 350);

--UPDATE Through View
UPDATE material_view
SET cost_per_unit = 400
WHERE material_id = 101;

--DELETE Through View
DELETE FROM material_view
WHERE material_id = 101;

--View With WHERE Condition
CREATE VIEW low_stock_view AS
SELECT * FROM material
WHERE total_quantity < 100;

--Insert with CHECK OPTION
CREATE VIEW low_stock_view AS
SELECT * FROM material
WHERE total_quantity < 100
WITH CHECK OPTION;

INSERT INTO low_stock_view
VALUES (102, 'Bricks', 'Pieces', 50, 10);

--View With JOIN (Not Updatable)
CREATE VIEW purchase_details AS
SELECT p.purchase_id, m.material_name, s.supplier_name
FROM purchase p
JOIN material m ON p.material_id = m.material_id
JOIN supplier s ON p.supplier_id = s.supplier_id;

--Replace View
CREATE OR REPLACE VIEW material_view AS
SELECT material_id, material_name
FROM material;

--Drop View
DROP VIEW material_view;
----------------------------------------------------------------
--Create Simple Index
CREATE INDEX material_name_idx
ON material(material_name);

--Create Unique Index
CREATE UNIQUE INDEX supplier_phone_idx
ON supplier(phone);

--Composite Index
CREATE INDEX purchase_index
ON purchase(material_id, supplier_id);

--Index on Foreign Key
CREATE INDEX issue_material_idx
ON material_issue(material_id);

--Bitmap Index (Oracle)
CREATE BITMAP INDEX material_unit_idx
ON material(unit);

--View All Indexes
SELECT index_name, table_name
FROM user_indexes;

--View Indexed Columns
SELECT index_name, column_name
FROM user_ind_columns;

--Rebuild Index
ALTER INDEX material_name_idx REBUILD;

--Rename Index
ALTER INDEX material_name_idx RENAME TO mat_name_idx;

-- Drop Index
DROP INDEX mat_name_idx;
