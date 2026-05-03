

-- PART A — JOIN QUERIES (20 Queries)


---

-- 1. Material with supplier details

SELECT m.material_name, s.supplier_name
FROM material m
JOIN purchase p ON m.material_id = p.material_id
JOIN supplier s ON p.supplier_id = s.supplier_id;


---

-- 2. Purchase details with material name

SELECT p.purchase_id, m.material_name, p.purchased_quantity
FROM purchase p
JOIN material m ON p.material_id = m.material_id;


---

-- 3. Materials issued to sites

SELECT mi.site_id, m.material_name, mi.issued_quantity
FROM material_issue mi
JOIN material m ON mi.material_id = m.material_id;


---

-- 4. Material return with material name

SELECT mr.return_id, m.material_name, mr.returned_quantity
FROM material_return mr
JOIN material m ON mr.material_id = m.material_id;


---

-- 5. Wastage with material name

SELECT w.site_id, m.material_name, w.wastage_quantity
FROM wastage w
JOIN material m ON w.material_id = m.material_id;


---

-- 6. Stock ledger with material name

SELECT m.material_name, sl.closing_stock
FROM stock_ledger sl
JOIN material m ON sl.material_id = m.material_id;


---

-- 7. Machine with material info

SELECT ma.machine_id, m.material_name, ma.machine_status
FROM machines ma
JOIN material m ON ma.material_id = m.material_id;


---

-- 8. Consumable with material name

SELECT c.consumable_id, m.material_name, c.remaining_quantity
FROM consumable c
JOIN material m ON c.material_id = m.material_id;


---

-- 9. LEFT JOIN – All materials with purchase info

SELECT m.material_name, p.purchase_id
FROM material m
LEFT JOIN purchase p
ON m.material_id = p.material_id;


---

-- 10. RIGHT JOIN – Purchases with material

SELECT p.purchase_id, m.material_name
FROM purchase p
RIGHT JOIN material m
ON p.material_id = m.material_id;


---

-- 11–15. More INNER JOIN variations

SELECT s.supplier_name, p.purchase_cost
FROM supplier s
JOIN purchase p ON s.supplier_id = p.supplier_id;

SELECT m.material_name, p.purchase_date
FROM material m
JOIN purchase p ON m.material_id = p.material_id;

SELECT m.material_name, mi.issue_date
FROM material m
JOIN material_issue mi ON m.material_id = mi.material_id;

SELECT m.material_name, mr.return_date
FROM material m
JOIN material_return mr ON m.material_id = mr.material_id;

SELECT m.material_name, w.reason
FROM material m
JOIN wastage w ON m.material_id = w.material_id;


---

-- 16–20. Multi-table joins

SELECT s.supplier_name, m.material_name, p.purchase_cost
FROM supplier s
JOIN purchase p ON s.supplier_id = p.supplier_id
JOIN material m ON p.material_id = m.material_id;

SELECT m.material_name, sl.opening_stock, sl.closing_stock
FROM material m
JOIN stock_ledger sl ON m.material_id = sl.material_id;

SELECT ma.machine_id, m.material_name
FROM machines ma
JOIN material m ON ma.material_id = m.material_id;

SELECT c.consumable_id, m.material_name
FROM consumables c
JOIN material m ON c.material_id = m.material_id;

SELECT m.material_name, p.purchased_quantity, mi.issued_quantity
FROM material m
JOIN purchase p ON m.material_id = p.material_id
JOIN material_issue mi ON m.material_id = mi.material_id;


---

-- PART B — SET OPERATORS (8 Queries)


---

-- 21. UNION

SELECT material_id FROM purchase
UNION
SELECT material_id FROM material_issue;


---

-- 22. UNION ALL

SELECT material_id FROM purchase
UNION ALL
SELECT material_id FROM material_return;


---

-- 23. INTERSECT

SELECT material_id FROM purchase
INTERSECT
SELECT material_id FROM wastage;


---

-- 24. MINUS

SELECT material_id FROM purchase
MINUS
SELECT material_id FROM material_issue;


---

-- 25–28. More set examples

SELECT supplier_id FROM purchase
UNION
SELECT supplier_id FROM purchase;

SELECT material_id FROM stock_ledger
INTERSECT
SELECT material_id FROM machines;

SELECT material_id FROM consumables
UNION
SELECT material_id FROM material;

SELECT material_id FROM material
MINUS
SELECT material_id FROM wastage;


---

--PART C — GROUP BY + HAVING (7 Queries)


---

-- 29. Total purchase per material

SELECT material_id, SUM(purchased_quantity)
FROM purchase
GROUP BY material_id;


---

-- 30. Materials with purchase > 100

SELECT material_id, SUM(purchased_quantity)
FROM purchase
GROUP BY material_id
HAVING SUM(purchased_quantity) > 100;


---

-- 31. Average machine rent

SELECT material_id, AVG(rent_per_day)
FROM machines
GROUP BY material_id;


---

-- 32. Total wastage per site

SELECT site_id, SUM(wastage_quantity)
FROM wastage
GROUP BY site_id;


---

-- 33. Sites with wastage > 5

SELECT site_id, SUM(wastage_quantity)
FROM wastage
GROUP BY site_id
HAVING SUM(wastage_quantity) > 5;


---

-- 34. Count purchases per supplier

SELECT supplier_id, COUNT(*)
FROM purchase
GROUP BY supplier_id;


---

-- 35. Materials with more than 1 purchase

SELECT material_id, COUNT(*)
FROM purchase
GROUP BY material_id
HAVING COUNT(*) > 1;


---

-- PART D — ORDER BY (5 Queries)


---

-- 36. Materials sorted by cost

SELECT * FROM material
ORDER BY cost_per_unit DESC;


---

-- 37. Purchases sorted by date

SELECT * FROM purchase
ORDER BY purchase_date;


---

-- 38. Machines sorted by rent

SELECT * FROM machines
ORDER BY rent_per_day DESC;


---

-- 39. Wastage sorted by quantity

SELECT * FROM wastage
ORDER BY wastage_quantity;


---

-- 40. Suppliers alphabetical

SELECT * FROM supplier
ORDER BY supplier_name;


