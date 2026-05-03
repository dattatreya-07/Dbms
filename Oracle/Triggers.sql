
-- 1. BEFORE INSERT Trigger (Material Quantity Check)

```sql
CREATE OR REPLACE TRIGGER trg_check_material_qty
BEFORE INSERT ON material
FOR EACH ROW
BEGIN
   IF :NEW.total_quantity < 0 THEN
      RAISE_APPLICATION_ERROR(-20001,'Quantity cannot be negative');
   END IF;
END;
/
```

---

-- 2. AFTER INSERT Trigger (Display Message)

```sql
CREATE OR REPLACE TRIGGER trg_material_insert
AFTER INSERT ON material
BEGIN
   DBMS_OUTPUT.PUT_LINE('Material inserted successfully');
END;
/
```

---

-- 3. BEFORE UPDATE Trigger

```sql
CREATE OR REPLACE TRIGGER trg_before_update_material
BEFORE UPDATE ON material
FOR EACH ROW
BEGIN
   IF :NEW.cost_per_unit < 0 THEN
      RAISE_APPLICATION_ERROR(-20002,'Invalid cost');
   END IF;
END;
/
```

---

-- 4. AFTER UPDATE Trigger

```sql
CREATE OR REPLACE TRIGGER trg_after_update_material
AFTER UPDATE ON material
BEGIN
   DBMS_OUTPUT.PUT_LINE('Material updated');
END;
/
```

---

-- 5. BEFORE DELETE Trigger

```sql
CREATE OR REPLACE TRIGGER trg_before_delete_material
BEFORE DELETE ON material
BEGIN
   DBMS_OUTPUT.PUT_LINE('Deleting material record');
END;
/
```

---

-- 6. AFTER DELETE Trigger

```sql
CREATE OR REPLACE TRIGGER trg_after_delete_material
AFTER DELETE ON material
BEGIN
   DBMS_OUTPUT.PUT_LINE('Material deleted');
END;
/
```

---

-- 7. Trigger to Update Stock Ledger After Purchase

```sql
CREATE OR REPLACE TRIGGER trg_purchase_stock
AFTER INSERT ON purchase
FOR EACH ROW
BEGIN
   UPDATE stock_ledger
   SET opening_stock = opening_stock + :NEW.purchased_quantity
   WHERE material_id = :NEW.material_id;
END;
/
```

---

-- 8. Trigger for Material Issue

```sql
CREATE OR REPLACE TRIGGER trg_issue_material
AFTER INSERT ON material_issue
FOR EACH ROW
BEGIN
   UPDATE material
   SET total_quantity = total_quantity - :NEW.issued_quantity
   WHERE material_id = :NEW.material_id;
END;
/
```

---

-- 9. Trigger for Material Return

```sql
CREATE OR REPLACE TRIGGER trg_return_material
AFTER INSERT ON material_return
FOR EACH ROW
BEGIN
   UPDATE material
   SET total_quantity = total_quantity + :NEW.returned_quantity
   WHERE material_id = :NEW.material_id;
END;
/
```

---

-- 10. Prevent Delete if Material Used

```sql
CREATE OR REPLACE TRIGGER trg_prevent_delete_material
BEFORE DELETE ON material
FOR EACH ROW
BEGIN
   IF :OLD.total_quantity < 10 THEN
      RAISE_APPLICATION_ERROR(-20003,'Material cannot be deleted');
   END IF;
END;
/
```

---

-- 11. Supplier Insert Validation

```sql
CREATE OR REPLACE TRIGGER trg_supplier_name
BEFORE INSERT ON supplier
FOR EACH ROW
BEGIN
   IF :NEW.supplier_name IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,'Supplier name required');
   END IF;
END;
/
```

---

-- 12. Purchase Cost Validation

```sql
CREATE OR REPLACE TRIGGER trg_purchase_cost
BEFORE INSERT ON purchase
FOR EACH ROW
BEGIN
   IF :NEW.purchase_cost <= 0 THEN
      RAISE_APPLICATION_ERROR(-20005,'Invalid purchase cost');
   END IF;
END;
/
```

---

-- 13. Update Consumable Quantity

```sql
CREATE OR REPLACE TRIGGER trg_consumable_update
AFTER INSERT ON material_issue
FOR EACH ROW
BEGIN
   UPDATE consumable
   SET used_quantity = used_quantity + :NEW.issued_quantity
   WHERE material_id = :NEW.material_id;
END;
/
```

---

-- 14. Prevent Machine Rent Negative

```sql
CREATE OR REPLACE TRIGGER trg_machine_rent
BEFORE INSERT ON machines
FOR EACH ROW
BEGIN
   IF :NEW.rent_per_day < 0 THEN
      RAISE_APPLICATION_ERROR(-20006,'Invalid rent amount');
   END IF;
END;
/
```

---

-- 15. Wastage Trigger

```sql
CREATE OR REPLACE TRIGGER trg_wastage_update
AFTER INSERT ON wastage
FOR EACH ROW
BEGIN
   UPDATE material
   SET total_quantity = total_quantity - :NEW.wastage_quantity
   WHERE material_id = :NEW.material_id;
END;
/
```

---

-- 16. Prevent Excess Issue

```sql
CREATE OR REPLACE TRIGGER trg_check_issue
BEFORE INSERT ON material_issue
FOR EACH ROW
DECLARE
   v_qty NUMBER;
BEGIN
   SELECT total_quantity INTO v_qty
   FROM material
   WHERE material_id = :NEW.material_id;

   IF :NEW.issued_quantity > v_qty THEN
      RAISE_APPLICATION_ERROR(-20007,'Insufficient stock');
   END IF;
END;
/
```

---

-- 17. Trigger for Update Purchase

```sql
CREATE OR REPLACE TRIGGER trg_update_purchase
AFTER UPDATE ON purchase
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Purchase record updated');
END;
/
```

---

-- 18. Statement Level Trigger

```sql
CREATE OR REPLACE TRIGGER trg_statement
AFTER INSERT ON material
BEGIN
   DBMS_OUTPUT.PUT_LINE('Insert operation completed');
END;
/
```

---

-- 19. Audit Trigger Example

```sql
CREATE OR REPLACE TRIGGER trg_audit_material
AFTER UPDATE ON material
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Material changed from '
   || :OLD.total_quantity || ' to ' || :NEW.total_quantity);
END;
/
```

---

-- 20. Prevent Update on Sunday

```sql
CREATE OR REPLACE TRIGGER trg_no_sunday_update
BEFORE UPDATE ON material
BEGIN
   IF TO_CHAR(SYSDATE,'DAY') = 'SUNDAY' THEN
      RAISE_APPLICATION_ERROR(-20008,'Update not allowed on Sunday');
   END IF;
END;
/
```

---

