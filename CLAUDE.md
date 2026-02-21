# SIMRS Khanza Claude Instructions

## Project Overview
This is a home repository for modified SIMRS Khanza, a fork of original [SIMRS Khanza](https://github.com/mas-elkhanza/SIMRS-Khanza) developed by YASKI. This fork is designed to cater the needs for RS Samarinda Medika Citra should the origin doesn't have the features it needed. The project uses java 17 as primary programming langauge, NetBeans as primary IDE, and Java Swing as its main UI.

### Important directories
Important root directories are listed below
- `/anjunganpasienmandiri`: A self-checkout kiosk for patient registrations, bookings, and check-ins.
- `/api-bpjsfktl`: An API service written in PHP for BPJS kesehatan to connect to hospital server.
- `/bridging-adamlabs`: A Laravel-based API service to bridge laboratory result and transaction used for ADAMLABS to connect to hospital server
- `/KhanzaAntrianLoket`: A subproject to display admission/registration queue.
- `/KhanzaHMSServiceAplicare`: A subproject used to update Hospital Bed in BPJS Kesehatan, running as a service.
- `/KhanzaHMSServiceMobileJKNERM`: A subproject used to update Queue service and Task ID for BPJS Kesehatan. Running as a service.
- `/KhanzaHMSServiceSatuSehat`: A subproject used to record visits and patient history into Satu Sehat, a national health ministry platform for centralized patient's medical history, running as a service.
- `/KhanzaPengenkripsiTeks`: A subproject used to encrypt/decrypt values in UI form.
- `/nbproject`: Netbeans project configuration folder.
- `/report`: Used to host jasper report designs and compiled report resources.
- `/setting`: Used to load settings in XML format.
- `/src`: The source code of main SIMRS Khanza project.
- `/suara`: used to host sound resources.
- `/webapps`: A web based application written in PHP, mainly used to support billing note printing, file-based medical history, eklaim bridging service, and various services to document proof of actions. Used internally in SIMRS Khanza project itself and not user-facing.

#### `/src` core architecture
`/src` has various modules. These are what they're used for.
- `src/48x48`: Used to place image resources for main menu icons.
- `src/bridging`: Modules for various bridging to 3rd party services, for example BPJS Kesehatan, SatuSehat.
- `src/dapur`: Modules for kitchen inventory management.
- `src/fungsi`: Helper functions
- `src/grafikanalisa`: Modules to display various graphics.
- `src/informasi`: Modules to display general information, for example bed status, doctor's practic schedule.
- `src/inventaris`: Modules used for general tools inventory management.
- `src/inventory`: Modules for pharmacy-related general inventory management.
- `src/ipsrs`: Modules used for non-medical and consumables inventory management.
- `src/kepegawaian`: Modules for managing human resources, CSSD, and general auditing for various worker-related incidents.
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
- `src/support`: Another helper functions, this was used for developer's need which may destruct changes from upstream repository.
- `src/surat`: Modules for managing letter and administrations.
- `src/toko`: Modules for managing internal shop.
- `src/tranfusidarah`: Modules for blood transfusion management.
- `src/viabarcode`: Modules for accessing various services quickly using barcode scanner.
- `src/widget`: Houses various UI components used internally.
- `src/ziscsr`: Modules for managing zakat, infaq, and shadaqah.

Unless specified otherwise, these modules follow Netbeans' regular swing forms. So each files in the modules have their `.form` counterpart. Inside the `.java` files, each have `initComponents()`. Changes in this method should be reflected to their `.form` counterpart.

### General coding guidelines
The coding guidelines should cater to users' netbeans configuration, which as follows.
- Use 4 spaces as indentation
- No hard line wrap. Soft line wrap is around 200-300 characters.

#### User's specific coding guidelines
- Before starting the changes, ensure current branch is in `custom` branch. Then switch to new branch. Branch format name must begin with `claude/` and styled as `kebab-case` with at-most 4 words in length.
- Changes related to database migration (e.g. table structure change) MUST BE in `sik_modif.sql`. DO NOT CHANGE other `.sql` files. Their changes follow upstream repository.
- When modifying codes that touches `sekuel` and `validasi` class methods, usually named `Sequel` and `Valid` respectively (e.g. `Sequel.menyimpantf("dpjp_ranap", "?, ?, ?", 3, new String[]{"a", "b", "c"})`), look for its alternative in those class affixed by `Smc`. Carefully read the parameter requirement.
- Adding new parameter to `database.xml.example` MUST BE reflected in `src/fungsi/koneksiDB.java` class. By default, parameter values are unecrypted.
