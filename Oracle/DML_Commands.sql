 1. MATERIAL

--INSERT

INSERT INTO material
VALUES (1, 'Cement', 'Bags', 500, '10-jan-2025', 350.00, 1);

INSERT INTO material VALUES (4, 'Sand', 'Ton', 200, '07-jan-2026', 1200.00, 2);
--UPDATE

UPDATE material
SET total_quantity = 600
WHERE material_id = 103;

--DELETE

DELETE FROM material
WHERE material_id = 102;

--LIKE / NOT LIKE

SELECT * FROM material
WHERE material_name LIKE 'c%';

SELECT * FROM material
WHERE material_name NOT LIKE 'S%';

--BETWEEN / NOT BETWEEN (Date)

SELECT * FROM material
WHERE purchase_date BETWEEN '2025-01-01' AND '2025-01-20';

SELECT * FROM material
WHERE cost_per_unit NOT BETWEEN 100 AND 500;

--Aggregate

SELECT SUM(total_quantity) FROM material;
SELECT AVG(cost_per_unit) FROM material;


---

 2. SUPPLIER

--INSERT

INSERT INTO supplier VALUES (1, 'ST Suppliers');

--INSERT ALL

INSERT INTO supplier VALUES (2, 'XYZ Traders');
INSERT INTO supplier VALUES (3, 'Metro Supply');

--UPDATE

UPDATE supplier
SET supplier_name = 'ST SANKAR Global'
WHERE supplier_id = 1;

--DELETE

DELETE FROM supplier
WHERE supplier_id = 3;

--LIKE

SELECT * FROM supplier
WHERE supplier_name LIKE '%Traders%';

--BETWEEN

SELECT * FROM consumable
WHERE used_quantity BETWEEN 10 AND 60;

--Aggregate

SELECT SUM(remaining_quantity) FROM consumable;
SELECT AVG(rent_per_day) FROM machines;
SELECT COUNT(*) FROM supplier;
SELECT SUM(closing_stock) FROM stock_ledger;
SELECT MAX(wastage_quantity) FROM wastage;

