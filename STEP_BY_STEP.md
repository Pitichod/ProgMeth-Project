# Project Progmeth - Step by Step

เอกสารนี้อธิบายสิ่งที่ทำแล้วในโครงสร้างพื้นฐาน และสิ่งที่ต้องทำต่อเพื่อให้ตรงเกณฑ์ส่งงาน

## 1) สิ่งที่ทำเสร็จแล้ว (Foundation)

### 1.1 OOP Core
- สร้าง `BaseObject` เป็น abstract class (มี `name`, `x`, `y`, getter/setter)
- สร้าง `Human` เป็น abstract class และใช้ `Inheritance` จาก `BaseObject`
- สร้าง `Player` ที่มี `health`, `stamina` ตาม mechanic

### 1.2 Interfaces
- `Attackable` : รองรับการถูกโจมตี
- `Moveable` : รองรับการถูกผลัก/เคลื่อนย้าย
- `Pickable` : รองรับการเก็บไอเทม
- `Interactable` : รองรับการโต้ตอบกับสิ่งของ

### 1.3 Characters ตามเนื้อเรื่อง
- `Introvert`
- `Extrovert`
- `TA`
- `Teacher`

### 1.4 Obstacles และ Items
- Obstacles: `Chair`, `Table`, `Cable`
- Items: `Parabola`, `Caffeine`, `RobuxGiftCard`
- `BaseItem` เป็น abstract class และ implement `Pickable`

### 1.5 Level + Reward
- ด่าน `ISCALE_401` ถึง `ISCALE_405`
- `LevelConfig` กำหนด stamina เริ่มต้นแต่ละด่าน
- รางวัล 5 ชิ้น: `Glasses`, `Mouse`, `Notebook`, `Backpack`, `ChatGPTPro`
- `GameSession` คุม flow เริ่มด่าน/จบด่าน/สะสมรางวัล

### 1.6 Tests เริ่มต้น
- เพิ่ม JUnit test ใน `CoreMechanicTest`

---

## 2) สิ่งที่ต้องทำต่อ (Next Steps)

### Step 1: Map Data ของห้อง 401-405
1. สร้างไฟล์แผนที่ใน `src/main/resources/maps/` เช่น
   - `iscale_401.txt`
   - `iscale_402.txt`
   - `iscale_403.txt`
   - `iscale_404.txt`
   - `iscale_405.txt`
2. กำหนดสัญลักษณ์ใน map เช่น
   - `#` กำแพง
   - `P` จุดเริ่ม Player
   - `D` ประตูออก
   - `C` Chair
   - `T` Table
   - `W` Cable
   - `I` Introvert
   - `E` Extrovert
   - `A` TA
   - `R` Teacher (เฉพาะ 405)
   - `1/2/3` item (`Parabola`, `Caffeine`, `RobuxGiftCard`)

### Step 2: Game Engine Loop
1. ทำคลาส `GameEngine`
2. เพิ่มคำสั่งเดิน 4 ทิศ
3. เมื่อเดิน 1 ช่องให้ลด stamina 1
4. ถ้า stamina หมดก่อนถึงประตู ให้แพ้ด่าน
5. ถ้า health เป็น 0 ให้ restart ด่านนั้น

### Step 3: Interaction Rules ให้ครบ
1. เดินชน `Cable` => HP -1
2. ผลัก `Chair` => stamina -1
3. ผลัก `Table` => stamina -2
4. โจมตี `Introvert/Extrovert` => ช่องว่าง + เสียตามเงื่อนไข
5. โจมตี `TA` => HP เหลือ 1
6. โจมตี `Teacher` => HP 0 และแพ้ทันที

### Step 4: JavaFX UI + รูปภาพ
1. ทำ Grid map renderer ด้วย JavaFX
2. ใส่ sprite สำหรับตัวละคร/สิ่งกีดขวาง/item
3. แสดง HUD: HP, stamina, current level, rewards ที่ได้แล้ว

### Step 5: Report + UML
1. ทำ UML Class Diagram (รวม Inheritance + Interface implementation)
2. เขียนเหตุผลการใช้ Inheritance/Interface/Polymorphism
3. เขียนวิธี run, โครงสร้างไฟล์, และตัวอย่างเกมเพลย์

### Step 6: Final QA ก่อนส่ง
1. เพิ่ม unit test ให้ครอบคลุม logic สำคัญ
2. ตรวจ naming/style ให้สม่ำเสมอ
3. แยก commit ตามฟีเจอร์
4. อัปโหลดโค้ด + report + UML ลง GitHub

---

## 3) คำสั่งรัน

### Build
```bash
./gradlew build
```

### Run tests
```bash
./gradlew test
```

### Run app
```bash
./gradlew run
```
