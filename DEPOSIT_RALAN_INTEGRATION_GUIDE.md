# Outpatient Deposit System - Integration Guide

## Implementation Status

### ✅ Completed Components

#### 1. Database Schema (Task #1)
- **File**: `database/migration_deposit_ralan.sql`
- **Tables Created**:
  - `deposit_ralan` - Main deposit transactions
  - `pengembalian_deposit_ralan` - Refund tracking
  - `saldo_deposit_ralan` - Prepaid balance per patient
  - `set_akun_ranap2` - Added GL accounts (Uang_Muka_Ralan, Sisa_Uang_Muka_Ralan)

#### 2. Core Deposit Class (Task #2)
- **File**: `src/keuangan/DlgDepositRalan.java`
- **Features**:
  - Record outpatient deposits
  - Auto-numbering: DR/YYYY/MM/NNNN
  - GL account integration: Uang_Muka_Ralan
  - Journal entry creation
  - PPN calculation
  - Supports no_rkm_medis and jenis_deposit fields
  - Currently defaults to VISIT type deposits

#### 3. Refund Management Class (Task #3)
- **File**: `src/keuangan/DlgPengembalianDepositRalan.java`
- **Features**:
  - Search and display refunds by date, patient, visit
  - Filter by officer
  - Print refund reports
  - Total refund calculations

### 🔄 Partially Completed

#### 4. Billing Integration (Task #4)
- **File**: `src/keuangan/DlgBilingRalan.java` (6330 lines - very complex)
- **Status**: Integration points identified but not yet implemented
- **Required Changes**:

##### A. Add Helper Methods (Add after line 100)
```java
// Get total deposit for a visit
private double getTotalDepositRalan(String no_rawat) {
    try {
        return Sequel.cariIsiAngka("SELECT COALESCE(SUM(besar_deposit), 0) FROM deposit_ralan WHERE no_rawat=?", no_rawat);
    } catch (Exception e) {
        return 0;
    }
}

// Get total refunds for a visit
private double getTotalRefundRalan(String no_rawat) {
    try {
        return Sequel.cariIsiAngka("SELECT COALESCE(SUM(besar_pengembalian), 0) FROM pengembalian_deposit_ralan WHERE no_rawat=?", no_rawat);
    } catch (Exception e) {
        return 0;
    }
}

// Get available deposit (deposit - refunds)
private double getAvailableDepositRalan(String no_rawat) {
    return getTotalDepositRalan(no_rawat) - getTotalRefundRalan(no_rawat);
}

// Get prepaid balance for patient
private double getSaldoDepositRalan(String no_rkm_medis) {
    try {
        return Sequel.cariIsiAngka("SELECT COALESCE(saldo, 0) FROM saldo_deposit_ralan WHERE no_rkm_medis=?", no_rkm_medis);
    } catch (Exception e) {
        return 0;
    }
}
```

##### B. Add UI Components (In FormInput panel)
Add after existing labels/buttons:
```java
// Display deposit information
private widget.Label labelInfoDeposit;
private widget.Button BtnTambahDeposit;
private widget.Button BtnGunakanSaldo;

// In constructor, initialize:
labelInfoDeposit = new widget.Label();
labelInfoDeposit.setText("Deposit: Rp 0 | Tersedia: Rp 0");
labelInfoDeposit.setName("labelInfoDeposit");

BtnTambahDeposit = new widget.Button();
BtnTambahDeposit.setText("Tambah Deposit");
BtnTambahDeposit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/addressbook-edit24.png")));
BtnTambahDeposit.addActionListener(evt -> BtnTambahDepositActionPerformed(evt));

BtnGunakanSaldo = new widget.Button();
BtnGunakanSaldo.setText("Gunakan Saldo Prepaid");
BtnGunakanSaldo.addActionListener(evt -> BtnGunakanSaldoActionPerformed(evt));
```

##### C. Update Billing Calculation (Find the total calculation method, around line 3000-4000)
```java
// Add deposit deduction to billing calculation
double totalDeposit = getAvailableDepositRalan(TNoRw.getText());
double totalTagihan = ttlLaborat + ttlRadiologi + ttlObat + ttlRalan_Dokter + ttlRalan_Paramedis +
                     ttlTambahan - ttlPotongan + ttlRegistrasi + ttlOperasi;

// Apply deposit
double sisaBayar = totalTagihan - totalDeposit - (already paid amount);

// Display in UI
labelInfoDeposit.setText(String.format("Deposit: Rp %s | Tersedia: Rp %s",
    Valid.SetAngka(totalDeposit), Valid.SetAngka(Math.max(0, totalDeposit - totalTagihan))));
```

##### D. Add Button Actions
```java
private void BtnTambahDepositActionPerformed(java.awt.event.ActionEvent evt) {
    if (!TNoRw.getText().equals("")) {
        DlgDepositRalan deposit = new DlgDepositRalan(null, false);
        deposit.isCek();
        deposit.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        deposit.setLocationRelativeTo(internalFrame1);
        deposit.setNoRm(TNoRM.getText(), TPasien.getText(), TNoRw.getText());
        deposit.setVisible(true);
        // Refresh billing after deposit window closes
        tampil();
    } else {
        JOptionPane.showMessageDialog(null, "Pilih pasien terlebih dahulu!");
    }
}

private void BtnGunakanSaldoActionPerformed(java.awt.event.ActionEvent evt) {
    double saldoPrepaid = getSaldoDepositRalan(TNoRM.getText());
    if (saldoPrepaid > 0) {
        int response = JOptionPane.showConfirmDialog(null,
            "Saldo Prepaid: Rp " + Valid.SetAngka(saldoPrepaid) +
            "\nGunakan untuk kunjungan ini?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Transfer prepaid to visit deposit
            // Implementation needed: create deposit_ralan record from prepaid balance
        }
    } else {
        JOptionPane.showMessageDialog(null, "Tidak ada saldo prepaid");
    }
}
```

##### E. Auto-refund Excess Deposit (In payment processing method)
```java
// After payment is completed and status_bayar is updated
double excessDeposit = totalDeposit - totalTagihan;
if (excessDeposit > 0 && statusBayar.equals("Sudah Bayar")) {
    int refundChoice = JOptionPane.showOptionDialog(null,
        "Kelebihan deposit: Rp " + Valid.SetAngka(excessDeposit) +
        "\nRefund ke:",
        "Refund Deposit",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        new String[]{"Kas/Tunai", "Saldo Prepaid", "Batal"},
        "Kas/Tunai");

    if (refundChoice == 0) {
        // Refund to cash
        Sequel.menyimpantf2("pengembalian_deposit_ralan",
            "?,?,?,?,?,?", "Refund", 6,
            new String[]{TNoRw.getText(), null,
                Valid.SetTgl(new Date()) + " " + now.substring(11,19),
                akses.getkode(), excessDeposit+"",
                "Kelebihan deposit (otomatis)"});
    } else if (refundChoice == 1) {
        // Return to prepaid balance
        // Update saldo_deposit_ralan
    }
}
```

### ❌ Not Yet Implemented

#### 5. Registration Integration (Task #5)
- **File**: `src/simrskhanza/DlgRawatJalan.java`
- **Required**: Add "Record Deposit" button in registration form
- **Location**: After patient registration is saved

#### 6. Jasper Reports (Task #6)
- **Files Needed**:
  - `report/rptDepositRalan.jasper` - Deposit receipt
  - `report/rptDepositRalanSummary.jasper` - Summary report
  - `report/rptSaldoDepositRalan.jasper` - Prepaid balance report
  - `report/rptPengembalianDepositRalan.jasper` - Refund report
- **Note**: Can copy from `rptDeposit.jasper` and modify

#### 7. Report Interface (Task #7)
- **File**: `src/laporan/DlgLaporanDepositRalan.java`
- **Needs**: Full implementation for all report types

#### 8. Menu Integration (Task #8)
- **Files**:
  - Update main menu configuration
  - `src/fungsi/akses.java` - Add `getdeposit_ralan()` permission method
- **Menu Items**:
  - Finance → Deposit Pasien Ralan
  - Finance → Pengembalian Deposit Ralan
  - Reports → Finance → Laporan Deposit Ralan

#### 9. Testing (Task #9)
- **Required**: Full integration testing
- **Test Cases**: See original implementation plan

## Advanced Features (Not Yet Implemented)

### Prepaid Deposit System
The current implementation only supports VISIT-type deposits. To add prepaid functionality:

1. **DlgDepositRalan.java modifications**:
   - Add JComboBox for deposit type selection (VISIT/PREPAID)
   - Add logic to allow NULL no_rawat for PREPAID deposits
   - Update `saldo_deposit_ralan` table when recording PREPAID
   - Add method to transfer prepaid balance to visit deposit

2. **saldo_deposit_ralan table operations**:
   - INSERT/UPDATE when recording prepaid deposit
   - DECREMENT when using prepaid for visit
   - INCREMENT when refunding to prepaid

### Multiple Deposit Types Per Visit
Currently, one deposit per visit is assumed. For multiple deposits:
- Modify queries to use SUM(besar_deposit)
- Track individual deposits via no_deposit reference

## Next Steps for Full Implementation

1. **Priority 1 - Critical for Basic Functionality**:
   - Complete DlgBilingRalan.java integration (sections A-E above)
   - Add menu items
   - Test basic deposit → billing → refund workflow

2. **Priority 2 - Enhanced Usability**:
   - Add registration integration (DlgRawatJalan.java)
   - Create Jasper report templates
   - Implement report interface

3. **Priority 3 - Advanced Features**:
   - Implement prepaid deposit system
   - Add prepaid balance transfer
   - Add deposit expiration (if needed)

## Database Migration Instructions

1. **Backup current database**:
   ```bash
   mysqldump -u root -p sik > backup_before_deposit_ralan.sql
   ```

2. **Run migration**:
   ```bash
   mysql -u root -p sik < database/migration_deposit_ralan.sql
   ```

3. **Configure GL accounts**:
   ```sql
   UPDATE set_akun_ranap2
   SET Uang_Muka_Ralan = '1.1.04.03',      -- Adjust to your chart of accounts
       Sisa_Uang_Muka_Ralan = '2.1.01.06'  -- Adjust to your chart of accounts
   WHERE 1=1;
   ```

4. **Verify tables created**:
   ```sql
   SHOW TABLES LIKE '%deposit_ralan%';
   DESC deposit_ralan;
   DESC pengembalian_deposit_ralan;
   DESC saldo_deposit_ralan;
   ```

## Compilation Instructions

After completing the Java implementations, compile:

```bash
cd D:\Projects\java\simrs-khanza
# Compile new classes
javac -cp "lib/*:." src/keuangan/DlgDepositRalan.java
javac -cp "lib/*:." src/keuangan/DlgPengembalianDepositRalan.java

# If using build tool (Ant/Maven/Gradle), use that instead
```

## Rollback Procedure

If issues arise:

1. **Database rollback**:
   ```bash
   mysql -u root -p sik < backup_before_deposit_ralan.sql
   ```

2. **Code rollback**:
   ```bash
   git checkout HEAD -- src/keuangan/DlgDepositRalan.java
   git checkout HEAD -- src/keuangan/DlgPengembalianDepositRalan.java
   git checkout HEAD -- src/keuangan/DlgBilingRalan.java
   ```

## Support and Maintenance

**Created**: 2026-02-11
**System**: SIMRS Khanza
**Module**: Outpatient Deposit Management
**Version**: 1.0 (Initial Implementation)

For questions or issues, refer to the full implementation plan document.
