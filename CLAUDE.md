# SIMRS Khanza Claude Instructions

## Project Overview
This is a home repository for modified SIMRS Khanza, a fork of original [SIMRS Khanza](https://github.com/mas-elkhanza/SIMRS-Khanza) developed by YASKI. This fork is designed to cater the needs for RS Samarinda Medika Citra should the origin doesn't have the features it needed. The project uses Java 17 as programming langauge but targets Java 11 for building, NetBeans as primary IDE, and Java Swing as its main UI.

### Important directories
Important root directories are listed below
- `/anjunganpasienmandiri`: A self-checkout kiosk for patient registrations, bookings, and check-ins.
- `/api-bpjsfktl`: An API service written in PHP for BPJS kesehatan to connect to hospital server.
- `/bridging-adamlabs`: A Laravel-based API service to connect laboratory result and transaction used for ADAMLABS into hospital server.
- `/KhanzaAntrianLoket`: A subproject to display admission/registration queue.
- `/KhanzaHMSServiceAplicare`: A subproject used to update Hospital Bed in BPJS Kesehatan, running as a service.
- `/KhanzaHMSServiceMobileJKNERM`: A subproject used to update Queue service and Task ID for BPJS Kesehatan. Running as a service.
- `/KhanzaHMSServiceSatuSehat`: A subproject used to record visits and patient history into Satu Sehat, a national health ministry platform for centralized patient's medical history, running as a service.
- `/KhanzaPengenkripsiTeks`: A subproject used to encrypt/decrypt values in UI form.
- `/mandiri`: An API service for managing Bank Mandiri service using host-to-host model.
- `/nbproject`: Netbeans project configuration folder.
- `/report`: Used to host jasper report designs and compiled report resources.
- `/setting`: Used to load settings in XML format.
- `/src`: The source code of main SIMRS Khanza project.
- `/suara`: used to host sound resources.
- `/webapps`: A web based application written in PHP, mainly used to support billing note printing, file-based medical history, eklaim bridging service, human resource management, and various services to document proof of actions. Used internally in SIMRS Khanza project itself.

#### `/src` core architecture
`/src` has various modules. These are what they're used for.
- `src/48x48`: Used to place image resources for main menu icons.
- `src/bridging`: Modules for various bridging to 3rd party services, for example BPJS Kesehatan, SatuSehat.
- `src/dapur`: Modules for kitchen inventory management.
- `src/fungsi`: Helper functions
- `src/grafikanalisa`: Modules to display various graphics.
- `src/informasi`: Modules to display general public information (e.g. bed status, doctor's practic schedule).
- `src/inventaris`: Modules used for asset, CSSD, building, and waste management.
- `src/inventory`: Modules for pharmacy-related general inventory management.
- `src/ipsrs`: Modules used for non-medical and consumable inventory management.
- `src/kepegawaian`: Modules for managing human resources and auditing for various worker-related incidents.
- `src/keuangan`: Modules for accounting management.
- `src/laporan`: Modules for medical statistic reporting.
- `src/parkir`: Modules for parking management.
- `src/pcraicra`: Modules for general construction related management.
- `src/permintaan`: Modules for managing medical service requests.
- `src/perpustakaan`: Modules for library/book management.
- `src/picture`: Used to place image resources for various UI resources.
- `src/rekammedis`: Modules for managing and record general patient medical history.
- `src/restore`: Modules for restoring deleted records from various menu.
- `src/setting`: Modules for administrator to control the settings for application uses.
- `src/simrskhanza`: Modules for main modules of SIMRS Khanza.
- `src/support`: Another helper functions, this was used for developer's need which may destruct changes from upstream repository if placed inside `src/fungsi`.
- `src/surat`: Modules for managing letter and administrations.
- `src/toko`: Modules for managing internal shop.
- `src/tranfusidarah`: Modules for blood transfusion management.
- `src/viabarcode`: Modules for accessing various services quickly using barcode scanner.
- `src/widget`: Houses various UI components used internally.
- `src/ziscsr`: Modules for managing charities and donations.

Unless specified otherwise, these modules follow Netbeans' regular swing forms. So each files in the modules have their `.form` counterpart. Inside the `.java` files, each have `initComponents()`. Changes in this method should be reflected to their `.form` counterpart.
If you're making plans for new menu, describe the general window layout you're going to design if you can. This should help user understand what you're going for designing the form in case it's broken when previewed in NetBeans.

### General coding guidelines
The coding guidelines should cater to users' netbeans configuration, which as follows.
- Use 4 spaces as indentation.
- No hard line wrap. Soft line wrap is around 200-300 characters.
- Trim trailing whitespaces.
- Package imports are sorted alphabetically.

#### Workflow guideline
- Before starting the changes, ensure current branch is in `custom` branch. Then switch to new branch. Branch format name must begin with `claude/` and styled as `kebab-case` with at-most 4 words in length.
- Changes related to database migration (e.g. table structure change) MUST BE in `sik_modif.sql`. DO NOT CHANGE other `.sql` files. Their changes follow upstream repository.
- When modifying codes that touches `sekuel` and `validasi` class methods, usually named `Sequel` and `Valid` respectively (e.g. `Sequel.menyimpantf("dpjp_ranap", "?, ?, ?", 3, new String[]{"a", "b", "c"})`), look for its alternative in those class affixed by `Smc`. Carefully read the parameter requirements.
- In addition, adding new features must affix the name with SMC, where method name is styled "Smc", permission name is styled "_smc", and file/menu name is styled "SMC".
- Adding new parameter to `database.xml.example` MUST BE reflected in `src/fungsi/koneksiDB.java` class. By default, parameter values are unecrypted. Sensitive parameters such as secret keys must be encrypted. Therefore, reading the value from java counterpart requires decryption.
- Whenever you finish the task, make a draft PR and assign `rizky92` as assignee. Title PR must be translated into Indonesian language, but the body/description doesn't have to. If the PR is a fix for current issues or supersedes other PR, attach the issue/PR number in PR body. The PR title doesn't need to include standard prefix such as "fix" or "chore". A simple title is enough.

#### Building the UI
When building UI components, use primary components from `src/widget`. Following is the list of used components.
- `ScrollPane.java` - Custom JScrollPane.
- `Table.java` - Custom JTable.
- `TabPane.java` - Custom JTabbedPane.
- `Tanggal.java` - Custom JCalendar, uses `java.util.Date` for handling dates.
- `TextArea.java` - Custom JTextArea.
- `TextBox.java` - Custom JTextField.
- `Button.java` - Custom JButton.
- `ButtonBig.java` - Custom JButton, for menu items in `frmUtama`.
- `CekBox.java` - Custom JCheckBox.
- `ComboBox.java` - Custom JComboBox.
- `InternalFrame.java` - Act as a main wrapper to house components inside JDialog.
- `Label.java` - Custom JLabel.
- `PanelBiasa.java` - Custom JPanel.
- `PasswordBox.java` - Custom JPasswordField.

Any other components not stated will fallback to swing counterpart.
