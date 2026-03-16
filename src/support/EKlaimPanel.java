package support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class EKlaimPanel extends widget.PanelBiasa {

    private final sekuel Sequel = new sekuel();
    private final Connection koneksi = koneksiDB.condb();
    private EKlaimService service;

    private String noRawat = "", noSep = "", coderNik = "";
    private int flagKlaim = -1;
    private int flagInacbgTopup = -1;
    private boolean corona = false;

    // Patient info labels
    private widget.Label valNoRawat, valNoSep, valNoRm, valNoKartu, valNamaPasien,
            valJk, valAlamat, valTglRegistrasi, valPoli, valDokter, valStatus;

    // Editable fields
    private widget.TextBox txtTglKeluar, txtIcuLos, txtVentilatorHour, txtUpgradeClassLos,
            txtAddPaymentPct, txtBirthWeight, txtSistole, txtDiastole, txtNoSitb;
    private widget.ComboBox cmbKelasRawat, cmbCaraMasuk, cmbAdlSubAcute, cmbAdlChronic,
            cmbIcuIndikator, cmbUpgradeClassInd, cmbUpgradeClassClass, cmbDischargeStatus,
            cmbDializerSingleUse;

    // Billing fields
    private widget.TextBox txtProsedurNonBedah, txtProsedurBedah, txtKonsultasi, txtTenagaAhli,
            txtKeperawatan, txtPenunjang, txtRadiologi, txtLaboratorium, txtPelayananDarah,
            txtKamar, txtRawatIntensif, txtObatKronis, txtObatKemoterapi,
            txtObat, txtAlkes, txtBmhp, txtSewaAlat, txtRehabilitasi, txtTarifPoliEks;
    private widget.TextBox txtDiscProsedurNonBedah, txtDiscProsedurBedah, txtDiscKonsultasi, txtDiscTenagaAhli,
            txtDiscKeperawatan, txtDiscPenunjang, txtDiscRadiologi, txtDiscLaboratorium, txtDiscPelayananDarah,
            txtDiscKamar, txtDiscRawatIntensif, txtDiscObatKronis, txtDiscObatKemoterapi,
            txtDiscObat, txtDiscAlkes, txtDiscBmhp, txtDiscSewaAlat, txtDiscRehabilitasi, txtDiscTarifPoliEks;
    private widget.Label lblTotalRincian, lblTotalBilling;

    // New parameter fields
    private widget.ComboBox cmbVentilatorUseInd;
    private widget.TextBox txtVentilatorStartDttm, txtVentilatorStopDttm;
    private widget.PanelBiasa panelVentilator;
    private widget.ComboBox cmbUpgradeClassPayor;
    private widget.ComboBox cmbBayiLahirStatusCd;
    private widget.TextBox txtKantongDarah;
    private widget.ComboBox cmbAlteplaseInd;
    private widget.PanelBiasa panelApgar;
    private widget.ComboBox cmbApgarM1Appearance, cmbApgarM1Pulse, cmbApgarM1Grimace, cmbApgarM1Activity, cmbApgarM1Respiration;
    private widget.ComboBox cmbApgarM5Appearance, cmbApgarM5Pulse, cmbApgarM5Grimace, cmbApgarM5Activity, cmbApgarM5Respiration;
    private widget.PanelBiasa panelPersalinan;
    private widget.TextBox txtUsiaKehamilan, txtGravida, txtPartus, txtAbortus;
    private widget.ComboBox cmbOnsetKontraksi;
    private widget.PanelBiasa panelDeliveryList;
    private final List<DeliveryRow> deliveryRows = new ArrayList<>();

    // Status displays
    private widget.Label lblStatusText, lblIdrgMdc, lblIdrgDrgCode, lblIdrgDrgDesc,
            lblInacbgCode, lblInacbgDesc, lblInacbgTarif, lblInacbgTopUp;

    // Stage 2 special CMG
    private widget.ComboBox cmbSpecialProcedure, cmbSpecialProsthesis, cmbSpecialInvestigation, cmbSpecialDrug;
    private widget.PanelBiasa panelStage2;

    // COVID fields
    private widget.PanelBiasa panelCovid;
    private widget.ComboBox cmbPemulasaraanJenazah, cmbKantongJenazah, cmbPetiJenazah,
            cmbPlastikErat, cmbDesinfektanJenazah, cmbMobilJenazah, cmbDesinfektanMobilJenazah,
            cmbCovid19StatusCd, cmbCovid19CcInd;
    private widget.TextBox txtNomorKartuT, txtEpisodes1, txtEpisodes2, txtEpisodes3,
            txtEpisodes4, txtEpisodes5, txtEpisodes6;

    // Action buttons
    private widget.Button btnAction, btnEditKlaim, btnCetakKlaim, btnKirimDC;
    private widget.PanelBiasa panelActionButtons;

    private Runnable onRefreshCallback;

    public EKlaimPanel() {
        setLayout(new BorderLayout());
        initService();
        buildUI();
    }

    private void initService() {
        String wsUrl = koneksiDB.EKLAIMWSURL();
        String key = koneksiDB.EKLAIMKEY();
        if (!wsUrl.isEmpty() && !key.isEmpty()) {
            service = new EKlaimService(wsUrl, key);
        }
    }

    public void setOnRefreshCallback(Runnable callback) {
        this.onRefreshCallback = callback;
    }

    public void tampil(String noSep, String noRawat, String coderNik, int flagKlaim, int flagInacbgTopup) {
        this.noSep = noSep;
        this.noRawat = noRawat;
        this.coderNik = coderNik;
        this.flagKlaim = flagKlaim;
        this.flagInacbgTopup = flagInacbgTopup;

        if (service == null) {
            initService();
        }

        lblStatusText.setText("Memuat data...");
        lblStatusText.setForeground(Color.GRAY);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Void, Void>() {
            private boolean coronaResult = false;
            private EKlaimAutoFiller.ClaimData claimData;
            private EKlaimBillingAggregator.BillingData billingData;

            @Override
            protected Void doInBackground() {
                System.out.println("[EKlaim] tampil: noSep=" + noSep + ", noRawat=" + noRawat + ", coderNik='" + coderNik + "', flagKlaim=" + flagKlaim);
                coronaResult = Sequel.cariExistsSmc("select * from perawatan_corona where perawatan_corona.no_rawat = ?", noRawat);
                if (flagKlaim <= 0 || flagKlaim >= 6) {
                    claimData = EKlaimAutoFiller.fill(noSep);
                    billingData = EKlaimBillingAggregator.aggregate(claimData.noRawat.isEmpty() ? noRawat : claimData.noRawat);
                }
                return null;
            }

            @Override
            protected void done() {
                corona = coronaResult;
                setCursor(Cursor.getDefaultCursor());
                switch (flagKlaim) {
                    case 1: showSelesai(); break;
                    case 2: showInacbgFinal(); break;
                    case 3:
                        if (flagInacbgTopup == 1) {
                            showInacbgStage2();
                        } else {
                            showInacbgGrouping();
                        }
                        break;
                    case 4: showIdrgFinal(); break;
                    case 5: showIdrgGrouping(); break;
                    default: showFormKlaimWithData(claimData, billingData); break;
                }
            }
        }.execute();
    }

    private void buildUI() {
        widget.PanelBiasa mainPanel = new widget.PanelBiasa(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        Font valueFont = new Font("Tahoma", Font.BOLD, 11);
        Font sectionFont = new Font("Tahoma", Font.BOLD, 12);

        // Status
        lblStatusText = createLeftLabel();
        lblStatusText.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(lblStatusText, gbc);
        row++;

        // Patient Info Section
        widget.Label headerPatient = createLeftLabel();
        headerPatient.setText("Data Pasien");
        headerPatient.setFont(sectionFont);
        headerPatient.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerPatient, gbc);
        row++;

        valNoRawat = createLeftLabel(); valNoSep = createLeftLabel(); valNoRm = createLeftLabel();
        valNoKartu = createLeftLabel(); valNamaPasien = createLeftLabel(); valJk = createLeftLabel();
        valAlamat = createLeftLabel(); valTglRegistrasi = createLeftLabel(); valPoli = createLeftLabel();
        valDokter = createLeftLabel(); valStatus = createLeftLabel();

        widget.Label[] patientLabels = { newLabel("No. Rawat"), newLabel("No. SEP"), newLabel("No. RM"),
                newLabel("No. Kartu Peserta"), newLabel("Nama Pasien"), newLabel("Jenis Kelamin"),
                newLabel("Alamat"), newLabel("Tgl. Registrasi"), newLabel("Poliklinik"),
                newLabel("Dokter DPJP"), newLabel("Status") };
        widget.Label[] patientValues = { valNoRawat, valNoSep, valNoRm, valNoKartu, valNamaPasien,
                valJk, valAlamat, valTglRegistrasi, valPoli, valDokter, valStatus };

        for (int i = 0; i < patientLabels.length; i++) {
            patientValues[i].setFont(valueFont);
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
            mainPanel.add(patientLabels[i], gbc);
            gbc.gridx = 1; gbc.weightx = 0;
            mainPanel.add(newLabel(":"), gbc);
            gbc.gridx = 2; gbc.weightx = 0.8;
            mainPanel.add(patientValues[i], gbc);
            row++;
        }

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        // Clinical Fields Section
        widget.Label headerClinical = createLeftLabel();
        headerClinical.setText("Data Klaim");
        headerClinical.setFont(sectionFont);
        headerClinical.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerClinical, gbc);
        row++;

        txtTglKeluar = new widget.TextBox();
        cmbKelasRawat = buildCombo("1", "2", "3");
        cmbCaraMasuk = buildCombo("gp", "hosp-trans", "mp", "outp", "inp", "emd", "born", "nursing", "psych", "rehab", "other");
        cmbAdlSubAcute = buildAdlCombo();
        cmbAdlChronic = buildAdlCombo();
        cmbIcuIndikator = buildCombo("0", "1");
        txtIcuLos = newTextBox("0");
        txtVentilatorHour = newTextBox("0");
        cmbUpgradeClassInd = buildCombo("0", "1");
        cmbUpgradeClassClass = buildCombo("", "kelas_1", "kelas_2", "vip", "vvip");
        txtUpgradeClassLos = newTextBox("0");
        txtAddPaymentPct = newTextBox("0");
        txtBirthWeight = new widget.TextBox();
        txtSistole = newTextBox("120");
        txtDiastole = newTextBox("90");
        cmbDischargeStatus = buildCombo("1", "2", "3", "4", "5");
        txtNoSitb = new widget.TextBox();
        cmbDializerSingleUse = buildCombo("0", "1");

        String[][] clinicalFields = {
            {"Tgl. Keluar", "txtTglKeluar"},
            {"Kelas Rawat", "cmbKelasRawat"},
            {"Cara Masuk", "cmbCaraMasuk"},
            {"ADL Sub Acute", "cmbAdlSubAcute"},
            {"ADL Chronic", "cmbAdlChronic"},
            {"ICU Indikator", "cmbIcuIndikator"},
            {"ICU Los", "txtIcuLos"},
            {"Ventilator Hour", "txtVentilatorHour"},
            {"Indikator Upgrade Kelas", "cmbUpgradeClassInd"},
            {"Naik ke Kelas", "cmbUpgradeClassClass"},
            {"Lama Hari Naik Kelas", "txtUpgradeClassLos"},
            {"Biaya Tambahan", "txtAddPaymentPct"},
            {"Berat Saat Lahir", "txtBirthWeight"},
            {"Sistole", "txtSistole"},
            {"Diastole", "txtDiastole"},
            {"Status Pulang", "cmbDischargeStatus"},
            {"No. Regist SITB", "txtNoSitb"},
            {"Dializer Single Use", "cmbDializerSingleUse"},
        };

        java.util.Map<String, java.awt.Component> fieldMap = new java.util.LinkedHashMap<>();
        fieldMap.put("txtTglKeluar", txtTglKeluar);
        fieldMap.put("cmbKelasRawat", cmbKelasRawat);
        fieldMap.put("cmbCaraMasuk", cmbCaraMasuk);
        fieldMap.put("cmbAdlSubAcute", cmbAdlSubAcute);
        fieldMap.put("cmbAdlChronic", cmbAdlChronic);
        fieldMap.put("cmbIcuIndikator", cmbIcuIndikator);
        fieldMap.put("txtIcuLos", txtIcuLos);
        fieldMap.put("txtVentilatorHour", txtVentilatorHour);
        fieldMap.put("cmbUpgradeClassInd", cmbUpgradeClassInd);
        fieldMap.put("cmbUpgradeClassClass", cmbUpgradeClassClass);
        fieldMap.put("txtUpgradeClassLos", txtUpgradeClassLos);
        fieldMap.put("txtAddPaymentPct", txtAddPaymentPct);
        fieldMap.put("txtBirthWeight", txtBirthWeight);
        fieldMap.put("txtSistole", txtSistole);
        fieldMap.put("txtDiastole", txtDiastole);
        fieldMap.put("cmbDischargeStatus", cmbDischargeStatus);
        fieldMap.put("txtNoSitb", txtNoSitb);
        fieldMap.put("cmbDializerSingleUse", cmbDializerSingleUse);

        for (String[] field : clinicalFields) {
            widget.Label lbl = newLabel(field[0]);
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
            mainPanel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0;
            mainPanel.add(newLabel(":"), gbc);
            gbc.gridx = 2; gbc.weightx = 0.8;
            mainPanel.add(fieldMap.get(field[1]), gbc);
            row++;
        }

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        // Billing Section
        widget.Label headerBilling = createLeftLabel();
        headerBilling.setText("Rincian Biaya");
        headerBilling.setFont(sectionFont);
        headerBilling.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerBilling, gbc);
        row++;

        // Billing header
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        mainPanel.add(createBoldLabel("Kategori"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        mainPanel.add(createBoldLabel("Nilai"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.35;
        mainPanel.add(createBoldLabel("Diskon"), gbc);
        row++;

        txtProsedurNonBedah = newTextBox("0");
        txtProsedurBedah = newTextBox("0");
        txtKonsultasi = newTextBox("0");
        txtTenagaAhli = newTextBox("0");
        txtKeperawatan = newTextBox("0");
        txtPenunjang = newTextBox("0");
        txtRadiologi = newTextBox("0");
        txtLaboratorium = newTextBox("0");
        txtPelayananDarah = newTextBox("0");
        txtKamar = newTextBox("0");
        txtRawatIntensif = newTextBox("0");
        txtObatKronis = newTextBox("0");
        txtObatKemoterapi = newTextBox("0");
        txtObat = newTextBox("0");
        txtAlkes = newTextBox("0");
        txtBmhp = newTextBox("0");
        txtSewaAlat = newTextBox("0");
        txtRehabilitasi = newTextBox("0");
        txtTarifPoliEks = newTextBox("0");

        txtDiscProsedurNonBedah = newTextBox("0");
        txtDiscProsedurBedah = newTextBox("0");
        txtDiscKonsultasi = newTextBox("0");
        txtDiscTenagaAhli = newTextBox("0");
        txtDiscKeperawatan = newTextBox("0");
        txtDiscPenunjang = newTextBox("0");
        txtDiscRadiologi = newTextBox("0");
        txtDiscLaboratorium = newTextBox("0");
        txtDiscPelayananDarah = newTextBox("0");
        txtDiscKamar = newTextBox("0");
        txtDiscRawatIntensif = newTextBox("0");
        txtDiscObatKronis = newTextBox("0");
        txtDiscObatKemoterapi = newTextBox("0");
        txtDiscObat = newTextBox("0");
        txtDiscAlkes = newTextBox("0");
        txtDiscBmhp = newTextBox("0");
        txtDiscSewaAlat = newTextBox("0");
        txtDiscRehabilitasi = newTextBox("0");
        txtDiscTarifPoliEks = newTextBox("0");

        String[] billingNames = {"Prosedur Non Bedah", "Prosedur Bedah", "Konsultasi", "Tenaga Ahli",
                "Keperawatan", "Penunjang", "Radiologi", "Laboratorium", "Pelayanan Darah",
                "Kamar/Akomodasi", "Rawat Intensif", "Obat Kronis", "Obat Kemoterapi",
                "Obat", "Alkes", "BMHP", "Sewa Alat", "Rehabilitasi", "Tarif Poli Eksekutif"};
        widget.TextBox[] billingValues = {txtProsedurNonBedah, txtProsedurBedah, txtKonsultasi, txtTenagaAhli,
                txtKeperawatan, txtPenunjang, txtRadiologi, txtLaboratorium, txtPelayananDarah,
                txtKamar, txtRawatIntensif, txtObatKronis, txtObatKemoterapi,
                txtObat, txtAlkes, txtBmhp, txtSewaAlat, txtRehabilitasi, txtTarifPoliEks};
        widget.TextBox[] billingDiscounts = {txtDiscProsedurNonBedah, txtDiscProsedurBedah, txtDiscKonsultasi, txtDiscTenagaAhli,
                txtDiscKeperawatan, txtDiscPenunjang, txtDiscRadiologi, txtDiscLaboratorium, txtDiscPelayananDarah,
                txtDiscKamar, txtDiscRawatIntensif, txtDiscObatKronis, txtDiscObatKemoterapi,
                txtDiscObat, txtDiscAlkes, txtDiscBmhp, txtDiscSewaAlat, txtDiscRehabilitasi, txtDiscTarifPoliEks};

        for (widget.TextBox tb : billingValues) {
            tb.setDocument(new batasInput(20).getOnlyAngka(tb));
            tb.setText("0");
        }
        for (widget.TextBox tb : billingDiscounts) {
            tb.setDocument(new batasInput(20).getOnlyAngka(tb));
            tb.setText("0");
        }

        widget.TextBox.CustomDocumentListener billingListener = e -> recalculateBilling();

        for (int i = 0; i < billingNames.length; i++) {
            widget.Label lbl = newLabel(billingNames[i]);
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.35;
            mainPanel.add(billingValues[i], gbc);
            gbc.gridx = 2; gbc.weightx = 0.35;
            mainPanel.add(billingDiscounts[i], gbc);
            billingValues[i].getDocument().addDocumentListener(billingListener);
            billingDiscounts[i].getDocument().addDocumentListener(billingListener);
            row++;
        }

        // Total row
        lblTotalRincian = createLeftLabel();
        lblTotalRincian.setText("Total Rincian: 0");
        lblTotalRincian.setFont(valueFont);
        lblTotalBilling = createLeftLabel();
        lblTotalBilling.setText("Total Billing: 0");
        lblTotalBilling.setFont(valueFont);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(lblTotalRincian, gbc);
        gbc.gridx = 2; gbc.gridwidth = 1;
        mainPanel.add(lblTotalBilling, gbc);
        row++;

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        // COVID Section (hidden by default)
        panelCovid = new widget.PanelBiasa(new GridBagLayout());
        panelCovid.setBorder(BorderFactory.createTitledBorder("Data COVID-19"));
        panelCovid.setVisible(false);

        cmbPemulasaraanJenazah = buildCombo("0", "1");
        cmbKantongJenazah = buildCombo("0", "1");
        cmbPetiJenazah = buildCombo("0", "1");
        cmbPlastikErat = buildCombo("0", "1");
        cmbDesinfektanJenazah = buildCombo("0", "1");
        cmbMobilJenazah = buildCombo("0", "1");
        cmbDesinfektanMobilJenazah = buildCombo("0", "1");
        cmbCovid19StatusCd = buildCombo("1", "2", "3");
        cmbCovid19CcInd = buildCombo("0", "1");
        txtNomorKartuT = new widget.TextBox();
        txtEpisodes1 = newTextBox("0");
        txtEpisodes2 = newTextBox("0");
        txtEpisodes3 = newTextBox("0");
        txtEpisodes4 = newTextBox("0");
        txtEpisodes5 = newTextBox("0");
        txtEpisodes6 = newTextBox("0");

        GridBagConstraints gcov = new GridBagConstraints();
        gcov.insets = new Insets(2, 5, 2, 5);
        gcov.fill = GridBagConstraints.HORIZONTAL;
        gcov.anchor = GridBagConstraints.WEST;
        int covRow = 0;

        String[][] covidFields = {
            {"Pemulasaraan Jenazah", "cmbPemulasaraanJenazah"},
            {"Kantong Jenazah", "cmbKantongJenazah"},
            {"Peti Jenazah", "cmbPetiJenazah"},
            {"Plastik Erat", "cmbPlastikErat"},
            {"Desinfektan Jenazah", "cmbDesinfektanJenazah"},
            {"Mobil Jenazah", "cmbMobilJenazah"},
            {"Desinfektan Mobil Jenazah", "cmbDesinfektanMobilJenazah"},
            {"Status Covid-19", "cmbCovid19StatusCd"},
            {"No. Jaminan/NIK/KITAS", "txtNomorKartuT"},
            {"Episodes 1 (ICU + Ventilator)", "txtEpisodes1"},
            {"Episodes 2 (ICU - Ventilator)", "txtEpisodes2"},
            {"Episodes 3 (Iso Neg + Ventilator)", "txtEpisodes3"},
            {"Episodes 4 (Iso Neg - Ventilator)", "txtEpisodes4"},
            {"Episodes 5 (Iso Non-Neg + Ventilator)", "txtEpisodes5"},
            {"Episodes 6 (Iso Non-Neg - Ventilator)", "txtEpisodes6"},
            {"Ada Comorbid/Complexity", "cmbCovid19CcInd"},
        };

        java.util.Map<String, java.awt.Component> covidFieldMap = new java.util.LinkedHashMap<>();
        covidFieldMap.put("cmbPemulasaraanJenazah", cmbPemulasaraanJenazah);
        covidFieldMap.put("cmbKantongJenazah", cmbKantongJenazah);
        covidFieldMap.put("cmbPetiJenazah", cmbPetiJenazah);
        covidFieldMap.put("cmbPlastikErat", cmbPlastikErat);
        covidFieldMap.put("cmbDesinfektanJenazah", cmbDesinfektanJenazah);
        covidFieldMap.put("cmbMobilJenazah", cmbMobilJenazah);
        covidFieldMap.put("cmbDesinfektanMobilJenazah", cmbDesinfektanMobilJenazah);
        covidFieldMap.put("cmbCovid19StatusCd", cmbCovid19StatusCd);
        covidFieldMap.put("txtNomorKartuT", txtNomorKartuT);
        covidFieldMap.put("txtEpisodes1", txtEpisodes1);
        covidFieldMap.put("txtEpisodes2", txtEpisodes2);
        covidFieldMap.put("txtEpisodes3", txtEpisodes3);
        covidFieldMap.put("txtEpisodes4", txtEpisodes4);
        covidFieldMap.put("txtEpisodes5", txtEpisodes5);
        covidFieldMap.put("txtEpisodes6", txtEpisodes6);
        covidFieldMap.put("cmbCovid19CcInd", cmbCovid19CcInd);

        for (String[] field : covidFields) {
            widget.Label covLbl = newLabel(field[0]);
            gcov.gridwidth = 1;
            gcov.gridx = 0; gcov.gridy = covRow; gcov.weightx = 0.3;
            panelCovid.add(covLbl, gcov);
            gcov.gridx = 1; gcov.weightx = 0;
            panelCovid.add(newLabel(":"), gcov);
            gcov.gridx = 2; gcov.weightx = 0.7;
            panelCovid.add(covidFieldMap.get(field[1]), gcov);
            covRow++;
        }

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelCovid, gbc);
        row++;

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        // New Parameter Sections
        row = buildNewParameterSections(mainPanel, gbc, row);

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        // IDRG Results Section
        widget.Label headerIdrg = createLeftLabel();
        headerIdrg.setText("Hasil Grouping IDRG");
        headerIdrg.setFont(sectionFont);
        headerIdrg.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerIdrg, gbc);
        row++;

        lblIdrgMdc = createLeftLabel(); lblIdrgMdc.setText("-");
        lblIdrgDrgCode = createLeftLabel(); lblIdrgDrgCode.setText("-");
        lblIdrgDrgDesc = createLeftLabel(); lblIdrgDrgDesc.setText("-");

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("MDC"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblIdrgMdc, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("DRG Code"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblIdrgDrgCode, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("DRG Desc"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblIdrgDrgDesc, gbc); row++;

        // INACBG Results Section
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        widget.Label headerInacbg = createLeftLabel();
        headerInacbg.setText("Hasil Grouping INACBG");
        headerInacbg.setFont(sectionFont);
        headerInacbg.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerInacbg, gbc);
        row++;

        lblInacbgCode = createLeftLabel(); lblInacbgCode.setText("-");
        lblInacbgDesc = createLeftLabel(); lblInacbgDesc.setText("-");
        lblInacbgTarif = createLeftLabel(); lblInacbgTarif.setText("-");
        lblInacbgTopUp = createLeftLabel(); lblInacbgTopUp.setText("-");

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("CBG Code"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblInacbgCode, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("Deskripsi"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblInacbgDesc, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("Tarif"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblInacbgTarif, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(newLabel("Top Up"), gbc);
        gbc.gridx = 1; mainPanel.add(newLabel(":"), gbc);
        gbc.gridx = 2; mainPanel.add(lblInacbgTopUp, gbc); row++;

        // Stage 2 panel (hidden by default)
        panelStage2 = new widget.PanelBiasa(new GridBagLayout());
        panelStage2.setBorder(BorderFactory.createTitledBorder("Special CMG Selection"));
        panelStage2.setVisible(false);
        cmbSpecialProcedure = new widget.ComboBox();
        cmbSpecialProsthesis = new widget.ComboBox();
        cmbSpecialInvestigation = new widget.ComboBox();
        cmbSpecialDrug = new widget.ComboBox();

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(2, 5, 2, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0; gbc2.gridy = 0; panelStage2.add(newLabel("Special Procedure:"), gbc2);
        gbc2.gridx = 1; panelStage2.add(cmbSpecialProcedure, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 1; panelStage2.add(newLabel("Prosthesis:"), gbc2);
        gbc2.gridx = 1; panelStage2.add(cmbSpecialProsthesis, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 2; panelStage2.add(newLabel("Investigation:"), gbc2);
        gbc2.gridx = 1; panelStage2.add(cmbSpecialInvestigation, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 3; panelStage2.add(newLabel("Drug:"), gbc2);
        gbc2.gridx = 1; panelStage2.add(cmbSpecialDrug, gbc2);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelStage2, gbc);
        row++;

        // Action buttons
        panelActionButtons = new widget.PanelBiasa(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnAction = new widget.Button();
        btnAction.setText("Simpan & Grouping IDRG");
        btnAction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAction.addActionListener(e -> executeAction());

        btnEditKlaim = new widget.Button();
        btnEditKlaim.setText("Edit Klaim");
        btnEditKlaim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditKlaim.addActionListener(e -> executeEditKlaim());
        btnEditKlaim.setVisible(false);

        btnCetakKlaim = new widget.Button();
        btnCetakKlaim.setText("Tarik Ulang Cetak Klaim");
        btnCetakKlaim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCetakKlaim.addActionListener(e -> executeCetakKlaim());
        btnCetakKlaim.setVisible(false);

        btnKirimDC = new widget.Button();
        btnKirimDC.setText("Kirim ke DC");
        btnKirimDC.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKirimDC.addActionListener(e -> executeKirimDC());
        btnKirimDC.setVisible(false);

        panelActionButtons.add(btnAction);
        panelActionButtons.add(btnEditKlaim);
        panelActionButtons.add(btnCetakKlaim);
        panelActionButtons.add(btnKirimDC);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelActionButtons, gbc);
        row++;

        // Spacer at bottom
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3; gbc.weighty = 1.0;
        mainPanel.add(new widget.Label(), gbc);

        widget.ScrollPane scrollPane = new widget.ScrollPane();
        scrollPane.setViewportView(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private int buildNewParameterSections(widget.PanelBiasa mainPanel, GridBagConstraints gbc, int row) {
        Font sectionFont = new Font("Tahoma", Font.BOLD, 12);

        // --- Ventilator Section ---
        panelVentilator = new widget.PanelBiasa(new GridBagLayout());
        panelVentilator.setBorder(BorderFactory.createTitledBorder("Ventilator"));
        panelVentilator.setVisible(false);

        cmbVentilatorUseInd = buildCombo("0", "1");
        txtVentilatorStartDttm = new widget.TextBox();
        txtVentilatorStopDttm = new widget.TextBox();

        GridBagConstraints gv = new GridBagConstraints();
        gv.insets = new Insets(2, 5, 2, 5);
        gv.fill = GridBagConstraints.HORIZONTAL;
        gv.anchor = GridBagConstraints.WEST;
        int vRow = 0;

        gv.gridx = 0; gv.gridy = vRow; gv.weightx = 0.3;
        panelVentilator.add(newLabel("Penggunaan Ventilator"), gv);
        gv.gridx = 1; gv.weightx = 0;
        panelVentilator.add(newLabel(":"), gv);
        gv.gridx = 2; gv.weightx = 0.7;
        panelVentilator.add(cmbVentilatorUseInd, gv);
        vRow++;

        gv.gridx = 0; gv.gridy = vRow; gv.weightx = 0.3;
        panelVentilator.add(newLabel("Mulai (yyyy-MM-dd HH:mm:ss)"), gv);
        gv.gridx = 1; gv.weightx = 0;
        panelVentilator.add(newLabel(":"), gv);
        gv.gridx = 2; gv.weightx = 0.7;
        panelVentilator.add(txtVentilatorStartDttm, gv);
        vRow++;

        gv.gridx = 0; gv.gridy = vRow; gv.weightx = 0.3;
        panelVentilator.add(newLabel("Selesai (yyyy-MM-dd HH:mm:ss)"), gv);
        gv.gridx = 1; gv.weightx = 0;
        panelVentilator.add(newLabel(":"), gv);
        gv.gridx = 2; gv.weightx = 0.7;
        panelVentilator.add(txtVentilatorStopDttm, gv);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelVentilator, gbc);
        row++;

        // --- Additional Clinical Fields ---
        widget.Label headerAdditional = createLeftLabel();
        headerAdditional.setText("Parameter Tambahan");
        headerAdditional.setFont(sectionFont);
        headerAdditional.setForeground(new Color(0x0c, 0x68, 0x4c));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(headerAdditional, gbc);
        row++;

        cmbUpgradeClassPayor = buildCombo("", "peserta", "pemberi_kerja", "asuransi_tambahan");
        cmbBayiLahirStatusCd = buildCombo("", "1", "2");
        txtKantongDarah = newTextBox("0");
        txtKantongDarah.setDocument(new batasInput(10).getOnlyAngka(txtKantongDarah));
        cmbAlteplaseInd = buildCombo("0", "1");

        String[][] additionalFields = {
            {"Upgrade Class Payor", "cmbUpgradeClassPayor"},
            {"Bayi Lahir Status (1=Normal, 2=Kelainan)", "cmbBayiLahirStatusCd"},
            {"Kantong Darah", "txtKantongDarah"},
            {"Alteplase Indikator", "cmbAlteplaseInd"},
        };
        java.util.Map<String, java.awt.Component> addFieldMap = new java.util.LinkedHashMap<>();
        addFieldMap.put("cmbUpgradeClassPayor", cmbUpgradeClassPayor);
        addFieldMap.put("cmbBayiLahirStatusCd", cmbBayiLahirStatusCd);
        addFieldMap.put("txtKantongDarah", txtKantongDarah);
        addFieldMap.put("cmbAlteplaseInd", cmbAlteplaseInd);

        for (String[] field : additionalFields) {
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
            mainPanel.add(newLabel(field[0]), gbc);
            gbc.gridx = 1; gbc.weightx = 0;
            mainPanel.add(newLabel(":"), gbc);
            gbc.gridx = 2; gbc.weightx = 0.8;
            mainPanel.add(addFieldMap.get(field[1]), gbc);
            row++;
        }

        // --- APGAR Section ---
        panelApgar = new widget.PanelBiasa(new GridBagLayout());
        panelApgar.setBorder(BorderFactory.createTitledBorder("APGAR Score"));
        panelApgar.setVisible(false);

        cmbApgarM1Appearance = buildCombo("0", "1", "2");
        cmbApgarM1Pulse = buildCombo("0", "1", "2");
        cmbApgarM1Grimace = buildCombo("0", "1", "2");
        cmbApgarM1Activity = buildCombo("0", "1", "2");
        cmbApgarM1Respiration = buildCombo("0", "1", "2");
        cmbApgarM5Appearance = buildCombo("0", "1", "2");
        cmbApgarM5Pulse = buildCombo("0", "1", "2");
        cmbApgarM5Grimace = buildCombo("0", "1", "2");
        cmbApgarM5Activity = buildCombo("0", "1", "2");
        cmbApgarM5Respiration = buildCombo("0", "1", "2");

        GridBagConstraints ga = new GridBagConstraints();
        ga.insets = new Insets(2, 5, 2, 5);
        ga.fill = GridBagConstraints.HORIZONTAL;
        ga.anchor = GridBagConstraints.WEST;
        int aRow = 0;

        String[] apgarLabels = {"Appearance", "Pulse", "Grimace", "Activity", "Respiration"};
        widget.ComboBox[] apgarM1 = {cmbApgarM1Appearance, cmbApgarM1Pulse, cmbApgarM1Grimace, cmbApgarM1Activity, cmbApgarM1Respiration};
        widget.ComboBox[] apgarM5 = {cmbApgarM5Appearance, cmbApgarM5Pulse, cmbApgarM5Grimace, cmbApgarM5Activity, cmbApgarM5Respiration};

        ga.gridx = 0; ga.gridy = aRow; ga.weightx = 0.3;
        panelApgar.add(newLabel(""), ga);
        ga.gridx = 1; ga.weightx = 0.35;
        panelApgar.add(createBoldLabel("Menit 1"), ga);
        ga.gridx = 2; ga.weightx = 0.35;
        panelApgar.add(createBoldLabel("Menit 5"), ga);
        aRow++;

        for (int i = 0; i < apgarLabels.length; i++) {
            ga.gridx = 0; ga.gridy = aRow; ga.weightx = 0.3;
            panelApgar.add(newLabel(apgarLabels[i]), ga);
            ga.gridx = 1; ga.weightx = 0.35;
            panelApgar.add(apgarM1[i], ga);
            ga.gridx = 2; ga.weightx = 0.35;
            panelApgar.add(apgarM5[i], ga);
            aRow++;
        }

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelApgar, gbc);
        row++;

        // --- Persalinan Section ---
        panelPersalinan = new widget.PanelBiasa(new GridBagLayout());
        panelPersalinan.setBorder(BorderFactory.createTitledBorder("Persalinan"));
        panelPersalinan.setVisible(false);

        txtUsiaKehamilan = newTextBox("");
        txtUsiaKehamilan.setDocument(new batasInput(3).getOnlyAngka(txtUsiaKehamilan));
        txtGravida = newTextBox("");
        txtGravida.setDocument(new batasInput(3).getOnlyAngka(txtGravida));
        txtPartus = newTextBox("");
        txtPartus.setDocument(new batasInput(3).getOnlyAngka(txtPartus));
        txtAbortus = newTextBox("");
        txtAbortus.setDocument(new batasInput(3).getOnlyAngka(txtAbortus));
        cmbOnsetKontraksi = buildCombo("", "spontan", "induksi", "non_spontan_non_induksi");

        GridBagConstraints gp = new GridBagConstraints();
        gp.insets = new Insets(2, 5, 2, 5);
        gp.fill = GridBagConstraints.HORIZONTAL;
        gp.anchor = GridBagConstraints.WEST;
        int pRow = 0;

        String[][] persalinanFields = {
            {"Usia Kehamilan (minggu)", "txtUsiaKehamilan"},
            {"Gravida", "txtGravida"},
            {"Partus", "txtPartus"},
            {"Abortus", "txtAbortus"},
            {"Onset Kontraksi", "cmbOnsetKontraksi"},
        };
        java.util.Map<String, java.awt.Component> persFieldMap = new java.util.LinkedHashMap<>();
        persFieldMap.put("txtUsiaKehamilan", txtUsiaKehamilan);
        persFieldMap.put("txtGravida", txtGravida);
        persFieldMap.put("txtPartus", txtPartus);
        persFieldMap.put("txtAbortus", txtAbortus);
        persFieldMap.put("cmbOnsetKontraksi", cmbOnsetKontraksi);

        for (String[] field : persalinanFields) {
            gp.gridwidth = 1;
            gp.gridx = 0; gp.gridy = pRow; gp.weightx = 0.3;
            panelPersalinan.add(newLabel(field[0]), gp);
            gp.gridx = 1; gp.weightx = 0;
            panelPersalinan.add(newLabel(":"), gp);
            gp.gridx = 2; gp.weightx = 0.7;
            panelPersalinan.add(persFieldMap.get(field[1]), gp);
            pRow++;
        }

        // Delivery sub-panel
        panelDeliveryList = new widget.PanelBiasa();
        panelDeliveryList.setLayout(new BoxLayout(panelDeliveryList, BoxLayout.Y_AXIS));

        widget.Button btnAddDelivery = new widget.Button();
        btnAddDelivery.setText("Tambah Delivery");
        btnAddDelivery.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddDelivery.addActionListener(e -> addDeliveryRow());

        gp.gridx = 0; gp.gridy = pRow; gp.gridwidth = 3;
        panelPersalinan.add(btnAddDelivery, gp);
        pRow++;

        gp.gridx = 0; gp.gridy = pRow; gp.gridwidth = 3; gp.weightx = 1.0;
        panelPersalinan.add(panelDeliveryList, gp);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        mainPanel.add(panelPersalinan, gbc);
        row++;

        return row;
    }

    private void addDeliveryRow() {
        DeliveryRow dr = new DeliveryRow(deliveryRows.size() + 1);
        deliveryRows.add(dr);
        panelDeliveryList.add(dr.panel);
        panelDeliveryList.revalidate();
        panelDeliveryList.repaint();
    }

    private void removeDeliveryRow(DeliveryRow dr) {
        deliveryRows.remove(dr);
        panelDeliveryList.remove(dr.panel);
        for (int i = 0; i < deliveryRows.size(); i++) {
            deliveryRows.get(i).setSequence(i + 1);
        }
        panelDeliveryList.revalidate();
        panelDeliveryList.repaint();
    }

    private static class DeliveryRow {
        widget.PanelBiasa panel;
        widget.Label lblSequence;
        widget.ComboBox cmbMethod, cmbLetakJanin, cmbKondisi, cmbUseManual, cmbUseForcep, cmbUseVacuum;
        widget.ComboBox cmbShkSpesimenAmbil, cmbShkLokasi, cmbShkAlasan;
        widget.TextBox txtDttm, txtShkSpesimenDttm;
        int sequence;

        DeliveryRow(int seq) {
            this.sequence = seq;
            panel = new widget.PanelBiasa(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Delivery #" + seq));

            lblSequence = new widget.Label();
            lblSequence.setText(String.valueOf(seq));

            cmbMethod = newCombo("vaginal", "sc");
            txtDttm = new widget.TextBox();
            cmbLetakJanin = newCombo("kepala", "sungsang", "lintang");
            cmbKondisi = newCombo("livebirth", "stillbirth");
            cmbUseManual = newCombo("0", "1");
            cmbUseForcep = newCombo("0", "1");
            cmbUseVacuum = newCombo("0", "1");
            cmbShkSpesimenAmbil = newCombo("ya", "tidak");
            cmbShkLokasi = newCombo("tumit", "vena");
            cmbShkAlasan = newCombo("tidak-dapat", "akses-sulit");
            txtShkSpesimenDttm = new widget.TextBox();

            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(1, 3, 1, 3);
            g.fill = GridBagConstraints.HORIZONTAL;
            g.anchor = GridBagConstraints.WEST;
            int r = 0;

            r = addField(g, r, "Metode", cmbMethod);
            r = addField(g, r, "Waktu (yyyy-MM-dd HH:mm:ss)", txtDttm);
            r = addField(g, r, "Letak Janin", cmbLetakJanin);
            r = addField(g, r, "Kondisi", cmbKondisi);
            r = addField(g, r, "Use Manual", cmbUseManual);
            r = addField(g, r, "Use Forcep", cmbUseForcep);
            r = addField(g, r, "Use Vacuum", cmbUseVacuum);
            r = addField(g, r, "SHK Spesimen Diambil?", cmbShkSpesimenAmbil);
            r = addField(g, r, "SHK Lokasi", cmbShkLokasi);
            r = addField(g, r, "SHK Alasan", cmbShkAlasan);
            r = addField(g, r, "SHK Spesimen Waktu", txtShkSpesimenDttm);

            cmbShkSpesimenAmbil.addActionListener(e -> {
                String val = (String) cmbShkSpesimenAmbil.getSelectedItem();
                cmbShkLokasi.setEnabled("ya".equals(val));
                txtShkSpesimenDttm.setEditable("ya".equals(val));
                cmbShkAlasan.setEnabled("tidak".equals(val));
            });
        }

        private int addField(GridBagConstraints g, int r, String label, java.awt.Component comp) {
            widget.Label lbl = new widget.Label();
            lbl.setText(label);
            g.gridx = 0; g.gridy = r; g.weightx = 0.3;
            panel.add(lbl, g);
            g.gridx = 1; g.weightx = 0;
            widget.Label colon = new widget.Label();
            colon.setText(":");
            panel.add(colon, g);
            g.gridx = 2; g.weightx = 0.7;
            panel.add(comp, g);
            return r + 1;
        }

        void setSequence(int seq) {
            this.sequence = seq;
            lblSequence.setText(String.valueOf(seq));
            panel.setBorder(BorderFactory.createTitledBorder("Delivery #" + seq));
        }

        void setEditable(boolean editable) {
            cmbMethod.setEnabled(editable);
            txtDttm.setEditable(editable);
            cmbLetakJanin.setEnabled(editable);
            cmbKondisi.setEnabled(editable);
            cmbUseManual.setEnabled(editable);
            cmbUseForcep.setEnabled(editable);
            cmbUseVacuum.setEnabled(editable);
            cmbShkSpesimenAmbil.setEnabled(editable);
            cmbShkLokasi.setEnabled(editable);
            cmbShkAlasan.setEnabled(editable);
            txtShkSpesimenDttm.setEditable(editable);
        }

        private static widget.ComboBox newCombo(String... items) {
            widget.ComboBox cmb = new widget.ComboBox();
            for (String item : items) {
                cmb.addItem(item);
            }
            return cmb;
        }
    }

    private void showFormKlaim() {
        EKlaimAutoFiller.ClaimData claim = EKlaimAutoFiller.fill(noSep);
        EKlaimBillingAggregator.BillingData billing = EKlaimBillingAggregator.aggregate(claim.noRawat.isEmpty() ? noRawat : claim.noRawat);
        showFormKlaimWithData(claim, billing);
    }

    private void showFormKlaimWithData(EKlaimAutoFiller.ClaimData claim, EKlaimBillingAggregator.BillingData billing) {
        lblStatusText.setText("Isi data klaim, lalu klik tombol simpan");
        lblStatusText.setForeground(new Color(0x0c, 0x68, 0x4c));

        if (claim != null && billing != null) {
            populatePatientInfo(claim);
            populateClinicalFields(claim);
            populateCovidFields(claim);
            populateBilling(billing);
        }
        refreshIdrgDisplay();
        refreshInacbgDisplay();

        panelCovid.setVisible(corona);
        panelVentilator.setVisible(true);
        panelApgar.setVisible(true);
        panelPersalinan.setVisible(true);
        setFormEditable(true);
        panelStage2.setVisible(false);
        btnAction.setText("Simpan & Grouping IDRG");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showIdrgGrouping() {
        lblStatusText.setText("IDRG Grouping selesai");
        lblStatusText.setForeground(new Color(0x0c, 0x68, 0x4c));

        refreshIdrgDisplay();
        refreshInacbgDisplay();

        setFormEditable(false);
        panelStage2.setVisible(false);
        btnAction.setText("Final IDRG & Import ke INACBG");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showIdrgFinal() {
        lblStatusText.setText("IDRG Final - lanjut ke Grouping INACBG");
        lblStatusText.setForeground(new Color(0x0c, 0x68, 0x4c));

        refreshIdrgDisplay();
        refreshInacbgDisplay();

        setFormEditable(false);
        panelStage2.setVisible(false);
        btnAction.setText("Grouping INACBG");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showInacbgGrouping() {
        lblStatusText.setText("INACBG Grouping selesai");
        lblStatusText.setForeground(new Color(0x0c, 0x68, 0x4c));

        refreshIdrgDisplay();
        refreshInacbgDisplay();

        setFormEditable(false);
        panelStage2.setVisible(false);
        btnAction.setText("Final INACBG");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showInacbgStage2() {
        lblStatusText.setText("Pilih Special CMG untuk Stage 2");
        lblStatusText.setForeground(new Color(0xcc, 0x66, 0x00));

        refreshIdrgDisplay();
        refreshInacbgDisplay();
        loadSpecialCmgOptions();

        setFormEditable(false);
        panelStage2.setVisible(true);
        btnAction.setText("Grouping INACBG Stage 2");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showInacbgFinal() {
        lblStatusText.setText("INACBG Final - lanjut ke Finalisasi Klaim");
        lblStatusText.setForeground(new Color(0x0c, 0x68, 0x4c));

        refreshIdrgDisplay();
        refreshInacbgDisplay();

        setFormEditable(false);
        panelStage2.setVisible(false);
        btnAction.setText("Finalisasi Klaim");
        btnAction.setVisible(true);
        btnEditKlaim.setVisible(false);
        btnCetakKlaim.setVisible(false);
        btnKirimDC.setVisible(false);
    }

    private void showSelesai() {
        lblStatusText.setText("Klaim berhasil disimpan!");
        lblStatusText.setForeground(new Color(0x22, 0xc5, 0x5e));

        refreshIdrgDisplay();
        refreshInacbgDisplay();

        setFormEditable(false);
        panelStage2.setVisible(false);
        btnAction.setVisible(false);
        btnEditKlaim.setVisible(true);
        btnCetakKlaim.setVisible(true);
        btnKirimDC.setVisible(true);

        String kirimInfo = Sequel.cariIsiSmc(
                "select if(inacbg_cetak_klaim.kirim_ke_dc is null or inacbg_cetak_klaim.kirim_ke_dc = '0000-00-00 00:00:00.000', '', " +
                "concat('Dikirim pada ', date_format(inacbg_cetak_klaim.kirim_ke_dc, '%d-%m-%Y %H:%i:%s'))) from inacbg_cetak_klaim where inacbg_cetak_klaim.no_sep = ?", noSep);
        if (!kirimInfo.isEmpty()) {
            btnKirimDC.setText("Kirim ke DC (" + kirimInfo + ")");
        } else {
            btnKirimDC.setText("Kirim ke DC");
        }
    }

    private void executeAction() {
        if (service == null) {
            JOptionPane.showMessageDialog(this, "E-Klaim belum dikonfigurasi! Periksa setting EKLAIMWSURL dan EKLAIMKEY.");
            return;
        }

        // Capture all form values on EDT before background work
        final java.util.Map<String, String> formValues = captureFormValues();

        btnAction.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final int currentFlag = flagKlaim;
        final int currentTopup = flagInacbgTopup;

        new SwingWorker<EKlaimService.EKlaimResult, Void>() {
            @Override
            protected EKlaimService.EKlaimResult doInBackground() {
                System.out.println("[EKlaim] executeAction: flagKlaim=" + currentFlag + ", flagInacbgTopup=" + currentTopup);
                switch (currentFlag) {
                    case 5: return executeFinalIdrgAndImport();
                    case 4: return executeGroupingInacbg();
                    case 3: {
                        if (currentTopup == 1) {
                            return executeGroupingStage2(formValues);
                        } else {
                            return executeFinalInacbg();
                        }
                    }
                    case 2: return executeFinalisasiKlaim();
                    default: return executeSimpanDanGroupingIdrg(formValues);
                }
            }

            @Override
            protected void done() {
                try {
                    EKlaimService.EKlaimResult result = get();
                    if (result.success) {
                        System.out.println("[EKlaim] Action succeeded: " + result.data);
                        String msg = result.data != null ? result.data : "Berhasil!";
                        lblStatusText.setText(msg);
                        lblStatusText.setForeground(new Color(0x22, 0xc5, 0x5e));
                        JOptionPane.showMessageDialog(EKlaimPanel.this, msg, "E-Klaim", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        System.out.println("[EKlaim] Action failed: " + result.error);
                        String errMsg = result.error != null ? result.error : "Unknown error";
                        lblStatusText.setText("Error: " + errMsg);
                        lblStatusText.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(EKlaimPanel.this, errMsg, "E-Klaim", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("[EKlaim] Action exception: " + e.getMessage());
                    e.printStackTrace();
                    lblStatusText.setText("Error: " + e.getMessage());
                    lblStatusText.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(EKlaimPanel.this, e.getMessage(), "E-Klaim", JOptionPane.ERROR_MESSAGE);
                }

                btnAction.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());

                if (onRefreshCallback != null) {
                    onRefreshCallback.run();
                }
            }
        }.execute();
    }

    private java.util.Map<String, String> captureFormValues() {
        java.util.Map<String, String> v = new java.util.LinkedHashMap<>();
        v.put("tglKeluar", getFieldValue(txtTglKeluar));
        v.put("kelasRawat", getComboValue(cmbKelasRawat));
        v.put("caraMasuk", getComboValue(cmbCaraMasuk));
        v.put("adlSubAcute", getComboValue(cmbAdlSubAcute));
        v.put("adlChronic", getComboValue(cmbAdlChronic));
        v.put("icuIndikator", getComboValue(cmbIcuIndikator));
        v.put("icuLos", getFieldValue(txtIcuLos));
        v.put("ventilatorHour", getFieldValue(txtVentilatorHour));
        v.put("upgradeClassInd", getComboValue(cmbUpgradeClassInd));
        v.put("upgradeClassClass", getComboValue(cmbUpgradeClassClass));
        v.put("upgradeClassLos", getFieldValue(txtUpgradeClassLos));
        v.put("addPaymentPct", getFieldValue(txtAddPaymentPct));
        v.put("birthWeight", getFieldValue(txtBirthWeight));
        v.put("sistole", getFieldValue(txtSistole));
        v.put("diastole", getFieldValue(txtDiastole));
        v.put("dischargeStatus", getComboValue(cmbDischargeStatus));
        v.put("noSitb", txtNoSitb.getText().trim());
        v.put("dializerSingleUse", getComboValue(cmbDializerSingleUse));
        // Billing (19 fields)
        v.put("prosedurNonBedah", getFieldValue(txtProsedurNonBedah));
        v.put("prosedurBedah", getFieldValue(txtProsedurBedah));
        v.put("konsultasi", getFieldValue(txtKonsultasi));
        v.put("tenagaAhli", getFieldValue(txtTenagaAhli));
        v.put("keperawatan", getFieldValue(txtKeperawatan));
        v.put("penunjang", getFieldValue(txtPenunjang));
        v.put("radiologi", getFieldValue(txtRadiologi));
        v.put("laboratorium", getFieldValue(txtLaboratorium));
        v.put("pelayananDarah", getFieldValue(txtPelayananDarah));
        v.put("kamar", getFieldValue(txtKamar));
        v.put("rawatIntensif", getFieldValue(txtRawatIntensif));
        v.put("obatKronis", getFieldValue(txtObatKronis));
        v.put("obatKemoterapi", getFieldValue(txtObatKemoterapi));
        v.put("obat", getFieldValue(txtObat));
        v.put("alkes", getFieldValue(txtAlkes));
        v.put("bmhp", getFieldValue(txtBmhp));
        v.put("sewaAlat", getFieldValue(txtSewaAlat));
        v.put("rehabilitasi", getFieldValue(txtRehabilitasi));
        v.put("tarifPoliEks", getFieldValue(txtTarifPoliEks));
        // Ventilator
        v.put("ventilatorUseInd", getComboValue(cmbVentilatorUseInd));
        v.put("ventilatorStartDttm", txtVentilatorStartDttm.getText().trim());
        v.put("ventilatorStopDttm", txtVentilatorStopDttm.getText().trim());
        // Additional params
        v.put("upgradeClassPayor", getComboValue(cmbUpgradeClassPayor));
        v.put("bayiLahirStatusCd", getComboValue(cmbBayiLahirStatusCd));
        v.put("kantongDarah", getFieldValue(txtKantongDarah));
        v.put("alteplaseInd", getComboValue(cmbAlteplaseInd));
        // APGAR
        v.put("apgarMenit1Appearance", getComboValue(cmbApgarM1Appearance));
        v.put("apgarMenit1Pulse", getComboValue(cmbApgarM1Pulse));
        v.put("apgarMenit1Grimace", getComboValue(cmbApgarM1Grimace));
        v.put("apgarMenit1Activity", getComboValue(cmbApgarM1Activity));
        v.put("apgarMenit1Respiration", getComboValue(cmbApgarM1Respiration));
        v.put("apgarMenit5Appearance", getComboValue(cmbApgarM5Appearance));
        v.put("apgarMenit5Pulse", getComboValue(cmbApgarM5Pulse));
        v.put("apgarMenit5Grimace", getComboValue(cmbApgarM5Grimace));
        v.put("apgarMenit5Activity", getComboValue(cmbApgarM5Activity));
        v.put("apgarMenit5Respiration", getComboValue(cmbApgarM5Respiration));
        // Persalinan
        v.put("usiaKehamilan", txtUsiaKehamilan.getText().trim());
        v.put("gravida", txtGravida.getText().trim());
        v.put("partus", txtPartus.getText().trim());
        v.put("abortus", txtAbortus.getText().trim());
        v.put("onsetKontraksi", getComboValue(cmbOnsetKontraksi));
        // Delivery list as JSON
        if (!deliveryRows.isEmpty()) {
            try {
                ObjectMapper om = new ObjectMapper();
                ArrayNode arr = om.createArrayNode();
                for (DeliveryRow dr : deliveryRows) {
                    ObjectNode obj = arr.addObject();
                    obj.put("deliverySequence", String.valueOf(dr.sequence));
                    obj.put("deliveryMethod", (String) dr.cmbMethod.getSelectedItem());
                    obj.put("deliveryDttm", dr.txtDttm.getText().trim());
                    obj.put("letakJanin", (String) dr.cmbLetakJanin.getSelectedItem());
                    obj.put("kondisi", (String) dr.cmbKondisi.getSelectedItem());
                    obj.put("useManual", (String) dr.cmbUseManual.getSelectedItem());
                    obj.put("useForcep", (String) dr.cmbUseForcep.getSelectedItem());
                    obj.put("useVacuum", (String) dr.cmbUseVacuum.getSelectedItem());
                    obj.put("shkSpesimenAmbil", (String) dr.cmbShkSpesimenAmbil.getSelectedItem());
                    obj.put("shkLokasi", (String) dr.cmbShkLokasi.getSelectedItem());
                    obj.put("shkAlasan", (String) dr.cmbShkAlasan.getSelectedItem());
                    obj.put("shkSpesimenDttm", dr.txtShkSpesimenDttm.getText().trim());
                }
                v.put("deliveryList", om.writeValueAsString(arr));
            } catch (Exception ex) {
                System.out.println("[EKlaim] Error serializing delivery: " + ex.getMessage());
            }
        }
        // COVID
        if (corona) {
            v.put("pemulasaraanJenazah", getComboValue(cmbPemulasaraanJenazah));
            v.put("kantongJenazah", getComboValue(cmbKantongJenazah));
            v.put("petiJenazah", getComboValue(cmbPetiJenazah));
            v.put("plastikErat", getComboValue(cmbPlastikErat));
            v.put("desinfektanJenazah", getComboValue(cmbDesinfektanJenazah));
            v.put("mobilJenazah", getComboValue(cmbMobilJenazah));
            v.put("desinfektanMobilJenazah", getComboValue(cmbDesinfektanMobilJenazah));
            v.put("covid19StatusCd", getComboValue(cmbCovid19StatusCd));
            v.put("covid19CcInd", getComboValue(cmbCovid19CcInd));
            v.put("nomorKartuT", txtNomorKartuT.getText().trim());
            v.put("episodes1", getFieldValue(txtEpisodes1));
            v.put("episodes2", getFieldValue(txtEpisodes2));
            v.put("episodes3", getFieldValue(txtEpisodes3));
            v.put("episodes4", getFieldValue(txtEpisodes4));
            v.put("episodes5", getFieldValue(txtEpisodes5));
            v.put("episodes6", getFieldValue(txtEpisodes6));
        }
        // Special CMG (for stage 2)
        v.put("specialProcedure", getComboValueSafe(cmbSpecialProcedure));
        v.put("specialProsthesis", getComboValueSafe(cmbSpecialProsthesis));
        v.put("specialInvestigation", getComboValueSafe(cmbSpecialInvestigation));
        v.put("specialDrug", getComboValueSafe(cmbSpecialDrug));
        return v;
    }

    private String getComboValueSafe(widget.ComboBox cmb) {
        if (cmb == null || cmb.getSelectedItem() == null) return "";
        return (String) cmb.getSelectedItem();
    }

    private EKlaimService.EKlaimResult executeSimpanDanGroupingIdrg(java.util.Map<String, String> form) {
        EKlaimAutoFiller.ClaimData claim = EKlaimAutoFiller.fill(noSep);
        System.out.println("[EKlaim] executeSimpanDanGroupingIdrg: noSep=" + noSep + ", noRawat=" + claim.noRawat);

        if (!validateBilling()) {
            System.out.println("[EKlaim] Billing validation failed");
            return new EKlaimService.EKlaimResult(false, null, "Total billing tidak sesuai dengan billing pasien!");
        }

        // 1. Buat klaim baru
        System.out.println("[EKlaim] Step 1: buatKlaimBaru");
        EKlaimService.EKlaimResult result = service.buatKlaimBaru(
                claim.noKartu, noSep, claim.noRkmMedis, claim.nmPasien, claim.tglLahir, claim.gender, claim.noRawat);
        System.out.println("[EKlaim] buatKlaimBaru result: success=" + result.success + ", data=" + result.data + ", error=" + result.error);
        if (!result.success) return result;

        // 2. Update data klaim — enrich form map with claim-derived fields
        String kodeTarif = koneksiDB.EKLAIMKODETARIF();
        String payorId = corona ? "71" : "3";
        String payorCd = corona ? "JAMINAN COVID-19" : "JKN";

        form.put("nomorKartu", claim.noKartu);
        form.put("tglMasuk", claim.tglRegistrasi);
        form.put("tglPulang", form.get("tglKeluar"));
        form.put("jenisRawat", claim.jenisRawat);
        form.put("namaDokter", claim.nmDokter);
        form.put("kodeTarif", kodeTarif);
        form.put("payorId", payorId);
        form.put("payorCd", payorCd);
        form.put("cobCd", "#");
        form.put("coderNik", coderNik);

        EKlaimService.CovidData covidData = null;
        if (corona) {
            covidData = new EKlaimService.CovidData();
            covidData.pemulasaraanJenazah = (int) parseLong(form.getOrDefault("pemulasaraanJenazah", "0"));
            covidData.kantongJenazah = (int) parseLong(form.getOrDefault("kantongJenazah", "0"));
            covidData.petiJenazah = (int) parseLong(form.getOrDefault("petiJenazah", "0"));
            covidData.plastikErat = (int) parseLong(form.getOrDefault("plastikErat", "0"));
            covidData.desinfektanJenazah = (int) parseLong(form.getOrDefault("desinfektanJenazah", "0"));
            covidData.mobilJenazah = (int) parseLong(form.getOrDefault("mobilJenazah", "0"));
            covidData.desinfektanMobilJenazah = (int) parseLong(form.getOrDefault("desinfektanMobilJenazah", "0"));
            covidData.covid19StatusCd = (int) parseLong(form.getOrDefault("covid19StatusCd", "1"));
            covidData.covid19CcInd = (int) parseLong(form.getOrDefault("covid19CcInd", "0"));
            covidData.nomorKartuT = form.getOrDefault("nomorKartuT", "");
            covidData.episodes1 = (int) parseLong(form.getOrDefault("episodes1", "0"));
            covidData.episodes2 = (int) parseLong(form.getOrDefault("episodes2", "0"));
            covidData.episodes3 = (int) parseLong(form.getOrDefault("episodes3", "0"));
            covidData.episodes4 = (int) parseLong(form.getOrDefault("episodes4", "0"));
            covidData.episodes5 = (int) parseLong(form.getOrDefault("episodes5", "0"));
            covidData.episodes6 = (int) parseLong(form.getOrDefault("episodes6", "0"));
        }

        System.out.println("[EKlaim] Step 2: updateDataKlaim");
        result = service.updateDataKlaim(noSep, form, covidData);
        System.out.println("[EKlaim] updateDataKlaim result: success=" + result.success + ", data=" + result.data + ", error=" + result.error);
        if (!result.success) return result;

        // 3. Set diagnosa IDRG
        String diagnosaIdrg = service.getDiagnosaIdrg(noSep);
        System.out.println("[EKlaim] Step 3: setDiagnosaIdrg, diagnosa=" + diagnosaIdrg);
        if (!diagnosaIdrg.isEmpty()) {
            result = service.setDiagnosaIdrg(noSep, diagnosaIdrg);
            System.out.println("[EKlaim] setDiagnosaIdrg result: success=" + result.success + ", error=" + result.error);
            if (!result.success) return result;
        }

        // 4. Set prosedur IDRG
        String prosedurIdrg = service.getProsedurIdrg(noSep);
        System.out.println("[EKlaim] Step 4: setProsedurIdrg, prosedur=" + prosedurIdrg);
        result = service.setProsedurIdrg(noSep, prosedurIdrg);
        System.out.println("[EKlaim] setProsedurIdrg result: success=" + result.success + ", error=" + result.error);
        if (!result.success) return result;

        // 5. Grouping IDRG Stage 1
        System.out.println("[EKlaim] Step 5: groupingStage1Idrg");
        result = service.groupingStage1Idrg(noSep, claim.noRawat, claim.statusLanjut, coderNik);
        System.out.println("[EKlaim] groupingStage1Idrg result: success=" + result.success + ", data=" + result.data + ", error=" + result.error);
        return result;
    }

    private EKlaimService.EKlaimResult executeFinalIdrgAndImport() {
        System.out.println("[EKlaim] executeFinalIdrgAndImport: noSep=" + noSep);
        EKlaimService.EKlaimResult result = service.finalIdrg(noSep, coderNik);
        System.out.println("[EKlaim] finalIdrg result: success=" + result.success + ", error=" + result.error);
        if (!result.success) return result;

        result = service.importIdrgToInacbg(noSep);
        System.out.println("[EKlaim] importIdrgToInacbg result: success=" + result.success + ", error=" + result.error);
        return result;
    }

    private EKlaimService.EKlaimResult executeGroupingInacbg() {
        System.out.println("[EKlaim] executeGroupingInacbg: noSep=" + noSep);
        String diagnosaInacbg = service.getDiagnosaInacbg(noSep);
        System.out.println("[EKlaim] diagnosaInacbg=" + diagnosaInacbg);
        if (!diagnosaInacbg.isEmpty()) {
            EKlaimService.EKlaimResult result = service.setDiagnosaInacbg(noSep, diagnosaInacbg);
            System.out.println("[EKlaim] setDiagnosaInacbg result: success=" + result.success + ", error=" + result.error);
            if (!result.success) return result;
        }

        String prosedurInacbg = service.getProsedurInacbg(noSep);
        System.out.println("[EKlaim] prosedurInacbg=" + prosedurInacbg);
        EKlaimService.EKlaimResult result = service.setProsedurInacbg(noSep, prosedurInacbg);
        System.out.println("[EKlaim] setProsedurInacbg result: success=" + result.success + ", error=" + result.error);
        if (!result.success) return result;

        result = service.groupingStage1Inacbg(noSep, coderNik);
        System.out.println("[EKlaim] groupingStage1Inacbg result: success=" + result.success + ", data=" + result.data + ", error=" + result.error);
        return result;
    }

    private EKlaimService.EKlaimResult executeGroupingStage2(java.util.Map<String, String> form) {
        StringBuilder specialCmg = new StringBuilder();
        appendSpecialCmgFromForm(specialCmg, form.getOrDefault("specialProcedure", ""));
        appendSpecialCmgFromForm(specialCmg, form.getOrDefault("specialProsthesis", ""));
        appendSpecialCmgFromForm(specialCmg, form.getOrDefault("specialInvestigation", ""));
        appendSpecialCmgFromForm(specialCmg, form.getOrDefault("specialDrug", ""));

        String cmgValue = specialCmg.toString();
        if (cmgValue.endsWith("#")) {
            cmgValue = cmgValue.substring(0, cmgValue.length() - 1);
        }

        System.out.println("[EKlaim] executeGroupingStage2: specialCmg=" + cmgValue);
        return service.groupingStage2Inacbg(noSep, coderNik, cmgValue);
    }

    private EKlaimService.EKlaimResult executeFinalInacbg() {
        System.out.println("[EKlaim] executeFinalInacbg: noSep=" + noSep);
        EKlaimService.EKlaimResult result = service.finalInacbg(noSep, coderNik);
        System.out.println("[EKlaim] finalInacbg result: success=" + result.success + ", error=" + result.error);
        return result;
    }

    private EKlaimService.EKlaimResult executeFinalisasiKlaim() {
        System.out.println("[EKlaim] executeFinalisasiKlaim: noSep=" + noSep);
        EKlaimService.EKlaimResult result = service.finalisasiKlaim(noSep, coderNik);
        System.out.println("[EKlaim] finalisasiKlaim result: success=" + result.success + ", error=" + result.error);
        return result;
    }

    private void executeEditKlaim() {
        if (service == null) return;

        btnEditKlaim.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<EKlaimService.EKlaimResult, Void>() {
            @Override
            protected EKlaimService.EKlaimResult doInBackground() {
                EKlaimService.EKlaimResult result = service.reeditKlaim(noSep, noRawat);
                if (result.success) {
                    service.reeditIdrg(noSep);
                }
                return result;
            }

            @Override
            protected void done() {
                btnEditKlaim.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                if (onRefreshCallback != null) {
                    onRefreshCallback.run();
                }
            }
        }.execute();
    }

    private void executeCetakKlaim() {
        if (service == null) return;

        btnCetakKlaim.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<EKlaimService.EKlaimResult, Void>() {
            @Override
            protected EKlaimService.EKlaimResult doInBackground() {
                return service.cetakKlaim(noSep);
            }

            @Override
            protected void done() {
                try {
                    EKlaimService.EKlaimResult result = get();
                    if (result.success) {
                        lblStatusText.setText("Cetak klaim berhasil diperbarui!");
                        lblStatusText.setForeground(new Color(0x22, 0xc5, 0x5e));
                        JOptionPane.showMessageDialog(EKlaimPanel.this, "Cetak klaim berhasil diperbarui!", "E-Klaim", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        lblStatusText.setText("Error: " + result.error);
                        lblStatusText.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(EKlaimPanel.this, result.error, "E-Klaim", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatusText.setText("Error: " + e.getMessage());
                    lblStatusText.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(EKlaimPanel.this, e.getMessage(), "E-Klaim", JOptionPane.ERROR_MESSAGE);
                }
                btnCetakKlaim.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
            }
        }.execute();
    }

    private void executeKirimDC() {
        if (service == null) return;

        btnKirimDC.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<EKlaimService.EKlaimResult, Void>() {
            @Override
            protected EKlaimService.EKlaimResult doInBackground() {
                return service.kirimKlaimIndividual(noSep);
            }

            @Override
            protected void done() {
                try {
                    EKlaimService.EKlaimResult result = get();
                    if (result.success) {
                        lblStatusText.setText("Klaim berhasil dikirim online!");
                        lblStatusText.setForeground(new Color(0x22, 0xc5, 0x5e));
                        JOptionPane.showMessageDialog(EKlaimPanel.this, "Klaim berhasil dikirim online!", "E-Klaim", JOptionPane.INFORMATION_MESSAGE);
                        if (onRefreshCallback != null) {
                            onRefreshCallback.run();
                        }
                    } else {
                        lblStatusText.setText("Error: " + result.error);
                        lblStatusText.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(EKlaimPanel.this, result.error, "E-Klaim", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatusText.setText("Error: " + e.getMessage());
                    lblStatusText.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(EKlaimPanel.this, e.getMessage(), "E-Klaim", JOptionPane.ERROR_MESSAGE);
                }
                btnKirimDC.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
            }
        }.execute();
    }

    private void populatePatientInfo(EKlaimAutoFiller.ClaimData claim) {
        valNoRawat.setText(claim.noRawat);
        valNoSep.setText(claim.noSep);
        valNoRm.setText(claim.noRkmMedis);
        valNoKartu.setText(claim.noKartu);
        valNamaPasien.setText(claim.nmPasien + ", " + claim.umurDaftar + " " + claim.sttsUmur);
        valJk.setText(claim.jenisKelamin);
        valAlamat.setText(claim.alamat);
        valTglRegistrasi.setText(claim.tglRegistrasi + " " + claim.jamReg);
        valPoli.setText(claim.nmPoli);
        valDokter.setText(claim.nmDokter);
        valStatus.setText(claim.statusLanjut + " (" + claim.pngJawab + ")");
    }

    private void populateClinicalFields(EKlaimAutoFiller.ClaimData claim) {
        txtTglKeluar.setText(claim.tglKeluar);
        selectComboValue(cmbKelasRawat, claim.kelasRawat);
        selectComboValue(cmbCaraMasuk, claim.caraMasuk);
        selectComboValue(cmbIcuIndikator, String.valueOf(claim.icuIndikator));
        txtIcuLos.setText(String.valueOf(claim.icuLos));
        selectComboValue(cmbUpgradeClassInd, claim.upgradeClassInd);
        selectComboValue(cmbUpgradeClassClass, claim.upgradeClassClass);
        selectComboValue(cmbDischargeStatus, claim.dischargeStatus);
        txtSistole.setText(claim.sistole);
        txtDiastole.setText(claim.diastole);
        txtNoSitb.setText(claim.noSitb);
        selectComboValue(cmbDializerSingleUse, claim.dializerSingleUse);
    }

    private void populateCovidFields(EKlaimAutoFiller.ClaimData claim) {
        selectComboValue(cmbPemulasaraanJenazah, String.valueOf(claim.pemulasaraanJenazah));
        selectComboValue(cmbKantongJenazah, String.valueOf(claim.kantongJenazah));
        selectComboValue(cmbPetiJenazah, String.valueOf(claim.petiJenazah));
        selectComboValue(cmbPlastikErat, String.valueOf(claim.plastikErat));
        selectComboValue(cmbDesinfektanJenazah, String.valueOf(claim.desinfektanJenazah));
        selectComboValue(cmbMobilJenazah, String.valueOf(claim.mobilJenazah));
        selectComboValue(cmbDesinfektanMobilJenazah, String.valueOf(claim.desinfektanMobilJenazah));
        selectComboValue(cmbCovid19StatusCd, String.valueOf(claim.covid19StatusCd));
        selectComboValue(cmbCovid19CcInd, String.valueOf(claim.covid19CcInd));
        txtNomorKartuT.setText(claim.nomorKartuT);
        txtEpisodes1.setText(String.valueOf(claim.episodes1));
        txtEpisodes2.setText(String.valueOf(claim.episodes2));
        txtEpisodes3.setText(String.valueOf(claim.episodes3));
        txtEpisodes4.setText(String.valueOf(claim.episodes4));
        txtEpisodes5.setText(String.valueOf(claim.episodes5));
        txtEpisodes6.setText(String.valueOf(claim.episodes6));
    }

    private void populateBilling(EKlaimBillingAggregator.BillingData billing) {
        txtProsedurNonBedah.setText(String.valueOf(billing.prosedurNonBedah));
        txtProsedurBedah.setText(String.valueOf(billing.prosedurBedah));
        txtKonsultasi.setText(String.valueOf(billing.konsultasi));
        txtTenagaAhli.setText(String.valueOf(billing.tenagaAhli));
        txtKeperawatan.setText(String.valueOf(billing.keperawatan));
        txtPenunjang.setText(String.valueOf(billing.penunjang));
        txtRadiologi.setText(String.valueOf(billing.radiologi));
        txtLaboratorium.setText(String.valueOf(billing.laboratorium));
        txtPelayananDarah.setText(String.valueOf(billing.pelayananDarah));
        txtKamar.setText(String.valueOf(billing.kamar));
        txtRawatIntensif.setText(String.valueOf(billing.rawatIntensif));
        txtObatKronis.setText(String.valueOf(billing.obatKronis));
        txtObatKemoterapi.setText(String.valueOf(billing.obatKemoterapi));
        txtObat.setText(String.valueOf(billing.obat));
        txtAlkes.setText(String.valueOf(billing.alkes));
        txtBmhp.setText(String.valueOf(billing.bmhp));
        txtSewaAlat.setText(String.valueOf(billing.sewaAlat));
        txtRehabilitasi.setText(String.valueOf(billing.rehabilitasi));
        txtTarifPoliEks.setText("0");
        lblTotalBilling.setText("Total Billing: " + billing.totalBilling);
        recalculateBilling();
    }

    private void refreshIdrgDisplay() {
        try (PreparedStatement ps = koneksi.prepareStatement("select * from idrg_grouping_smc where no_sep = ?")) {
            ps.setString(1, noSep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String mdc = rs.getString("mdc_number");
                    lblIdrgMdc.setText(mdc + " - " + rs.getString("mdc_description"));
                    lblIdrgDrgCode.setText(rs.getString("drg_code"));
                    lblIdrgDrgDesc.setText(rs.getString("drg_description"));

                    if ("36".equals(mdc)) {
                        lblIdrgMdc.setForeground(Color.RED);
                        lblIdrgDrgCode.setForeground(Color.RED);
                        lblIdrgDrgDesc.setForeground(Color.RED);
                    } else {
                        lblIdrgMdc.setForeground(Color.BLACK);
                        lblIdrgDrgCode.setForeground(Color.BLACK);
                        lblIdrgDrgDesc.setForeground(Color.BLACK);
                    }
                } else {
                    lblIdrgMdc.setText("-");
                    lblIdrgDrgCode.setText("-");
                    lblIdrgDrgDesc.setText("-");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif refreshIdrgDisplay: " + e);
        }
    }

    private void refreshInacbgDisplay() {
        try (PreparedStatement ps = koneksi.prepareStatement("select * from inacbg_grouping_stage12 where no_sep = ?")) {
            ps.setString(1, noSep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblInacbgCode.setText(rs.getString("code_cbg"));
                    lblInacbgDesc.setText(rs.getString("deskripsi"));
                    lblInacbgTarif.setText(rs.getString("tarif"));
                    lblInacbgTopUp.setText(rs.getString("top_up"));

                    if (rs.getString("code_cbg") != null && rs.getString("code_cbg").startsWith("X")) {
                        lblInacbgCode.setForeground(Color.RED);
                    } else {
                        lblInacbgCode.setForeground(Color.BLACK);
                    }
                } else {
                    lblInacbgCode.setText("-");
                    lblInacbgDesc.setText("-");
                    lblInacbgTarif.setText("-");
                    lblInacbgTopUp.setText("-");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif refreshInacbgDisplay: " + e);
        }
    }

    private void loadSpecialCmgOptions() {
        cmbSpecialProcedure.removeAllItems();
        cmbSpecialProsthesis.removeAllItems();
        cmbSpecialInvestigation.removeAllItems();
        cmbSpecialDrug.removeAllItems();

        cmbSpecialProcedure.addItem("");
        cmbSpecialProsthesis.addItem("");
        cmbSpecialInvestigation.addItem("");
        cmbSpecialDrug.addItem("");

        try (PreparedStatement ps = koneksi.prepareStatement("select * from tempinacbg where coder_nik = ?")) {
            ps.setString(1, coderNik);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("cmg_code");
                    String desc = rs.getString("cmg_description");
                    String type = rs.getString("cmg_type");
                    String item = code + " - " + desc;

                    switch (type.toLowerCase()) {
                        case "special procedures": cmbSpecialProcedure.addItem(item); break;
                        case "special prosthesis": cmbSpecialProsthesis.addItem(item); break;
                        case "special investigation": cmbSpecialInvestigation.addItem(item); break;
                        case "special drug": cmbSpecialDrug.addItem(item); break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif loadSpecialCmgOptions: " + e);
        }
    }

    private void recalculateBilling() {
        long total = 0;
        widget.TextBox[] values = getBillingValueFields();
        widget.TextBox[] discounts = getBillingDiscountFields();

        for (int i = 0; i < values.length; i++) {
            total += parseLong(values[i].getText()) - parseLong(discounts[i].getText());
        }

        lblTotalRincian.setText("Total Rincian: " + total);
    }

    private widget.TextBox[] getBillingValueFields() {
        return new widget.TextBox[]{txtProsedurNonBedah, txtProsedurBedah, txtKonsultasi, txtTenagaAhli,
                txtKeperawatan, txtPenunjang, txtRadiologi, txtLaboratorium, txtPelayananDarah,
                txtKamar, txtRawatIntensif, txtObatKronis, txtObatKemoterapi,
                txtObat, txtAlkes, txtBmhp, txtSewaAlat, txtRehabilitasi, txtTarifPoliEks};
    }

    private widget.TextBox[] getBillingDiscountFields() {
        return new widget.TextBox[]{txtDiscProsedurNonBedah, txtDiscProsedurBedah, txtDiscKonsultasi, txtDiscTenagaAhli,
                txtDiscKeperawatan, txtDiscPenunjang, txtDiscRadiologi, txtDiscLaboratorium, txtDiscPelayananDarah,
                txtDiscKamar, txtDiscRawatIntensif, txtDiscObatKronis, txtDiscObatKemoterapi,
                txtDiscObat, txtDiscAlkes, txtDiscBmhp, txtDiscSewaAlat, txtDiscRehabilitasi, txtDiscTarifPoliEks};
    }

    private boolean validateBilling() {
        long totalBilling = Sequel.cariIntegerSmc(
                "select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = ?", noRawat);

        long totalRincian = 0;
        widget.TextBox[] values = getBillingValueFields();
        widget.TextBox[] discounts = getBillingDiscountFields();

        for (int i = 0; i < values.length; i++) {
            totalRincian += parseLong(values[i].getText()) - parseLong(discounts[i].getText());
        }

        return Math.round(totalBilling - totalRincian) == 0;
    }

    private void setFormEditable(boolean editable) {
        txtTglKeluar.setEditable(editable);
        cmbKelasRawat.setEnabled(editable);
        cmbCaraMasuk.setEnabled(editable);
        cmbAdlSubAcute.setEnabled(editable);
        cmbAdlChronic.setEnabled(editable);
        cmbIcuIndikator.setEnabled(editable);
        txtIcuLos.setEditable(editable);
        txtVentilatorHour.setEditable(editable);
        cmbUpgradeClassInd.setEnabled(editable);
        cmbUpgradeClassClass.setEnabled(editable);
        txtUpgradeClassLos.setEditable(editable);
        txtAddPaymentPct.setEditable(editable);
        txtBirthWeight.setEditable(editable);
        txtSistole.setEditable(editable);
        txtDiastole.setEditable(editable);
        cmbDischargeStatus.setEnabled(editable);
        txtNoSitb.setEditable(editable);
        cmbDializerSingleUse.setEnabled(editable);

        // COVID fields
        cmbPemulasaraanJenazah.setEnabled(editable);
        cmbKantongJenazah.setEnabled(editable);
        cmbPetiJenazah.setEnabled(editable);
        cmbPlastikErat.setEnabled(editable);
        cmbDesinfektanJenazah.setEnabled(editable);
        cmbMobilJenazah.setEnabled(editable);
        cmbDesinfektanMobilJenazah.setEnabled(editable);
        cmbCovid19StatusCd.setEnabled(editable);
        cmbCovid19CcInd.setEnabled(editable);
        txtNomorKartuT.setEditable(editable);
        txtEpisodes1.setEditable(editable);
        txtEpisodes2.setEditable(editable);
        txtEpisodes3.setEditable(editable);
        txtEpisodes4.setEditable(editable);
        txtEpisodes5.setEditable(editable);
        txtEpisodes6.setEditable(editable);

        // New parameter fields
        cmbVentilatorUseInd.setEnabled(editable);
        txtVentilatorStartDttm.setEditable(editable);
        txtVentilatorStopDttm.setEditable(editable);
        cmbUpgradeClassPayor.setEnabled(editable);
        cmbBayiLahirStatusCd.setEnabled(editable);
        txtKantongDarah.setEditable(editable);
        cmbAlteplaseInd.setEnabled(editable);
        cmbApgarM1Appearance.setEnabled(editable);
        cmbApgarM1Pulse.setEnabled(editable);
        cmbApgarM1Grimace.setEnabled(editable);
        cmbApgarM1Activity.setEnabled(editable);
        cmbApgarM1Respiration.setEnabled(editable);
        cmbApgarM5Appearance.setEnabled(editable);
        cmbApgarM5Pulse.setEnabled(editable);
        cmbApgarM5Grimace.setEnabled(editable);
        cmbApgarM5Activity.setEnabled(editable);
        cmbApgarM5Respiration.setEnabled(editable);
        txtUsiaKehamilan.setEditable(editable);
        txtGravida.setEditable(editable);
        txtPartus.setEditable(editable);
        txtAbortus.setEditable(editable);
        cmbOnsetKontraksi.setEnabled(editable);
        for (DeliveryRow dr : deliveryRows) {
            dr.setEditable(editable);
        }

        // Billing fields
        widget.TextBox[] allBillingFields = new widget.TextBox[getBillingValueFields().length + getBillingDiscountFields().length];
        System.arraycopy(getBillingValueFields(), 0, allBillingFields, 0, getBillingValueFields().length);
        System.arraycopy(getBillingDiscountFields(), 0, allBillingFields, getBillingValueFields().length, getBillingDiscountFields().length);
        for (widget.TextBox f : allBillingFields) {
            f.setEditable(editable);
        }
    }

    private void appendSpecialCmgFromForm(StringBuilder sb, String selected) {
        if (selected != null && !selected.isEmpty()) {
            String code = selected.split(" - ")[0].trim();
            sb.append(code).append("#");
        }
    }

    private widget.ComboBox buildAdlCombo() {
        widget.ComboBox cmb = new widget.ComboBox();
        cmb.addItem("0");
        for (int i = 12; i <= 60; i++) {
            cmb.addItem(String.valueOf(i));
        }
        return cmb;
    }

    private widget.ComboBox buildCombo(String... items) {
        widget.ComboBox cmb = new widget.ComboBox();
        for (String item : items) {
            cmb.addItem(item);
        }
        return cmb;
    }

    private widget.Label newLabel(String text) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text);
        return lbl;
    }

    private widget.Label createLeftLabel() {
        widget.Label lbl = new widget.Label();
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        return lbl;
    }

    private widget.Label createBoldLabel(String text) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        return lbl;
    }

    private widget.TextBox newTextBox(String text) {
        widget.TextBox tb = new widget.TextBox();
        tb.setText(text);
        return tb;
    }

    private void selectComboValue(widget.ComboBox cmb, String value) {
        if (value == null || value.isEmpty()) return;
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (cmb.getItemAt(i).equals(value)) {
                cmb.setSelectedIndex(i);
                return;
            }
        }
    }

    private String getFieldValue(widget.TextBox field) {
        String val = field.getText().trim();
        return val.isEmpty() ? "0" : val;
    }

    private String getComboValue(widget.ComboBox cmb) {
        String val = (String) cmb.getSelectedItem();
        return val == null ? "" : val;
    }

    private long parseLong(String val) {
        try {
            String cleaned = val.trim().replace(",", ".");
            if (cleaned.contains(".")) {
                return Math.round(Double.parseDouble(cleaned));
            }
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
