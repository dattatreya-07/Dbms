---

-- 1. Materials purchased by supplier 1

SELECT material_name
FROM material
WHERE material_id IN (
    SELECT material_id
    FROM purchase
    WHERE supplier_id = 1
);


---

-- 2. Suppliers who supplied material 1

SELECT supplier_name
FROM supplier
WHERE supplier_id IN (
    SELECT supplier_id
    FROM purchase
    WHERE material_id = 1
);


---

-- 3. Materials not purchased yet

SELECT material_name
FROM material
WHERE material_id NOT IN (
    SELECT material_id FROM purchase
);


---

-- 4. Material with highest purchase cost

SELECT *
FROM purchase
WHERE purchase_cost = (
    SELECT MAX(purchase_cost)
    FROM purchase
);


---

-- 5. Machines with rent greater than average rent

SELECT *
FROM machines
WHERE rent_per_day > (
    SELECT AVG(rent_per_day)
    FROM machines
);


---

-- 6. Materials with cost greater than average cost

SELECT *
FROM material
WHERE cost_per_unit > (
    SELECT AVG(cost_per_unit)
    FROM material
);


---

-- 7. Sites where issued quantity is greater than 50

SELECT site_id
FROM material_issue
WHERE issued_quantity > (
    SELECT AVG(issued_quantity)
    FROM material_issue
);


---

-- 8. Materials used in wastage

SELECT material_name
FROM material
WHERE material_id IN (
    SELECT material_id FROM wastage
);


---

-- 9. Materials not used in wastage

SELECT material_name
FROM material
WHERE material_id NOT IN (
    SELECT material_id FROM wastage
);


---

-- 10. Supplier with maximum total purchase cost

SELECT supplier_id
FROM purchase
GROUP BY supplier_id
HAVING SUM(purchase_cost) = (
    SELECT MAX(total_cost)
    FROM (
        SELECT SUM(purchase_cost) AS total_cost
        FROM purchase
        GROUP BY supplier_id
    ) t
);


---

-- 11. Materials purchased more than 100 quantity

SELECT material_name
FROM material
WHERE material_id IN (
    SELECT material_id
    FROM purchase
    WHERE purchased_quantity > 100
);


---

-- 12. Machines linked to purchased materials

SELECT *
FROM machines
WHERE material_id IN (
    SELECT material_id FROM purchase
);


---

-- 13. Materials whose stock is less than issued quantity

SELECT material_name
FROM material
WHERE material_id IN (
    SELECT material_id
    FROM stock_ledger
    WHERE closing_stock < issued_qty
);


---

-- 14. Materials purchased on latest date

SELECT *
FROM purchase
WHERE purchase_date = (
    SELECT MAX(purchase_date)
    FROM purchase
);


---

-- 15. Materials purchased between two dates (Nested)

SELECT material_name
FROM material
WHERE material_id IN (
    SELECT material_id
    FROM purchase
    WHERE purchase_date BETWEEN '01-jan-2025' AND '01-jan-2026'
);


---

-- 16. Using EXISTS

SELECT material_name
FROM material m
WHERE EXISTS (
    SELECT 1
    FROM purchase p
    WHERE p.material_id = m.material_id
);


---

-- 17. Using NOT EXISTS

SELECT material_name
FROM material m
WHERE NOT EXISTS (
    SELECT 1
    FROM purchase p
    WHERE p.material_id = m.material_id
);


---

-- 18. Using ANY

SELECT *
FROM machines
WHERE rent_per_day > ANY (
    SELECT rent_per_day FROM machines
);


---

-- 19. Using ALL

SELECT *
FROM material
WHERE cost_per_unit < ALL (
    SELECT cost_per_unit FROM material
    WHERE material_id in(101,102,103)
);


---

-- 20. Correlated Subquery – Materials purchased more than average of that material

SELECT p.material_id, p.purchased_quantity
FROM purchase p
WHERE p.purchased_quantity > (
    SELECT AVG(p2.purchased_quantity)
    FROM purchase p2
    WHERE p2.material_id = p.material_id
);
.


---

-- > ALL (Greater Than All)


SELECT emp_name, salary
FROM employee
WHERE salary > ALL (SELECT salary FROM employee WHERE dept_id = 10);

✔ Returns employees whose salary is greater than the highest salary in dept 10.


---

-- < ALL (Less Than All)



SELECT emp_name, salary
FROM employee
WHERE salary < ALL (SELECT salary FROM employee WHERE dept_id = 20);


---

--= ANY (Equal to Any)



SELECT emp_name
FROM employee
WHERE dept_id = ANY (SELECT dept_id FROM department WHERE location = 'Chennai');


---

-- > ANY



SELECT emp_name, salary
FROM employee
WHERE salary > ANY (SELECT salary FROM employee WHERE dept_id = 30);


---

--< ANY

SELECT emp_name, salary
FROM employee
WHERE salary < ANY (SELECT salary FROM employee WHERE dept_id = 40);


---

