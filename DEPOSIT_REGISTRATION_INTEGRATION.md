# DlgRawatJalan.java - Deposit Integration Instructions

## File Location
`D:\Projects\java\simrs-khanza\src\simrskhanza\DlgRawatJalan.java`

## Overview
Add deposit recording capability to the outpatient registration form so that staff can record deposits immediately upon registration.

## Step 1: Import the Deposit Class

Add to imports section (around line 1-50):
```java
import keuangan.DlgDepositRalan;
```

## Step 2: Declare Button Variable

Add to variable declarations (look for section with "private widget.Button BtnSimpan;" around line 1142):
```java
private widget.Button BtnDepositRalan;
```

## Step 3: Initialize Button in initComponents()

Find the button panel initialization section (around line 1495-1550 where BtnSimpan, BtnBatal, BtnHapus are initialized).

Add after BtnHapus initialization:
```java
BtnDepositRalan = new widget.Button();
BtnDepositRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/addressbook-edit24.png"))); // NOI18N
BtnDepositRalan.setMnemonic('D');
BtnDepositRalan.setText("Deposit");
BtnDepositRalan.setToolTipText("Alt+D - Record Deposit");
BtnDepositRalan.setName("BtnDepositRalan"); // NOI18N
BtnDepositRalan.setPreferredSize(new java.awt.Dimension(100, 30));
BtnDepositRalan.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        BtnDepositRalanActionPerformed(evt);
    }
});
BtnDepositRalan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyPressed(java.awt.event.KeyEvent evt) {
        BtnDepositRalanKeyPressed(evt);
    }
});
panelGlass8.add(BtnDepositRalan);
```

## Step 4: Implement Button Action Method

Add these methods at the end of the class (before the closing brace, around line 14300):

```java
    private void BtnDepositRalanActionPerformed(java.awt.event.ActionEvent evt) {
        // Verify registration exists
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih atau buat registrasi terlebih dahulu!");
            return;
        }

        // Verify patient data is loaded
        if (TNoRM.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Data pasien belum lengkap!");
            return;
        }

        // Open deposit dialog
        DlgDepositRalan depositDialog = new DlgDepositRalan(null, false);
        depositDialog.isCek();
        depositDialog.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        depositDialog.setLocationRelativeTo(internalFrame1);
        depositDialog.setNoRm(TNoRM.getText(), TPasien.getText(), TNoRw.getText());
        depositDialog.setVisible(true);

        // Optional: Refresh display after deposit window closes
        // depositDialog.addWindowListener(new WindowAdapter() {
        //     @Override
        //     public void windowClosed(WindowEvent e) {
        //         tampil(); // Refresh if needed
        //     }
        // });
    }

    private void BtnDepositRalanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnDepositRalanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }
```

## Step 5: Add Access Control

Find the `isCek()` method (search for "public void isCek()" - likely around line 13000-14000).

Add permission check:
```java
// Add this line in the isCek() method
BtnDepositRalan.setEnabled(akses.getdeposit_pasien()); // Or use akses.getdeposit_ralan() when available
```

## Step 6: Optional - Display Prepaid Balance

To show patient's prepaid balance in the registration form:

### 6a. Add Label Declaration
```java
private widget.Label LabelSaldoPrepaid;
```

### 6b. Initialize Label
Add to initComponents() in the form layout section:
```java
LabelSaldoPrepaid = new widget.Label();
LabelSaldoPrepaid.setText("Saldo Prepaid: Rp 0");
LabelSaldoPrepaid.setName("LabelSaldoPrepaid");
LabelSaldoPrepaid.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
LabelSaldoPrepaid.setForeground(new java.awt.Color(0, 102, 0));
// Add to appropriate panel near patient info
```

### 6c. Create Method to Load Balance
```java
private void loadSaldoPrepaid(String no_rkm_medis) {
    try {
        double saldo = Sequel.cariIsiAngka(
            "SELECT COALESCE(saldo, 0) FROM saldo_deposit_ralan WHERE no_rkm_medis = ?",
            no_rkm_medis
        );
        LabelSaldoPrepaid.setText("Saldo Prepaid: Rp " + Valid.SetAngka(saldo));
        if (saldo > 0) {
            LabelSaldoPrepaid.setForeground(new java.awt.Color(0, 102, 0)); // Green
        } else {
            LabelSaldoPrepaid.setForeground(new java.awt.Color(102, 102, 102)); // Gray
        }
    } catch (Exception e) {
        LabelSaldoPrepaid.setText("Saldo Prepaid: Rp 0");
    }
}
```

### 6d. Call loadSaldoPrepaid() When Patient is Selected
Find where patient data is loaded (search for methods that populate TNoRM, TPasien).
Add call to refresh prepaid balance:
```java
// After patient is selected and TNoRM is populated:
if (!TNoRM.getText().trim().equals("")) {
    loadSaldoPrepaid(TNoRM.getText());
}
```

## Step 7: Testing

### Test Case 1: Basic Deposit Recording
1. Open registration form (Alt+R or menu)
2. Select existing patient or register new patient
3. Click "Deposit" button (Alt+D)
4. Deposit dialog should open with patient data pre-filled
5. Record deposit
6. Verify deposit is saved in `deposit_ralan` table

### Test Case 2: Validation
1. Try clicking Deposit button without selecting registration
2. Should show error: "Silahkan pilih atau buat registrasi terlebih dahulu!"

### Test Case 3: Prepaid Balance Display (if implemented)
1. Create prepaid deposit for a patient
2. Select that patient in registration
3. Prepaid balance should display in green if > 0

### SQL Verification
```sql
-- Check deposits recorded from registration
SELECT dr.no_deposit, dr.no_rawat, dr.no_rkm_medis, p.nm_pasien,
       dr.tgl_deposit, dr.besar_deposit, dr.jenis_deposit
FROM deposit_ralan dr
INNER JOIN pasien p ON dr.no_rkm_medis = p.no_rkm_medis
WHERE DATE(dr.tgl_deposit) = CURDATE()
ORDER BY dr.tgl_deposit DESC;
```

## UI Layout Considerations

### Button Placement Options

**Option A: Add to main button panel (Recommended)**
- Place after BtnHapus in panelGlass8
- Follows existing pattern: Simpan | Baru | Hapus | **Deposit** | Print | ...

**Option B: Add as separate panel near patient info**
- Create new panel near patient display section
- Shows prepaid balance + deposit button together
- More prominent but requires more UI changes

**Option C: Add to context menu**
- Right-click menu on registered patients table
- Less discoverable but cleaner UI

## Icon Suggestions

Use one of these existing icons:
- `/picture/addressbook-edit24.png` - Edit icon
- `/picture/money.png` - Money icon (if available)
- `/picture/cashew16.png` - Cash icon (if available)

Or create custom icon: 16x16 or 24x24 PNG with deposit/money symbol.

## Dependencies

Required classes:
- ✅ `keuangan.DlgDepositRalan` - Already created
- ✅ `database.deposit_ralan` table - Already created
- ⚠️ `fungsi.akses.getdeposit_ralan()` - Needs to be added (currently using getdeposit_pasien)

## Keyboard Shortcuts

- **Alt+D**: Open deposit dialog (defined in button mnemonic)
- **Alt+S**: Save registration (existing)
- **Alt+B**: New/Reset form (existing)

## Notes

1. **Performance**: The deposit dialog is created new each time. For better performance, consider singleton pattern with dialog reuse.

2. **Auto-open**: Consider auto-opening deposit dialog after successful registration if configured:
   ```java
   // Add to end of BtnSimpanActionPerformed after successful registration:
   if (Sequel.cariIsi("SELECT deposit_ralan_auto FROM set_user WHERE id_user=?", akses.getkode()).equals("Ya")) {
       BtnDepositRalanActionPerformed(null);
   }
   ```

3. **Notification**: Consider showing toast notification after deposit is recorded successfully.

4. **Integration with Billing**: When implementing billing integration, ensure deposits recorded here appear in DlgBilingRalan.

## Troubleshooting

**Issue**: Button doesn't appear
- Check if panelGlass8.add(BtnDepositRalan) is called
- Verify button initialization is in initComponents()
- Rebuild project to ensure UI changes are compiled

**Issue**: ClassNotFoundException for DlgDepositRalan
- Verify import statement is correct
- Ensure DlgDepositRalan.java is compiled
- Check classpath includes keuangan package

**Issue**: Permission denied
- Check isCek() method includes button permission
- Verify user has deposit_pasien permission in database
- Test with admin account first

## Future Enhancements

1. **Quick Deposit Entry**: Add small deposit entry form inline in registration (amount + payment method only)
2. **Prepaid Usage**: Add checkbox "Use prepaid balance for this visit"
3. **Deposit History**: Show recent deposits for this patient
4. **Deposit Required**: Add setting to require deposit before registration completes

---

**Implementation Date**: 2026-02-11
**Modified File**: `src/simrskhanza/DlgRawatJalan.java`
**Related Files**: `src/keuangan/DlgDepositRalan.java`, `database/migration_deposit_ralan.sql`
