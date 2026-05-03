
-- Function to Get Material Quantity

```sql
CREATE OR REPLACE FUNCTION get_material_qty
(p_id NUMBER)
RETURN NUMBER
IS
v_qty NUMBER;
BEGIN
   SELECT total_quantity INTO v_qty
   FROM material
   WHERE material_id = p_id;

   RETURN v_qty;
END;
/
```

---

-- Function to Get Material Cost

```sql
CREATE OR REPLACE FUNCTION get_material_cost
(p_id NUMBER)
RETURN NUMBER
IS
v_cost NUMBER;
BEGIN
   SELECT cost_per_unit INTO v_cost
   FROM material
   WHERE material_id = p_id;

   RETURN v_cost;
END;
/
```

---

-- Function to Count Materials

```sql
CREATE OR REPLACE FUNCTION count_material
RETURN NUMBER
IS
v_count NUMBER;
BEGIN
   SELECT COUNT(*) INTO v_count FROM material;
   RETURN v_count;
END;
/
```

---

-- Function to Get Supplier Name

```sql
CREATE OR REPLACE FUNCTION get_supplier_name
(p_id NUMBER)
RETURN VARCHAR2
IS
v_name VARCHAR2(100);
BEGIN
   SELECT supplier_name INTO v_name
   FROM supplier
   WHERE supplier_id = p_id;

   RETURN v_name;
END;
/
```

---

-- Function to Calculate Purchase Cost

```sql
CREATE OR REPLACE FUNCTION calc_purchase_cost
(p_qty NUMBER, p_cost NUMBER)
RETURN NUMBER
IS
v_total NUMBER;
BEGIN
   v_total := p_qty * p_cost;
   RETURN v_total;
END;
/
```

---

-- Function to Check Stock Level

```sql
CREATE OR REPLACE FUNCTION check_stock
(p_mid NUMBER)
RETURN VARCHAR2
IS
v_qty NUMBER;
BEGIN
   SELECT total_quantity INTO v_qty
   FROM material
   WHERE material_id = p_mid;

   IF v_qty < 50 THEN
      RETURN 'LOW';
   ELSE
      RETURN 'SUFFICIENT';
   END IF;
END;
/
```

---

-- Function to Count Purchases

```sql
CREATE OR REPLACE FUNCTION count_purchase
RETURN NUMBER
IS
v_total NUMBER;
BEGIN
   SELECT COUNT(*) INTO v_total FROM purchase;
   RETURN v_total;
END;
/
```

---

-- Function to Get Machine Rent

```sql
CREATE OR REPLACE FUNCTION get_machine_rent
(p_id NUMBER)
RETURN NUMBER
IS
v_rent NUMBER;
BEGIN
   SELECT rent_per_day INTO v_rent
   FROM machines
   WHERE machine_id = p_id;

   RETURN v_rent;
END;
/
```

---

-- Function to Count Wastage

```sql
CREATE OR REPLACE FUNCTION total_wastage
RETURN NUMBER
IS
v_total NUMBER;
BEGIN
   SELECT SUM(wastage_quantity) INTO v_total
   FROM wastage;

   RETURN v_total;
END;
/
```

---

-- Function to Check Consumable Stock

```sql
CREATE OR REPLACE FUNCTION remaining_consumable
(p_id NUMBER)
RETURN NUMBER
IS
v_rem NUMBER;
BEGIN
   SELECT remaining_quantity INTO v_rem
   FROM consumable
   WHERE consumable_id = p_id;

   RETURN v_rem;
END;
/
```
