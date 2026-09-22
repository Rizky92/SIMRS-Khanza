# Swing forms, `.form` files, and NetBeans

Applies to any agent working in this repository.

Unless specified otherwise, the modules under `/src` are regular NetBeans Swing forms: every UI class has a
`.form` counterpart — the design file, an XML structure with XML-like formatting.

## Keeping `.form` and `.java` in sync
The two files must agree:

- The design maps to the `initComponents()` method in the java file.
- Every event maps to a method marked with `//GEN-FIRST:event_<event method name>` after the opening curly
  brace and `//GEN-LAST:event<event method name>` after the closing curly brace.

A change on either side must be reflected on the other.

### Preferred workflow
1. **Edit the `.form` file first**, if you understand its XML.
2. Then hand it to NetBeans so it regenerates the java side for you:
   - **If the NetBeans MCP server is available**, call its `openFile` tool on the java counterpart. Opening
     the form in the IDE re-synchronizes `initComponents()` and the generated event methods, so you never
     hand-write them.
   - **If it is not available**, tell the user to open the form manually in NetBeans, and say which file.

This avoids hand-editing generated code. If you do edit `initComponents()` or an event method directly, you
own the job of mirroring it back into the `.form`.

## Planning a new menu
When planning a new menu, describe the general window layout you intend to design. This lets the user catch
a broken layout before it is previewed in NetBeans.

## Components
Use the primary components from `src/widget`:

| Component | Description |
| --- | --- |
| `ScrollPane.java` | Custom JScrollPane. |
| `Table.java` | Custom JTable. |
| `TabPane.java` | Custom JTabbedPane. |
| `Tanggal.java` | Custom JCalendar, uses `java.util.Date` for handling dates. |
| `TextArea.java` | Custom JTextArea. |
| `TextBox.java` | Custom JTextField. |
| `Button.java` | Custom JButton. |
| `ButtonBig.java` | Custom JButton, for menu items in `frmUtama`. |
| `CekBox.java` | Custom JCheckBox. |
| `ComboBox.java` | Custom JComboBox. |
| `InternalFrame.java` | Acts as the main wrapper housing components inside a JDialog. |
| `Label.java` | Custom JLabel. |
| `PanelBiasa.java` | Custom JPanel. |
| `PasswordBox.java` | Custom JPasswordField. |

Any component not listed falls back to its Swing counterpart.

## Layout metrics

### Heights
| Component | Height |
| --- | --- |
| Label, TextBox, PasswordBox, CekBox, radio button, ComboBox, Tanggal | 23 |
| Button | 30 |
| Clip button (quick-pick button, icon `/picture/190.png`) | 23 (width 28) |

Clip buttons are the exception to the button height: they sit inside an input row, so they are **28 x 23**
— matching the input height, not the button height.

### Spacing
Between label / textbox / checkbox / radio button / combobox / tanggal components:

- horizontal gap: **3**
- bottom gap: **7**

With a component height of 23, the next row's `y` is therefore `y + 30`.

### Origin
The first label of an input in a form sits at **x = 0, y = 10**.

## Titled border
All forms and custom-designed dialogs give their `InternalFrame` panel a titled border:

- inner border: line border, color **[240, 245, 235]**, thickness **1**
- title font: **Tahoma 11, plain** (style 0)
- title color: **[50, 50, 50]**
- title text formatted as `::[ <title> ]::`

**Exception — sub-dialogs:** a sub-dialog panel keeps everything above but uses a line border color of
**[50, 50, 50]** (`blue="32" green="32" red="32"`), the same value as the title color.
So far only two forms use it, and both also carry a normal [240, 245, 235] panel alongside the
sub-dialog one:

- `src/bridging/BPJSKompilasiBerkasKlaimSMC.form` — `::[ Pengaturan Kompilasi Berkas ]::`
- `src/inventory/DlgDaftarPermintaanResep.form` — `::[ Pengaturan Resep Otomatis ]::`

Every other form is not converted yet. Follow this rule for new work; do not treat an unconverted form as
a counter-example.

In the `.form` XML:

```xml
<Border info="org.netbeans.modules.form.compat2.border.TitledBorderInfo">
  <TitledBorder title="::[ Riwayat Obat Diberikan ]::">
    <Border PropertyName="innerBorder" info="org.netbeans.modules.form.compat2.border.LineBorderInfo">
      <LineBorder>
        <Color PropertyName="color" blue="eb" green="f5" red="f0" type="rgb"/>
      </LineBorder>
    </Border>
    <Font PropertyName="font" name="Tahoma" size="11" style="0"/>
    <Color PropertyName="color" blue="32" green="32" red="32" type="rgb"/>
  </TitledBorder>
</Border>
```

## CRUD action row
The action buttons at the bottom of a form are ordered:

```
[ Simpan ] [ Baru ] [ Hapus ] [ Ganti ] [ Cetak ] [ <record counter> ] [ Keluar ]
```

Their panel uses a **Flow layout**, aligned **left**, horizontal gap **5**, vertical gap **9**:

```xml
<Layout class="org.netbeans.modules.form.compat2.layouts.DesignFlowLayout">
  <Property name="alignment" type="int" value="0"/>
  <Property name="verticalGap" type="int" value="9"/>
</Layout>
```

NetBeans omits `horizontalGap` when it equals the default of 5, so its absence from the XML is expected.
