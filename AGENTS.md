# SIMRS Khanza Agent Instruction

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

Unless specified otherwise, these modules follow Netbeans' regular swing forms, so each file in the modules has a `.form` counterpart that must stay synchronized with its java counterpart. See [`docs/agent/swing-forms.md`](docs/agent/swing-forms.md) for the sync workflow, the NetBeans MCP steps, and the layout metrics.

### General coding guidelines
The coding guidelines should cater to users' Netbeans configuration, which as follows.
- Use 4 spaces as indentation.
- No hard line wrap. Soft line wrap is around 200-300 characters.
- Trim trailing whitespaces.
- Package imports are sorted alphabetically, case sensitive.
- Use Yoda-style for object equality check.

#### Workflow guidelines
- DO NOT add line comments to the code. Existing comments are unaffected by this rule.
- After completing the task. DO NOT make a commit.
- Which branch to work on, when to create one, and how to name it: see [`docs/agent/branching.md`](docs/agent/branching.md).
- Database structure changes (`sik_modif.sql` only, ordering, foreign keys): see [`docs/agent/db-schema-change.md`](docs/agent/db-schema-change.md).
- SMC affix naming, `Sequel`/`Valid` Smc alternatives, acronym casing, and `database.xml.example` parameters: see [`docs/agent/smc-conventions.md`](docs/agent/smc-conventions.md).

#### Building the UI
Use the primary components from `src/widget`, along with the sizing, spacing, titled border, and CRUD action row conventions in [`docs/agent/swing-forms.md`](docs/agent/swing-forms.md).
