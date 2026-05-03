--Implicit Cursor – UPDATE
BEGIN
   UPDATE material
   SET total_quantity = total_quantity + 5;

   DBMS_OUTPUT.PUT_LINE('Rows Updated: ' || SQL%ROWCOUNT);
END;
/

--Implicit Cursor – DELETE
BEGIN
   DELETE FROM material_return
   WHERE returned_quantity = 0;

   IF SQL%FOUND THEN
      DBMS_OUTPUT.PUT_LINE('Rows Deleted');
   END IF;
END;
/
-- Implicit Cursor – INSERT
BEGIN
   INSERT INTO purchase
   VALUES (11,1, 103,’10-jan-2025’, 10,1000, 'inv18');

   DBMS_OUTPUT.PUT_LINE('Inserted Rows: ' || SQL%ROWCOUNT);
END;
/

--Simple Explicit Cursor
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
   v_name material.material_name%TYPE;
BEGIN
   OPEN c1;
   FETCH c1 INTO v_name;
   DBMS_OUTPUT.PUT_LINE(v_name);
   CLOSE c1;
END;
/

--Explicit Cursor – LOOP
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
   v_name material.material_name%TYPE;
BEGIN
   OPEN c1;
   LOOP
      FETCH c1 INTO v_name;
      EXIT WHEN c1%NOTFOUND;
      DBMS_OUTPUT.PUT_LINE(v_name);
   END LOOP;
   CLOSE c1;
END;
/

--Cursor %ROWCOUNT
DECLARE
   CURSOR c1 IS SELECT * FROM material;
   v_rec material%ROWTYPE;
BEGIN
   OPEN c1;
   LOOP
      FETCH c1 INTO v_rec;
      EXIT WHEN c1%NOTFOUND;
   END LOOP;
   DBMS_OUTPUT.PUT_LINE('Total Rows: ' || c1%ROWCOUNT);
   CLOSE c1;
END;
/

-- Cursor FOR Loop
BEGIN
   FOR rec IN (SELECT material_name FROM material) LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name);
   END LOOP;
END;
/

--Parameterized Cursor
DECLARE
   CURSOR low_stock(p_qty NUMBER) IS
      SELECT material_name FROM material
      WHERE total_quantity < p_qty;
BEGIN
   FOR rec IN low_stock(50) LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name);
   END LOOP;
END;
/

--Cursor with WHERE Clause
DECLARE
   CURSOR c1 IS
      SELECT material_name FROM material
      WHERE cost_per_unit > 100;
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name);
   END LOOP;
END;
/

--Cursor with ORDER BY
DECLARE
   CURSOR c1 IS
      SELECT material_name FROM material
      ORDER BY cost_per_unit DESC;
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name);
   END LOOP;
END;
/

--Cursor with GROUP BY
DECLARE
   CURSOR c1 IS
      SELECT unit_type, COUNT(*)
      FROM material
      GROUP BY unit_type;
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.unit_type || ' - ' || rec.COUNT);
END;
/

--Cursor with HAVING
DECLARE
   CURSOR c1 IS
      SELECT unit, COUNT(*) total
      FROM material
      GROUP BY unit
      HAVING COUNT(*) > 1;
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.unit || ' - ' || rec.total);
   END LOOP;
END;
/

--Cursor FOR UPDATE
DECLARE
   CURSOR c1 IS
      SELECT total_quantity
      FROM material
      FOR UPDATE;
BEGIN
   FOR rec IN c1 LOOP
      UPDATE material
      SET total_quantity = rec.total_quantity + 1
      WHERE CURRENT OF c1;
   END LOOP;
END;
/

--Cursor with DELETE
DECLARE
   CURSOR c1 IS
      SELECT material_id
      FROM material
      WHERE total_quantity = 0
      FOR UPDATE;
BEGIN
   FOR rec IN c1 LOOP
      DELETE FROM material
      WHERE CURRENT OF c1;
   END LOOP;
END;
/

--Cursor with Exception
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
   v_name material.material_name%TYPE;
BEGIN
   OPEN c1;
   FETCH c1 INTO v_name;
   CLOSE c1;

EXCEPTION
   WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Error Occurred');
END;
/

--Nested Cursor
DECLARE
   CURSOR c1 IS SELECT supplier_id FROM supplier;
   CURSOR c2(p_id NUMBER) IS
      SELECT purchase_id FROM purchase
      WHERE supplier_id = p_id;
BEGIN
   FOR rec1 IN c1 LOOP
      FOR rec2 IN c2(rec1.supplier_id) LOOP
         DBMS_OUTPUT.PUT_LINE(rec2.purchase_id);
      END LOOP;
   END LOOP;
END;
/

-- Bulk Collect
DECLARE
   TYPE mat_tab IS TABLE OF material.material_name%TYPE;
   v_list mat_tab;
BEGIN
   SELECT material_name BULK COLLECT INTO v_list FROM material;
   DBMS_OUTPUT.PUT_LINE('Rows: ' || v_list.COUNT);
END;
/

--Cursor with %ISOPEN
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
BEGIN
   OPEN c1;
   IF c1%ISOPEN THEN
      DBMS_OUTPUT.PUT_LINE('Cursor Opened');
   END IF;
   CLOSE c1;
END;
/

--Cursor with %FOUND
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
   v_name material.material_name%TYPE;
BEGIN
   OPEN c1;
   FETCH c1 INTO v_name;

   IF c1%FOUND THEN
      DBMS_OUTPUT.PUT_LINE('Row Found');
   END IF;

   CLOSE c1;
END;
/

--Cursor with %NOTFOUND
DECLARE
   CURSOR c1 IS SELECT material_name FROM material;
   v_name material.material_name%TYPE;
BEGIN
   OPEN c1;
   FETCH c1 INTO v_name;

   IF c1%NOTFOUND THEN
      DBMS_OUTPUT.PUT_LINE('No Row');
   ELSIF C1%FOUND THEN
      DBMS_OUTPUT.PUT_LINE(‘ROW’);
   ELSE
      DBMS_OUTPUT.PUT_LINE(‘INVALID’)
   END IF;

   CLOSE c1;
END;
/

--Cursor with COUNT Condition
DECLARE
   CURSOR c1 IS SELECT COUNT(*) total FROM material;
BEGIN
   FOR rec IN c1 LOOP
      IF rec.total > 5 THEN
         DBMS_OUTPUT.PUT_LINE('More Materials');
      END IF;
   END LOOP;
END;
/

--Cursor with Join
DECLARE
   CURSOR c1 IS
      SELECT m.material_name, s.supplier_name
      FROM material m
      JOIN purchase p ON m.material_id = p.material_id
      JOIN supplier s ON p.supplier_id = s.supplier_id;
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name || ' - ' || rec.supplier_name);
   END LOOP;
END;
/

--Cursor with Subquery
DECLARE
   CURSOR c1 IS
      SELECT material_name
      FROM material
      WHERE cost_per_unit > (SELECT AVG(cost_per_unit) FROM material);
BEGIN
   FOR rec IN c1 LOOP
      DBMS_OUTPUT.PUT_LINE(rec.material_name);
   END LOOP;
END;
/

--Cursor with CASE
DECLARE
   CURSOR c1 IS SELECT material_name, total_quantity FROM material;
BEGIN
   FOR rec IN c1 LOOP
      CASE
         WHEN rec.total_quantity < 50 THEN
            DBMS_OUTPUT.PUT_LINE(rec.material_name || ' Low');
         ELSE
            DBMS_OUTPUT.PUT_LINE(rec.material_name || ' OK');
      END CASE;
   END LOOP;
END;
/

--Cursor with Commit
DECLARE
   CURSOR c1 IS
      SELECT material_id FROM material
      FOR UPDATE;
BEGIN
   FOR rec IN c1 LOOP
      UPDATE material
      SET cost_per_unit = cost_per_unit + 5
      WHERE CURRENT OF c1;
   END LOOP;
   COMMIT;
END;
/
