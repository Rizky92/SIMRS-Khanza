# SIMRS Khanza Claude Instruction

## Project Overview
This is a home repository for modified SIMRS Khanza, a fork of original [SIMRS Khanza](https://github.com/mas-elkhanza/SIMRS-Khanza) developed by Yayasan Asosiasi SIMRS Khanza Indonesia (YASKI). This fork is designed to cater the needs for RS Samarinda Medika Citra should the origin doesn't have the feature it needed. The project mainly uses Java 17 as programming langauge but targets Java 11 for build and compiling, Jasper Report as reporting framework, NetBeans as primary IDE, and Java Swing as its main UI. It also uses PHP ^8.2 for specific web/API services.

### Important directories
Important root directories are listed below:  
| directory | description |
| --- | --- |
| `/anjunganpasienmandiri` | A self-checkout kiosk for patient registrations, bookings, and check-ins. |
| `/api-bpjsfktl` | An API service written in PHP for BPJS Kesehatan to establish queueing service and patient's operation schedule. |
| `/bridging-adamlabs` | A Laravel-based API service to connect laboratory exam result and transaction used for ADAMLABS into hospital server. |
| `/KhanzaAntrianApotek` | A subproject to manage pharmacy prescription queue. |
| `/KhanzaAntrianLoket` | A subproject to display and manage admission/registration queue. |
| `/KhanzaHMSServiceAplicare` | A subproject used to update Hospital Bed in BPJS Kesehatan, running as a service. |
| `/KhanzaHMSServiceMobileJKNERM` | A subproject used to update Queue service and Task ID for BPJS Kesehatan. Running as a service. |
| `/KhanzaHMSServiceSatuSehat` | A subproject used to record visits and patient history into Satu Sehat, a national health ministry platform for centralized patient's medical history, running as a service. |
| `/KhanzaPengenkripsiTeks` | A subproject used to encrypt/decrypt values in UI form. |
| `/mandiri` | An API service for managing Bank Mandiri service using host-to-host model. |
| `/nbproject` | Netbeans project configuration folder. |
| `/report` | Used to host jasper report designs and compiled report resources. |
| `/setting` | Used to load settings in XML format. |
| `/src` | The source code of main SIMRS Khanza project. |
| `/suara` | used to host sound resources. |
| `/webapps` | A web based application written in PHP, mainly used to support billing note printing, file-based medical history, eklaim bridging service, human resource management, and various services to document proof of actions. Used internally in SIMRS Khanza project itself. |

#### `/src` core architecture
`/src` has various modules. These are what they're used for:  
| directory | description |
| --- | --- |
| `src/48x48` | Used to place image resources for main menu icons. |
| `src/bridging` | Modules for various bridging to 3rd party services, for example BPJS Kesehatan, SatuSehat. |
| `src/dapur` | Modules for kitchen inventory management. |
| `src/fungsi` | Helper functions |
| `src/grafikanalisa` | Modules to display various graphics. |
| `src/informasi` | Modules to display general public information (e.g. bed status, doctor's practic schedule). |
| `src/inventaris` | Modules used for asset, CSSD, building, and waste management. |
| `src/inventory` | Modules for pharmacy-related general inventory management. |
| `src/ipsrs` | Modules used for non-medical and consumable inventory management. |
| `src/kepegawaian` | Modules for managing human resources and auditing for various worker-related incidents. |
| `src/keuangan` | Modules for accounting management. |
| `src/laporan` | Modules for medical statistic reporting. |
| `src/parkir` | Modules for parking management. |
| `src/pcraicra` | Modules for general construction related management. |
| `src/permintaan` | Modules for managing medical service requests. |
| `src/perpustakaan` | Modules for library/book management. |
| `src/picture` | Used to place image resources for various UI components. |
| `src/rekammedis` | Modules for managing and record general patient medical history. |
| `src/restore` | Modules for restoring deleted records from various menu. |
| `src/setting` | Modules for administrator to control the settings for application uses. |
| `src/simrskhanza` | Modules for main modules of SIMRS Khanza. |
| `src/smc` | SMC specific modules, either for replacing existing forms or additional helpers. |
| `src/surat` | Modules for managing letter and administrations. |
| `src/toko` | Modules for managing internal shop. |
| `src/tranfusidarah` | Modules for blood transfusion management. |
| `src/viabarcode` | Modules for accessing various services quickly using barcode scanner. |
| `src/widget` | Houses various UI components used internally. |
| `src/ziscsr` | Modules for managing charities and donations. |

The following files are SENSITIVE, but they are still needed in order to run:  
| file | description |
| --- | --- |
|`setting/database.xml` | An origin copy of `setting/database.xml.example`. Not needed in the repo. |
|`webapps/conf/conf.php` | Contains database connection configuration and security login for non-user interaction. Needed for reference in repo but highly sensitive. |
|`webapps/inacbg/conf/wsinacbg(2).php` | Contains API Key for bridging claim. Although the connection to eklaim server is within local environment, this is still sensitive information. Needed for reference in repo. |

Unless specified otherwise, these modules follow Netbeans' regular swing forms. So each files in the modules have their `.form` counterpart. These `.form` files are the design file used by Netbeans using XML structure with XML-like formatting. The design must synchronize their java counterpart from a method called `initComponents()`, as well as any events must synchronized to its method marked with `//GEN-FIRST:event_<event method name>` after opening curly brace and `//GEN-LAST:event<event method name>` after closing curly brace. Changes in this method should be reflected to their `.form` counterpart and vice versa.

If you're making plans for new menu, describe the general window layout you're going to design if you can. This should help user understand what you're going for designing the form in case it's broken when previewed in NetBeans.
> [!NOTE]  
> If you have access to Netbeans MCP, you can edit the `.form` files first if you understand its code, then request Netbeans to open the java counterpart for you using `openFile` tool. This way you don't have to mingle with re-syncing to `initComponents()` and any other generated event methods.

### General coding guidelines
The coding guidelines should cater to users' Netbeans configuration, which as follows.
- Use 4 spaces as indentation.
- No hard line wrap. Soft line wrap is around 200-300 characters.
- Trim trailing whitespaces.
- Package imports are sorted alphabetically, case sensitive.
- Use Yoda-style for object equality checks.

#### Workflow guideline
- DO NOT add line comments to the code. Existing comments are unaffected by this rule.
- If you're iterating a plan, ensure current branch name begins with `custom-` followed by date in `yyyy-MM-dd` format. Branch date must be the latest version.
- After you start editing, then switch to a new branch. Branch name must starts with `c/` and followed by the title, styled as `kebab-case` with at-most 4 words in length. DO NOT force create a branch name if the branch of the same name already exists.
- Changes related to database structure change MUST BE in `sik_modif.sql`. DO NOT CHANGE other `.sql` files. Their changes are either follow upstream repository or contains referenced data dump. Ensure any addition is sorted alphabetically, in snake_case by table name. If the table contains foreign keys that are dependant of other tables where current table name is BEFORE the related table name, then add it like so:
    ```sql
    CREATE TABLE IF NOT EXISTS `current_table`  (
        -- ...
        `column_from_related_table` varchar(255) NOT NULL,
        -- ...
        INDEX `column_from_related_table`(`column_from_related_table`) USING BTREE,
        -- ...
    ) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

    -- ...

    CREATE TABLE IF NOT EXISTS `related_table`  (
        `column_from_related_table` varchar(255) NOT NULL,
        -- ...
        PRIMARY KEY (`column_from_related_table`),
        -- ...
    ) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

    -- foreign key constraints are added and sorted alphabetically by table names
    ALTER TABLE `current_table` ADD CONSTRAINT `<constraint name>` FOREIGN KEY IF NOT EXISTS (`column_of_related_table`) ON DELETE RESTRICT ON UPDATE RESTRICT;

    -- ...
    ```
- When modifying codes that touches `sekuel` and `validasi` class methods, usually named `Sequel` and `Valid` respectively (e.g. `Sequel.menyimpantf("dpjp_ranap", "?, ?, ?", 3, new String[]{"a", "b", "c"})`), look for its alternative in those class affixed by `Smc`. Carefully read the parameter requirements.
- In addition, adding new features must affix the name with SMC, where method and property names are styled "Smc", permission and table names are styled "_smc", and file/class/static property/database.xml parameter names are styled "SMC". Menus, form titles, or local declarations do not require affixes. Exception to classes whose name is already affixed by SMC, in which case method or (static) property names do not need affixes.
- Adding new parameter to `database.xml.example` MUST BE reflected in `src/fungsi/koneksiDB.java` class. By default, parameter values are unecrypted. Sensitive parameters such as secret keys must be encrypted. Therefore, reading the value from java counterpart requires decryption first. Added Parameters are append-only, positioned before `WAHOST` for `database.xml.example` and before `HOST()` in `src/fungsi/koneksiDB.java`.
- Acronyms (e.g. SEP, KFA, INACBG, IDRG, or API) must be in ALL UPPERCASE. Exceptions for Smc-affixed methods, as a name in column or table, or as named keys, in which case they're styled `snake_case`.
- After finishing the task. DO NOT make a commit.

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
