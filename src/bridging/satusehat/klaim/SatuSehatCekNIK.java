/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author khanzasoft
 */
public class SatuSehatCekNIK {
    private String link="",json="";
    public String birthDate="",province="",provincename="",city="",cityname="",district="",districtname="",village="",
                  villagename="",rt="",rw="",line="",postalCode="",gender="",noktp="",idpasien="",maritalStatus="",
                  name="",phone="",email="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private sekuel Sequel = new sekuel();
    private FileReader dataPropinsi,dataKabupaten,dataKecamatan,dataKelurahan;
    private final Connection koneksi = koneksiDB.condb();
        
    public SatuSehatCekNIK(){
        super();
        try {
            link=koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }  
        
        try {
            dataPropinsi = new FileReader("./cache/propinsi.iyem");
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        } 
        
        try {
            dataKabupaten = new FileReader("./cache/kabupaten.iyem");
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        } 
        
        try {
            dataKecamatan= new FileReader("./cache/kecamatan.iyem");
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        } 
        
        try {
            dataKelurahan= new FileReader("./cache/kelurahan.iyem");
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        } 
    }
    
    public void tampil(String cari) {
        try{
            birthDate="";province="";provincename="";city="";cityname="";district="";districtname="";village="";villagename="";
            rt="";rw="";line="";postalCode="";gender="";noktp="";idpasien="";maritalStatus="";name="";phone="";email="";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+link+"/Patient?identifier=https://fhir.kemkes.go.id/id/nik|"+cari);
            json=api.getRest().exchange(link+"/Patient?identifier=https://fhir.kemkes.go.id/id/nik|"+cari, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("JSON : "+json);
            root = mapper.readTree(json);
            for(JsonNode list:root.path("entry")){
                idpasien=list.path("resource").path("id").asText();
                noktp=cari;
                try{
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                    requestEntity = new HttpEntity(headers);
                    System.out.println("URL : "+link+"/Patient/"+idpasien);
                    json=api.getRest().exchange(link+"/Patient/"+idpasien, HttpMethod.GET, requestEntity, String.class).getBody();
                    System.out.println("JSON : "+json);
                    root = mapper.readTree(json);
                    gender = root.path("gender").asText().toLowerCase().equals("male")?"Laki-laki":"Perempuan";
                    birthDate = root.path("birthDate").asText();
                    maritalStatus = root.path("maritalStatus").path("text").asText().toLowerCase().equals("married")?"Menikah":"Belum Menikah";
                    for(JsonNode listname:root.path("name")){
                        name=listname.path("text").asText();
                    }
                    for(JsonNode listtelecom:root.path("telecom")){
                        if(listtelecom.path("system").asText().equals("phone")){
                            phone=listtelecom.path("value").asText();
                        }else if(listtelecom.path("system").asText().equals("email")){
                            email=listtelecom.path("value").asText();
                        }
                    }
                    for(JsonNode listaddress:root.path("address")){
                        line=listaddress.path("line").get(0).asText();
                        postalCode=listaddress.path("postalCode").asText();
                        for(JsonNode listextension:listaddress.path("extension")){
                            for(JsonNode listextensionextension:listextension.path("extension")){
                                if(listextensionextension.path("url").asText().equals("province")){
                                    province=listextensionextension.path("valueCode").asText();
                                }else if(listextensionextension.path("url").asText().equals("city")){
                                    city=listextensionextension.path("valueCode").asText();
                                }else if(listextensionextension.path("url").asText().equals("district")){
                                    district=listextensionextension.path("valueCode").asText();
                                }else if(listextensionextension.path("url").asText().equals("village")){
                                    village=listextensionextension.path("valueCode").asText();
                                }else if(listextensionextension.path("url").asText().equals("rt")){
                                    rt=listextensionextension.path("valueCode").asText();
                                }else if(listextensionextension.path("url").asText().equals("rw")){
                                    rw=listextensionextension.path("valueCode").asText();
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Notifikasi : "+e);
                }
                response = mapper.readTree(dataKelurahan).path("kelurahan");
                for(JsonNode listkelurahan:response){
                    if(listkelurahan.path("id").asText().toLowerCase().equals(village)&&listkelurahan.path("id_kecamatan").asText().equals(district)){
                        villagename=listkelurahan.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataKecamatan).path("kecamatan");
                for(JsonNode listkcamatan:response){
                    if(listkcamatan.path("id").asText().toLowerCase().equals(district)&&listkcamatan.path("id_kabupaten").asText().equals(city)){
                        districtname=listkcamatan.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataKabupaten).path("kabupaten");
                for(JsonNode listkabupaten:response){
                    if(listkabupaten.path("id").asText().toLowerCase().equals(city)&&listkabupaten.path("id_propinsi").asText().equals(province)){
                        cityname=listkabupaten.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataPropinsi).path("propinsi");
                for(JsonNode listpropinsi:response){
                    if(listpropinsi.path("id").asText().toLowerCase().equals(province)){
                        provincename=listpropinsi.path("nama").asText();
                    }
                }
            }
            
            if(name.equals("")){
                try{
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                    requestEntity = new HttpEntity(headers);
                    System.out.println("URL : "+link+"/Patient/"+cari);
                    json=api.getRest().exchange(link+"/Patient/"+cari, HttpMethod.GET, requestEntity, String.class).getBody();
                    System.out.println("JSON : "+json);
                    root = mapper.readTree(json);
                    idpasien=cari;
                    gender = root.path("gender").asText().toLowerCase().equals("male")?"Laki-laki":"Perempuan";
                    birthDate = root.path("birthDate").asText();
                    maritalStatus = root.path("maritalStatus").path("text").asText().toLowerCase().equals("married")?"Menikah":"Belum Menikah";
                    for(JsonNode listname:root.path("name")){
                        name=listname.path("text").asText();
                    }
                    for(JsonNode listtelecom:root.path("telecom")){
                        if(listtelecom.path("system").asText().equals("phone")){
                            phone=listtelecom.path("value").asText();
                        }
                        if(listtelecom.path("system").asText().equals("email")){
                            email=listtelecom.path("value").asText();
                        }
                    }
                    for(JsonNode listnoktp:root.path("identifier")){
                        if(listnoktp.path("system").asText().equals("https://fhir.kemkes.go.id/id/nik")){
                            noktp=listnoktp.path("value").asText();
                        }
                    }
                    for(JsonNode listaddress:root.path("address")){
                        line=listaddress.path("line").get(0).asText();
                        postalCode=listaddress.path("postalCode").asText();
                        for(JsonNode listextension:listaddress.path("extension")){
                            for(JsonNode listextensionextension:listextension.path("extension")){
                                if(listextensionextension.path("url").asText().equals("province")){
                                    province=listextensionextension.path("valueCode").asText();
                                }
                                if(listextensionextension.path("url").asText().equals("city")){
                                    city=listextensionextension.path("valueCode").asText();
                                }
                                if(listextensionextension.path("url").asText().equals("district")){
                                    district=listextensionextension.path("valueCode").asText();
                                }
                                if(listextensionextension.path("url").asText().equals("village")){
                                    village=listextensionextension.path("valueCode").asText();
                                }
                                if(listextensionextension.path("url").asText().equals("rt")){
                                    rt=listextensionextension.path("valueCode").asText();
                                }
                                if(listextensionextension.path("url").asText().equals("rw")){
                                    rw=listextensionextension.path("valueCode").asText();
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Notifikasi : "+e);
                }
                response = mapper.readTree(dataKelurahan).path("kelurahan");
                for(JsonNode listkelurahan:response){
                    if(listkelurahan.path("id").asText().toLowerCase().equals(village)&&listkelurahan.path("id_kecamatan").asText().equals(district)){
                        villagename=listkelurahan.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataKecamatan).path("kecamatan");
                for(JsonNode listkcamatan:response){
                    if(listkcamatan.path("id").asText().toLowerCase().equals(district)&&listkcamatan.path("id_kabupaten").asText().equals(city)){
                        districtname=listkcamatan.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataKabupaten).path("kabupaten");
                for(JsonNode listkabupaten:response){
                    if(listkabupaten.path("id").asText().toLowerCase().equals(city)&&listkabupaten.path("id_propinsi").asText().equals(province)){
                        cityname=listkabupaten.path("nama").asText();
                    }
                }
                response = mapper.readTree(dataPropinsi).path("propinsi");
                for(JsonNode listpropinsi:response){
                    if(listpropinsi.path("id").asText().toLowerCase().equals(province)){
                        provincename=listpropinsi.path("nama").asText();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        
        if(name.equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Belum Ada data di Server Satu Sehat");
        }
    }
    
    // ====================== CEK IHS PASIEN ======================

    /** Status hasil {@link #cekIHS(String)}. Dipisah supaya pemanggil bisa membedakan
     *  "tidak terdaftar di SATUSEHAT" dari "gagal menghubungi SATUSEHAT" — dua hal yang
     *  penanganannya berbeda (yang pertama perlu registrasi pasien, yang kedua perlu ulangi). */
    public static final String IHS_NIK_TIDAK_VALID  = "NIK_TIDAK_VALID";
    public static final String IHS_DITEMUKAN        = "DITEMUKAN";
    public static final String IHS_CACHE_COCOK      = "CACHE_COCOK";
    public static final String IHS_CACHE_DIPERBAIKI = "CACHE_DIPERBAIKI";
    public static final String IHS_TIDAK_DITEMUKAN  = "TIDAK_DITEMUKAN";
    public static final String IHS_GAGAL            = "GAGAL";

    /**
     * Hasil pencarian IHS pasien: id, status, pesan, plus data Patient APA ADANYA dari respons
     * server. Field data sengaja dibiarkan "" bila SATUSEHAT tidak mengirimkannya — respons
     * pencarian by NIK isinya bisa berbeda-beda (contoh dev hanya membawa name), jadi yang
     * ditampilkan ke petugas harus benar-benar yang dibalikkan server, bukan tebakan.
     */
    public static class HasilIHS {
        /** IHS number bila ketemu, "" bila tidak. */
        public String ihs = "";
        /** Salah satu konstanta IHS_*. */
        public String status = "";
        public String pesan = "";
        /** true bila mapping NIK->IHS tersimpan di satu_sehat_ihs_patient setelah pengecekan ini. */
        public boolean tersimpanLokal = false;
        /**
         * true bila SATUSEHAT punya resource Patient untuk NIK ini tapi tidak ada satu pun yang
         * ber-identifier ihs-number (Patient provisional). Bukan "tidak terdaftar": NIK-nya sudah
         * dipakai di server, jadi pembuatan Patient baru harus dibatalkan supaya tidak jadi ganda.
         */
        public boolean adaTanpaIhs = false;

        // ---- data dari resource Patient (kosong bila tidak ada di respons) ----
        public String nama = "";
        public String gender = "";
        public String tglLahir = "";
        /** Nilai identifier NIK persis seperti dibalikkan server — biasanya bertopeng "################". */
        public String nikServer = "";
        /** "Aktif"/"Tidak aktif"/"" bila field active tidak dikirim. */
        public String aktif = "";

        public boolean ada() {
            return !ihs.equals("");
        }
    }

    /**
     * Cari IHS number pasien berdasarkan NIK, sekaligus rawat cache lokal satu_sehat_ihs_patient.
     *
     * Pencarian by NIK ke server SELALU dilakukan dan menjadi satu-satunya sumber kebenaran:
     * cache hanya dipakai untuk tahu apakah mapping lokal sudah benar. Alasannya, bila cache
     * pernah salah isi, ID milik pasien LAIN akan lolos selamanya kalau hanya dicek
     * "apakah Patient/{id} ada?" — ia memang ada di server. Akibatnya seluruh data klinis
     * pasien dikirim ke rekam orang lain, dan hanya Account yang menolak (Rule 10226) karena
     * cuma dia yang membandingkan nama. Kasus nyata 28 Juli 2026: NIK 9204014804000002
     * (Claudia Sintia) ter-cache sebagai P01654557057 milik pasien lain. NIK pada resource
     * Patient dikembalikan BERTOPENG ("################") sehingga tidak bisa dipakai
     * memverifikasi — membandingkan id hasil pencarian by NIK adalah satu-satunya cara sahih.
     *
     * Cache TIDAK dihapus bila panggilan ke server gagal: kegagalan jaringan bukan bukti
     * bahwa mapping lokal salah.
     */
    public HasilIHS cekIHS(String nik) {
        HasilIHS hasil = new HasilIHS();
        nik = (nik == null) ? "" : nik.trim();

        // NIK wajib 16 digit numerik; SATUSEHAT selalu balas 400 untuk NIK malformed.
        if (!nik.matches("\\d{16}")) {
            hasil.status = IHS_NIK_TIDAK_VALID;
            hasil.pesan = "NIK harus 16 digit angka.";
            System.out.println("Notifikasi cekIHS : NIK tidak valid = '" + nik + "'");
            return hasil;
        }

        JsonNode bundle;
        try {
            bundle = bundlePatientByNik(nik);
        } catch (Exception e) {
            hasil.status = IHS_GAGAL;
            hasil.pesan = "Gagal menghubungi SATUSEHAT. Mapping lokal dibiarkan apa adanya."
                    + "\n\nSebabnya:\n" + ringkasError(e);
            System.out.println("Notifikasi cekIHS (NIK=" + nik + ") : " + e);
            return hasil;
        }

        JsonNode patient = pilihPatientBerIhs(bundle);
        if (patient == null && adaEntryPatient(bundle)) {
            hasil.adaTanpaIhs = true;
            System.out.println("Notifikasi cekIHS : NIK=" + nik + " punya entry Patient tapi tidak ada yang "
                    + "ber-identifier ihs-number. Sengaja tidak dipakai supaya id Patient provisional "
                    + "tidak ter-cache, dan pembuatan Patient baru dibatalkan.");
        }

        String idServer = (patient == null) ? "" : patient.path("id").asText();
        String idCache = ambilCacheIhs(nik);

        if (idServer.equals("")) {
            if (!idCache.equals("")) {
                Sequel.queryu2("delete from satu_sehat_ihs_patient where nikpasien=?", 1, new String[]{nik});
                System.out.println("Notifikasi cekIHS : NIK=" + nik + " tidak ada di SATUSEHAT, "
                        + "mapping lokal usang (" + idCache + ") dihapus.");
                hasil.pesan = "Pasien tidak ditemukan di SATUSEHAT. Mapping lokal yang usang sudah dihapus.";
            } else {
                hasil.pesan = "Pasien tidak ditemukan di SATUSEHAT.";
            }
            hasil.status = IHS_TIDAK_DITEMUKAN;
            return hasil;
        }

        hasil.ihs = idServer;
        idpasien = idServer;
        isiDataPatient(hasil, patient);

        if (idCache.equals(idServer)) {
            hasil.status = IHS_CACHE_COCOK;
            hasil.tersimpanLokal = true;
            hasil.pesan = "IHS ditemukan dan cocok dengan data lokal.";
            return hasil;
        }

        hasil.status = idCache.equals("") ? IHS_DITEMUKAN : IHS_CACHE_DIPERBAIKI;
        hasil.tersimpanLokal = simpanCacheIhs(nik, idServer);
        if (!idCache.equals("")) {
            System.out.println("Notifikasi cekIHS : mapping diperbaiki. NIK=" + nik
                    + ", ID lama=" + idCache + ", ID benar=" + idServer);
        }
        hasil.pesan = hasil.tersimpanLokal
                ? (idCache.equals("") ? "IHS ditemukan dan disimpan ke data lokal."
                                      : "IHS ditemukan. Mapping lokal yang salah sudah diperbaiki.")
                : "IHS ditemukan, tetapi belum bisa disimpan lokal karena NIK ini belum ada "
                  + "di data pasien. Simpan dulu data pasiennya, lalu cek ulang.";
        return hasil;
    }

    /**
     * Salin data Patient dari respons server ke hasil. Setiap field dibiarkan "" bila tidak
     * dikirim server — tidak ada nilai default supaya petugas tidak salah membaca data kosong
     * sebagai data yang terkonfirmasi.
     */
    static void isiDataPatient(HasilIHS hasil, JsonNode patient) {
        if (patient == null) {
            return;
        }
        for (JsonNode nama : patient.path("name")) {
            String teks = nama.path("text").asText();
            if (!teks.equals("")) {
                hasil.nama = teks;
                break;
            }
        }
        String gender = patient.path("gender").asText();
        if (gender.equalsIgnoreCase("male")) {
            hasil.gender = "Laki-laki";
        } else if (gender.equalsIgnoreCase("female")) {
            hasil.gender = "Perempuan";
        } else {
            hasil.gender = gender;
        }
        hasil.tglLahir = patient.path("birthDate").asText();
        if (patient.has("active")) {
            hasil.aktif = patient.path("active").asBoolean() ? "Aktif" : "Tidak aktif";
        }
        for (JsonNode ident : patient.path("identifier")) {
            if (ident.path("system").asText().endsWith("/nik")) {
                hasil.nikServer = ident.path("value").asText();
                break;
            }
        }
    }

    /** Mapping NIK->IHS yang tersimpan lokal; "" bila belum ada. */
    private String ambilCacheIhs(String nik) {
        String id = Sequel.cariIsi("select ihspasien from satu_sehat_ihs_patient where nikpasien=?", nik);
        return (id == null) ? "" : id.trim();
    }

    /**
     * Simpan mapping NIK->IHS. Mengembalikan false bila NIK belum ada di master pasien:
     * satu_sehat_ihs_patient.nikpasien punya foreign key ke pasien.no_ktp, jadi insert-nya
     * pasti ditolak constraint — dicek di depan supaya tidak jadi kegagalan diam-diam.
     */
    private boolean simpanCacheIhs(String nik, String ihs) {
        if (Sequel.cariInteger("select count(no_rkm_medis) from pasien where no_ktp=?", nik) <= 0) {
            System.out.println("Notifikasi cekIHS : NIK=" + nik + " belum ada di tabel pasien, "
                    + "mapping IHS tidak disimpan.");
            return false;
        }
        Sequel.queryu2("replace into satu_sehat_ihs_patient (nikpasien,ihspasien) values (?,?)",
                2, new String[]{nik, ihs});
        System.out.println("Notifikasi cekIHS : mapping disimpan. NIK=" + nik + ", IHS=" + ihs);
        return true;
    }

    // ====================== BUAT PATIENT BARU ======================

    /** Patient baru berhasil dibuat di SATUSEHAT dan IHS-nya langsung dipakai. */
    public static final String IHS_DIBUAT             = "DIBUAT";
    /** Pembuatan dibatalkan karena data pasien di SIMRS belum layak kirim. */
    public static final String IHS_DATA_BELUM_LENGKAP = "DATA_BELUM_LENGKAP";
    /** Pembuatan sudah dicoba tapi ditolak server. */
    public static final String IHS_GAGAL_BUAT         = "GAGAL_BUAT";

    /**
     * Cari IHS; bila pasien memang belum terdaftar, daftarkan sekalian ke SATUSEHAT.
     *
     * Pembuatan HANYA dilakukan pada status {@link #IHS_TIDAK_DITEMUKAN}, yaitu ketika server
     * sudah menjawab dan jawabannya kosong. Status {@link #IHS_GAGAL} (token/jaringan/HTTP error)
     * sengaja TIDAK pernah memicu pembuatan: kalau tidak, satu gangguan jaringan sesaat akan
     * melahirkan Patient duplikat untuk pasien yang sebenarnya sudah punya IHS.
     */
    public HasilIHS cekIHSAtauBuat(String nik) {
        HasilIHS hasil = cekIHS(nik);
        if (!hasil.status.equals(IHS_TIDAK_DITEMUKAN)) {
            return hasil;
        }
        if (hasil.adaTanpaIhs) {
            hasil.pesan = "NIK ini sudah dipakai Patient provisional di SATUSEHAT (belum ber-IHS). "
                    + "Tidak dibuatkan Patient baru supaya tidak jadi data ganda.";
            return hasil;
        }
        return buatPatient((nik == null) ? "" : nik.trim(), hasil);
    }

    /**
     * Daftarkan pasien ke SATUSEHAT berdasarkan NIK, sumber datanya tabel pasien SIMRS.
     * Mengembalikan IHS number bila berhasil, "" bila tidak.
     *
     * Dipanggil sendiri hanya bila memang ingin memaksa pendaftaran; alur normal cukup lewat
     * {@link #cekIHSAtauBuat(String)} yang sudah memasang semua pengaman urutannya.
     */
    public String createPatientByNIK(String nik) {
        return daftarkanPasien(nik).ihs;
    }

    /**
     * Sama dengan {@link #createPatientByNIK(String)} tapi mengembalikan hasil lengkapnya
     * (status + pesan), supaya pemanggil dari UI bisa memberi tahu petugas apa yang harus
     * dibereskan ketika pendaftaran ditolak.
     */
    public HasilIHS daftarkanPasien(String nik) {
        return buatPatient((nik == null) ? "" : nik.trim(), new HasilIHS());
    }

    private HasilIHS buatPatient(String nik, HasilIHS hasil) {
        if (!nik.matches("\\d{16}")) {
            hasil.status = IHS_NIK_TIDAK_VALID;
            hasil.pesan = "NIK harus 16 digit angka.";
            return hasil;
        }

        DataPasien d = ambilPasienLokal(nik);
        if (d == null) {
            hasil.status = IHS_DATA_BELUM_LENGKAP;
            hasil.pesan = "NIK ini belum ada di data pasien SIMRS, jadi tidak ada yang bisa didaftarkan.";
            System.out.println("Notifikasi buatPatient : NIK=" + nik + " tidak ada di tabel pasien.");
            return hasil;
        }

        String kurang = d.yangKurang();
        if (!kurang.equals("")) {
            hasil.status = IHS_DATA_BELUM_LENGKAP;
            hasil.pesan = "Data pasien belum layak dikirim: " + kurang + ". Lengkapi dulu di menu pasien.";
            System.out.println("Notifikasi buatPatient : NIK=" + nik + " ditolak, " + kurang);
            return hasil;
        }

        KodeWilayah w = petakanWilayah(d);
        if (!w.lengkap()) {
            hasil.status = IHS_DATA_BELUM_LENGKAP;
            hasil.pesan = "Alamat pasien tidak bisa dipetakan ke kode wilayah Kemendagri (" + w.gagalDi
                    + "). Betulkan dulu alamatnya, jangan dikira-kira.";
            System.out.println("Notifikasi buatPatient : NIK=" + nik + " gagal petakan wilayah - " + w.gagalDi);
            return hasil;
        }

        try {
            String id = postPatient(bodyPatient(mapper, nik, d, w));
            if (id.equals("")) {
                hasil.status = IHS_GAGAL_BUAT;
                hasil.pesan = "SATUSEHAT menerima kiriman tapi tidak mengembalikan IHS number.";
                return hasil;
            }
            hasil.ihs = id;
            idpasien = id;
            hasil.status = IHS_DIBUAT;
            hasil.nama = d.nama;
            hasil.gender = d.jk.equals("P") ? "Perempuan" : "Laki-laki";
            hasil.tglLahir = d.tglLahir;
            hasil.tersimpanLokal = simpanCacheIhs(nik, id);
            hasil.pesan = "Pasien berhasil didaftarkan ke SATUSEHAT, IHS " + id + ".";
            System.out.println("Notifikasi buatPatient : NIK=" + nik + " -> IHS=" + id);
            return hasil;
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String badan = e.getResponseBodyAsString();
            hasil.status = IHS_GAGAL_BUAT;
            hasil.pesan = pesanPenolakan(badan, e.getStatusCode().value());
            System.out.println("Notifikasi buatPatient (NIK=" + nik + ") : " + e.getStatusCode()
                    + " - " + badan);
            return hasil;
        } catch (Exception e) {
            hasil.status = IHS_GAGAL_BUAT;
            hasil.pesan = "Gagal mendaftarkan pasien ke SATUSEHAT.\n\nSebabnya:\n" + ringkasError(e);
            System.out.println("Notifikasi buatPatient (NIK=" + nik + ") : " + e);
            return hasil;
        }
    }

    /**
     * Terjemahkan penolakan server jadi pesan yang bisa ditindaklanjuti petugas.
     *
     * Dua penolakan di bawah sudah diuji ke staging 1 Agt 2026 dan BUKAN soal payload — keduanya
     * berarti pasien sebenarnya sudah ada, jadi yang benar adalah mencarinya, bukan mengulang POST:
     *  - "Found duplicate ... Patient" (RuleNumber 20002) — NIK sudah punya Patient.
     *  - "Patient data exists..."      — NIK ada di Dukcapil tapi belum ter-index untuk cari by NIK.
     */
    static String pesanPenolakan(String badan, int kode) {
        String b = (badan == null) ? "" : badan;
        if (b.contains("Found duplicate") && b.contains("Patient")) {
            return "NIK ini sudah punya Patient di SATUSEHAT, tetapi tidak muncul saat dicari by NIK. "
                 + "Cari manual memakai nama + tanggal lahir + jenis kelamin, jangan didaftarkan ulang.";
        }
        if (b.contains("Patient data exists")) {
            return "NIK terdaftar di Dukcapil tapi belum bisa dicari lewat NIK. Cari manual memakai "
                 + "nama + tanggal lahir + jenis kelamin, jangan didaftarkan ulang.";
        }
        return "SATUSEHAT menolak pendaftaran (HTTP " + kode + ").\n\nKata servernya:\n"
                + rincianPenolakan(b);
    }

    /**
     * Keterangan penolakan yang bisa dibaca petugas, diambil dari OperationOutcome milik server.
     *
     * Sengaja ditarik ke dialog, bukan cuma dicetak ke konsol: petugas pendaftaran tidak membuka
     * jendela log, jadi tanpa ini kegagalan hanya terlihat sebagai "gagal" tanpa sebab. Bila
     * balasannya bukan JSON yang dikenal, isinya ditampilkan apa adanya dan dipotong supaya
     * kotak dialog tidak meledak.
     */
    static String rincianPenolakan(String badan) {
        String b = (badan == null) ? "" : badan.trim();
        if (b.equals("")) {
            return "(server tidak mengirim keterangan apa pun)";
        }
        StringBuilder s = new StringBuilder();
        try {
            JsonNode akar = new ObjectMapper().readTree(b);
            for (JsonNode issue : akar.path("issue")) {
                String teks = issue.path("details").path("text").asText();
                if (teks.equals("")) {
                    teks = issue.path("diagnostics").asText();
                }
                if (teks.equals("")) {
                    teks = issue.path("code").asText();
                }
                if (!teks.equals("")) {
                    if (s.length() > 0) {
                        s.append("\n");
                    }
                    s.append("- ").append(teks);
                }
            }
        } catch (Exception e) {
            // Balasan bukan OperationOutcome (mis. halaman error proxy). Bukan keanehan —
            // isinya tetap ditampilkan apa adanya di bawah, jadi tidak perlu dicatat.
        }
        String hasil = (s.length() > 0) ? s.toString() : b;
        return hasil.length() > 600 ? hasil.substring(0, 600) + " ..." : hasil;
    }

    /** Ringkasan exception untuk ditampilkan ke petugas tanpa membuka log. */
    public static String ringkasError(Throwable e) {
        String pesan = (e == null || e.getMessage() == null) ? "" : e.getMessage().trim();
        String jenis = (e == null) ? "Error" : e.getClass().getSimpleName();
        if (pesan.length() > 300) {
            pesan = pesan.substring(0, 300) + " ...";
        }
        return pesan.equals("") ? jenis : (jenis + ": " + pesan);
    }

    // ---------------------------------------------------------------- data lokal

    /** Data pasien dari SIMRS yang dibutuhkan untuk membentuk resource Patient. */
    static class DataPasien {
        String nama = "", jk = "", tglLahir = "", alamat = "", telepon = "", email = "", sttsNikah = "";
        String namaProp = "", namaKab = "", namaKec = "", namaKel = "";
        /** Nama sebelum dibersihkan; dipakai memeriksa bentuk "BY NY <nama ibu>". */
        String namaAsli = "";

        /** Daftar syarat yang belum terpenuhi; "" bila semua sudah beres. */
        String yangKurang() {
            StringBuilder s = new StringBuilder();
            if (nama.equals("")) {
                s.append("nama pasien kosong");
            }
            if (!jk.equals("L") && !jk.equals("P")) {
                if (s.length() > 0) { s.append(", "); }
                s.append("jenis kelamin belum diisi");
            }
            // SATUSEHAT menolak birthDate kosong maupun tanggal nol MySQL (0000-00-00).
            if (!tglLahir.matches("\\d{4}-\\d{2}-\\d{2}") || tglLahir.startsWith("0000")) {
                if (s.length() > 0) { s.append(", "); }
                s.append("tanggal lahir tidak sah");
            }
            if (alamat.equals("")) {
                if (s.length() > 0) { s.append(", "); }
                s.append("alamat kosong");
            }
            return s.toString();
        }
    }

    private DataPasien ambilPasienLokal(String nik) {
        DataPasien d = null;
        PreparedStatement p = null;
        ResultSet r = null;
        try {
            p = koneksi.prepareStatement(
                    "select ifnull(p.nm_pasien,'') nm_pasien, ifnull(p.jk,'') jk, "
                    + "ifnull(date_format(p.tgl_lahir,'%Y-%m-%d'),'') tgl_lahir, "
                    + "ifnull(p.alamat,'') alamat, ifnull(p.no_tlp,'') no_tlp, ifnull(p.email,'') email, "
                    + "ifnull(p.stts_nikah,'') stts_nikah, ifnull(pr.nm_prop,'') nm_prop, "
                    + "ifnull(kb.nm_kab,'') nm_kab, ifnull(kc.nm_kec,'') nm_kec, ifnull(kl.nm_kel,'') nm_kel "
                    + "from pasien p "
                    + "left join propinsi pr on pr.kd_prop=p.kd_prop "
                    + "left join kabupaten kb on kb.kd_kab=p.kd_kab "
                    + "left join kecamatan kc on kc.kd_kec=p.kd_kec "
                    + "left join kelurahan kl on kl.kd_kel=p.kd_kel "
                    + "where p.no_ktp=? limit 1");
            p.setString(1, nik);
            r = p.executeQuery();
            if (r.next()) {
                d = new DataPasien();
                d.namaAsli = r.getString("nm_pasien");
                d.nama = namaUntukKirim(d.namaAsli);
                d.jk = r.getString("jk").trim().toUpperCase();
                d.tglLahir = r.getString("tgl_lahir").trim();
                d.alamat = r.getString("alamat").trim();
                d.telepon = r.getString("no_tlp").trim();
                d.email = r.getString("email").trim();
                d.sttsNikah = r.getString("stts_nikah").trim().toUpperCase();
                d.namaProp = r.getString("nm_prop");
                d.namaKab = r.getString("nm_kab");
                d.namaKec = r.getString("nm_kec");
                d.namaKel = r.getString("nm_kel");
            }
        } catch (Exception e) {
            System.out.println("Notifikasi ambilPasienLokal (NIK=" + nik + ") : " + e);
            d = null;
        } finally {
            tutup(r, p);
        }
        return d;
    }

    /** Sapaan yang ditempelkan ke nama pasien di SIMRS, bukan bagian nama pada KTP. */
    private static final java.util.Set<String> SAPAAN = new java.util.HashSet<String>(
            java.util.Arrays.asList("NY", "TN", "AN", "NN", "SDR", "SDRI", "BY"));

    /**
     * Buang sapaan yang menempel di nama pasien, karena yang dicocokkan Dukcapil adalah nama
     * pada KTP.
     *
     * Khanza menaruhnya di BELAKANG dipisah koma ("FATIMATUZ ZAHRO,NY"). Diukur pada data dev:
     * 24.989 dari 26.185 nama memakai pola ini — NY 10.042, TN 5.856, AN 4.563, SDR 1.867,
     * NN 1.687, BY 852. Sebagian kecil menaruhnya di depan ("Ny. SITI") atau memisahnya dengan
     * spasi saja, jadi ketiga bentuk itu ditangani.
     *
     * Pengupasan hanya jalan bila potongannya PERSIS sapaan yang dikenal. Yang dibandingkan token
     * utuh, bukan akhiran huruf, sehingga nama seperti "IRFAN" atau "RAHMAWATI" tidak tersentuh.
     * Bila hasilnya jadi kosong, nama asli dikembalikan apa adanya — lebih baik mengirim nama
     * bersapaan daripada mengirim nama kosong.
     */
    /**
     * Nama yang dipakai untuk mendaftarkan pasien ke SATUSEHAT.
     *
     * Untuk bayi yang namanya masih menunjuk ibunya ("BY NY ANGGUN RAHMAWATI") sapaannya sengaja
     * TIDAK dikupas. Mengupasnya menyisakan nama ibu, dan nama itu akan menempel pada NIK bayi
     * tanpa terlihat keliru oleh siapa pun. Dikirim utuh, kekeliruannya kasat mata sehingga masih
     * bisa dikenali dan dibetulkan. Keputusan pengguna 4 Agustus 2026.
     */
    static String namaUntukKirim(String namaAsli) {
        String asli = (namaAsli == null) ? "" : namaAsli.trim();
        if (!namaMenunjukIbu(asli)) {
            return bersihkanGelar(asli);
        }
        String rapi = rapikanTandaBaca(buangPenandaKurung(asli));
        return rapi.equals("") ? asli : rapi;
    }

    /**
     * Penanda administratif dalam kurung bukan bagian nama. Diperiksa di data dev: seluruh 132
     * nama berkurung isinya penanda ("(TIDAK DIPAKAI)", "(BATAL PRIKSA)", "(X)"), tidak satu pun
     * bagian nama sungguhan.
     */
    private static String buangPenandaKurung(String nama) {
        String s = (nama == null) ? "" : nama.trim();
        s = s.replaceAll("\\s*\\([^)]*\\)", " ");
        // Kurung buka tanpa penutup berarti penandanya terpotong lebar kolom nm_pasien
        // varchar(40), mis. "FAIRUZ ...,AN (TIDAK DIPA". Sisanya ikut dibuang.
        int buka = s.indexOf('(');
        if (buka >= 0) {
            s = s.substring(0, buka);
        }
        return s.replaceAll("\\s+", " ").trim();
    }

    /** Buang pemisah yang menggantung di ujung, mis. "ABDUL AZIS," sesudah penanda dibuang. */
    private static String rapikanTandaBaca(String nama) {
        return nama.replaceAll("^[\\s.,]+", "").replaceAll("[\\s.,]+$", "").trim();
    }

    static String bersihkanGelar(String nama) {
        String asli = (nama == null) ? "" : nama.trim();
        // Dirapikan SEBELUM dan sesudah tiap lapis: koma menggantung ("... , AN,") membuat
        // potongan sesudah koma terakhir jadi kosong sehingga sapaannya lolos tak terkupas.
        String hasil = rapikanTandaBaca(buangPenandaKurung(asli));
        for (int i = 0; i < 3; i++) {
            String sesudah = rapikanTandaBaca(kupasSapaan(hasil));
            if (sesudah.equals("") || sesudah.equals(hasil)) {
                break;
            }
            hasil = sesudah;
        }
        hasil = rapikanTandaBaca(hasil);
        return hasil.equals("") ? asli : hasil;
    }

    /**
     * true bila nama pasien sebenarnya menunjuk IBUNYA ("BY NY SITI"), bukan nama bayi itu sendiri.
     *
     * Bentuk ini tidak boleh dikirim: sesudah sapaan dikupas yang tersisa adalah nama ibu, dan
     * nama itu akan menempel pada NIK bayi di rekam nasional. Diukur di data dev — 3 dari 328 bayi
     * ber-NIK memakai bentuk ini, dan pada ketiganya `nm_ibu` memang sama persis dengan sisa nama.
     * Sisanya (325) sudah memakai nama bayi sungguhan dengan akhiran ", BY" dan aman dikirim.
     */
    static boolean namaMenunjukIbu(String namaAsli) {
        return normalNama(namaAsli).matches("^BY[\\s.,]+NY[\\s.,]+.*");
    }

    /** Satu lapis pengupasan; mengembalikan teks apa adanya bila tidak ada yang cocok. */
    private static String kupasSapaan(String nama) {
        int koma = nama.lastIndexOf(',');
        if (koma > 0 && sapaanMurni(nama.substring(koma + 1))) {
            return nama.substring(0, koma).trim();
        }
        int spasi = nama.lastIndexOf(' ');
        if (spasi > 0 && sapaanMurni(nama.substring(spasi + 1))) {
            return nama.substring(0, spasi).trim();
        }
        return nama.replaceFirst("(?i)^(NY|TN|NN|AN|SDR|SDRI|BY)\\.?[\\s,]+", "").trim();
    }

    /**
     * true bila potongan teks itu murni sapaan. Titik dan penanda administratif dalam kurung
     * ("NY (TIDAK DIPAKAI)") diabaikan supaya ikut terkupas bersama sapaannya.
     */
    private static boolean sapaanMurni(String bagian) {
        String s = normalNama(bagian).replace(".", "");
        s = s.replaceAll("\\s*\\([^)]*\\)\\s*", " ").trim();
        return SAPAAN.contains(s);
    }

    private void tutup(ResultSet r, PreparedStatement p) {
        try { if (r != null) { r.close(); } } catch (Exception e) { System.out.println("Notif : " + e); }
        try { if (p != null) { p.close(); } } catch (Exception e) { System.out.println("Notif : " + e); }
    }

    // ---------------------------------------------------------------- kode wilayah Kemendagri

    /**
     * Kode wilayah 4 tingkat versi Kemendagri. Wajib lengkap: SATUSEHAT menolak Patient yang
     * address.extension administrativeCode-nya tidak utuh.
     */
    static class KodeWilayah {
        String provinsi = "", kabupaten = "", kecamatan = "", kelurahan = "", gagalDi = "";

        boolean lengkap() {
            return !kelurahan.equals("");
        }
    }

    /**
     * Petakan nama wilayah pasien ke kode Kemendagri.
     *
     * Pencocokannya EXACT dan BERHIERARKI — kabupaten harus anak provinsi yang sudah cocok,
     * kecamatan anak kabupaten, dan seterusnya. Begitu satu tingkat tidak dikenali atau kembar,
     * proses berhenti dan Patient tidak jadi dibuat. Tidak ada penebakan dari potongan NIK dan
     * tidak ada "ambil kelurahan pertama": alamat karangan yang terlanjur masuk rekam medis
     * nasional tidak bisa kita hapus sendiri.
     *
     * Diukur pada 26.185 pasien (4 Agt 2026): 87,8% terpetakan penuh. Sisanya memang salah input
     * di SIMRS — typo ("DEMKA", "JAW TENGAH"), kolom kelurahan berisi nama kecamatan, atau
     * wilayah yang sudah dimekarkan ("RUMBAI PESISIR") — dan semua itu memang harus ditolak.
     */
    static KodeWilayah petakanWilayah(DataPasien d) {
        KodeWilayah w = new KodeWilayah();
        if (!siapkanIndeksWilayah()) {
            w.gagalDi = "berkas ./cache/*.iyem tidak terbaca";
            return w;
        }
        w.provinsi = kodeWilayah(idxPropinsi, "", new String[]{aliasPropinsi(d.namaProp)});
        if (w.provinsi.equals("")) {
            w.gagalDi = "propinsi '" + d.namaProp.trim() + "' tidak dikenali";
            return w;
        }
        // Nama kabupaten telanjang ("SEMARANG") ambigu: ada KABUPATEN dan KOTA dengan nama sama.
        // Karena itu tiap calon dicoba sampai kecamatannya ikut ketemu, bukan berhenti di calon
        // pertama. Tanpa runut-balik ini pasien Genuk/Tembalang/Pedurungan tertahan di Kabupaten
        // Semarang, dan kecamatan yang kebetulan ada di kedua wilayah bisa salah pilih diam-diam.
        for (String calon : kandidatKabupaten(d.namaKab)) {
            String kodeKab = kodeWilayah(idxKabupaten, w.provinsi, new String[]{calon});
            if (kodeKab.equals("")) {
                continue;
            }
            if (w.kabupaten.equals("")) {
                w.kabupaten = kodeKab;      // simpan calon pertama sebagai jawaban terbaik sementara
            }
            String kodeKec = kodeWilayah(idxKecamatan, kodeKab, new String[]{d.namaKec});
            if (!kodeKec.equals("")) {
                w.kabupaten = kodeKab;
                w.kecamatan = kodeKec;
                break;
            }
        }
        if (w.kabupaten.equals("")) {
            w.gagalDi = "kabupaten/kota '" + d.namaKab.trim() + "' tidak dikenali";
            return w;
        }
        if (w.kecamatan.equals("")) {
            w.gagalDi = "kecamatan '" + d.namaKec.trim() + "' tidak ada di bawah "
                    + d.namaKab.trim();
            return w;
        }
        w.kelurahan = kodeWilayah(idxKelurahan, w.kecamatan, new String[]{d.namaKel});
        if (w.kelurahan.equals("")) {
            w.gagalDi = "kelurahan/desa '" + d.namaKel.trim() + "' tidak ada di bawah kecamatan "
                    + d.namaKec.trim();
        }
        return w;
    }

    /** Penanda nama yang dipakai lebih dari satu kode di induk yang sama — sengaja ditolak. */
    private static final String WILAYAH_KEMBAR = " KEMBAR";
    private static Map<String, String> idxPropinsi, idxKabupaten, idxKecamatan, idxKelurahan;

    /**
     * Bangun indeks nama->kode sekali saja untuk seluruh aplikasi (kelurahan.iyem ~6 MB, terlalu
     * mahal diurai tiap pemanggilan). Bila salah satu berkas gagal dibaca, indeks dibiarkan kosong
     * supaya percobaan berikutnya bisa memulihkan diri setelah berkas cache tersedia.
     */
    private static synchronized boolean siapkanIndeksWilayah() {
        if (idxKelurahan != null) {
            return true;
        }
        try {
            Map<String, String> prop = muatWilayah("propinsi.iyem", "propinsi", null);
            Map<String, String> kab = muatWilayah("kabupaten.iyem", "kabupaten", "id_propinsi");
            Map<String, String> kec = muatWilayah("kecamatan.iyem", "kecamatan", "id_kabupaten");
            Map<String, String> kel = muatWilayah("kelurahan.iyem", "kelurahan", "id_kecamatan");
            idxPropinsi = prop;
            idxKabupaten = kab;
            idxKecamatan = kec;
            idxKelurahan = kel;
            return true;
        } catch (Exception e) {
            System.out.println("Notifikasi indeks wilayah : " + e);
            return false;
        }
    }

    /**
     * Muat satu berkas wilayah jadi peta pencarian. Tiap baris didaftarkan dua kali: kunci "E|"
     * untuk nama apa adanya dan "R|" untuk nama tanpa spasi — yang kedua menjembatani beda penulisan
     * seperti "GUNUNG PATI" lawan "GUNUNGPATI". Keduanya memakai ruang kunci terpisah supaya
     * kecocokan longgar tidak pernah mengalahkan kecocokan persis.
     */
    private static Map<String, String> muatWilayah(String berkas, String akar, String kolomInduk) throws Exception {
        Map<String, String> peta = new HashMap<>();
        JsonNode daftar;
        FileReader fr = new FileReader("./cache/" + berkas);
        try {
            daftar = new ObjectMapper().readTree(fr).path(akar);
        } finally {
            fr.close();
        }
        for (JsonNode baris : daftar) {
            String id = baris.path("id").asText().trim();
            String nama = normalNama(baris.path("nama").asText());
            if (id.equals("") || nama.equals("")) {
                continue;
            }
            String induk = (kolomInduk == null) ? "" : baris.path(kolomInduk).asText().trim();
            taruhWilayah(peta, "E|" + induk + "|" + nama, id);
            taruhWilayah(peta, "R|" + induk + "|" + nama.replace(" ", ""), id);
        }
        return peta;
    }

    private static void taruhWilayah(Map<String, String> peta, String kunci, String id) {
        String lama = peta.get(kunci);
        if (lama == null) {
            peta.put(kunci, id);
        } else if (!lama.equals(id)) {
            peta.put(kunci, WILAYAH_KEMBAR);
        }
    }

    /** Kode wilayah pertama yang cocok persis; "" bila tidak ada atau namanya kembar. */
    private static String kodeWilayah(Map<String, String> peta, String induk, String[] kandidat) {
        for (String c : kandidat) {
            String v = peta.get("E|" + induk + "|" + normalNama(c));
            if (v != null && !v.equals(WILAYAH_KEMBAR)) {
                return v;
            }
        }
        for (String c : kandidat) {
            String v = peta.get("R|" + induk + "|" + normalNama(c).replace(" ", ""));
            if (v != null && !v.equals(WILAYAH_KEMBAR)) {
                return v;
            }
        }
        return "";
    }

    static String normalNama(String s) {
        return (s == null) ? "" : s.trim().toUpperCase().replaceAll("\\s+", " ");
    }

    /** Nama propinsi versi SIMRS yang berbeda penulisan dengan daftar Kemendagri. */
    static String aliasPropinsi(String nama) {
        String n = normalNama(nama);
        if (n.equals("DKI JAKARTA") || n.equals("DKI") || n.equals("JAKARTA")) {
            return "DAERAH KHUSUS IBUKOTA JAKARTA";
        }
        if (n.equals("DI YOGYAKARTA") || n.equals("D.I. YOGYAKARTA") || n.equals("DIY")
                || n.equals("YOGYAKARTA")) {
            return "DAERAH ISTIMEWA YOGYAKARTA";
        }
        return n;
    }

    /**
     * Kemendagri selalu menulis kabupaten/kota berawalan "KABUPATEN "/"KOTA " (DKI memakai
     * "KOTA ADMINISTRASI "), sedangkan SIMRS menyimpan nama telanjang ("DEMAK") atau singkatan
     * ("KAB. DEMAK"). Nama telanjang diartikan kabupaten lebih dulu, sebab kota di SIMRS selalu
     * ditulis dengan awalan "KOTA".
     */
    static String[] kandidatKabupaten(String nama) {
        String n = normalNama(nama).replaceFirst("^KAB\\.?\\s+", "KABUPATEN ");
        if (n.startsWith("KABUPATEN ")) {
            String inti = n.substring("KABUPATEN ".length());
            return new String[]{n, "KABUPATEN ADMINISTRASI " + inti};
        }
        if (n.startsWith("KOTA ")) {
            String inti = n.substring("KOTA ".length());
            return new String[]{n, "KOTA ADMINISTRASI " + inti};
        }
        return new String[]{"KABUPATEN " + n, "KOTA " + n, "KABUPATEN ADMINISTRASI " + n,
                            "KOTA ADMINISTRASI " + n, n};
    }

    // ---------------------------------------------------------------- payload & kiriman

    /**
     * Rakit resource Patient. Dibentuk lewat ObjectMapper, bukan sambung string, supaya nama atau
     * alamat yang memuat kutip/backslash/baris baru tidak merusak struktur JSON-nya.
     */
    static ObjectNode bodyPatient(ObjectMapper mapper, String nik, DataPasien d, KodeWilayah w) {
        ObjectNode p = mapper.createObjectNode();
        p.put("resourceType", "Patient");

        ObjectNode ident = p.putArray("identifier").addObject();
        ident.put("use", "usual");
        ident.put("system", "https://fhir.kemkes.go.id/id/nik");
        ident.put("value", nik);

        ObjectNode nama = p.putArray("name").addObject();
        nama.put("use", "official");
        nama.put("text", d.nama);
        String[] bagian = d.nama.split("\\s+", 2);
        nama.putArray("given").add(bagian[0]);
        nama.put("family", bagian.length > 1 ? bagian[1] : bagian[0]);

        // telecom hanya diisi bila datanya benar-benar ada; "-" di SIMRS berarti kosong.
        com.fasterxml.jackson.databind.node.ArrayNode telecom = p.putArray("telecom");
        if (adaIsi(d.telepon)) {
            ObjectNode t = telecom.addObject();
            t.put("system", "phone");
            t.put("value", d.telepon);
            t.put("use", "mobile");
        }
        if (adaIsi(d.email)) {
            ObjectNode t = telecom.addObject();
            t.put("system", "email");
            t.put("value", d.email);
            t.put("use", "home");
        }
        if (telecom.size() == 0) {
            p.remove("telecom");
        }

        p.put("gender", d.jk.equals("P") ? "female" : "male");
        p.put("birthDate", d.tglLahir);

        ObjectNode alamat = p.putArray("address").addObject();
        alamat.put("use", "home");
        alamat.putArray("line").add(d.alamat);
        alamat.put("city", d.namaKab.trim());
        alamat.put("country", "ID");
        ObjectNode ext = alamat.putArray("extension").addObject();
        ext.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/administrativeCode");
        com.fasterxml.jackson.databind.node.ArrayNode kode = ext.putArray("extension");
        tambahKode(kode, "province", w.provinsi);
        tambahKode(kode, "city", w.kabupaten);
        tambahKode(kode, "district", w.kecamatan);
        tambahKode(kode, "village", w.kelurahan);

        ObjectNode kawin = p.putObject("maritalStatus").putArray("coding").addObject();
        kawin.put("system", "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");
        kawin.put("code", kodeStatusNikah(d.sttsNikah));

        ObjectNode wni = p.putArray("extension").addObject();
        wni.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/citizenshipStatus");
        wni.put("valueCode", "WNI");

        p.put("multipleBirthInteger", 0);

        ObjectNode bahasa = p.putArray("communication").addObject();
        ObjectNode coding = bahasa.putObject("language").putArray("coding").addObject();
        coding.put("system", "urn:ietf:bcp:47");
        coding.put("code", "id-ID");
        bahasa.put("preferred", true);

        return p;
    }

    private static void tambahKode(com.fasterxml.jackson.databind.node.ArrayNode wadah,
                                   String url, String kode) {
        ObjectNode n = wadah.addObject();
        n.put("url", url);
        n.put("valueCode", kode);
    }

    private static boolean adaIsi(String s) {
        return s != null && !s.trim().equals("") && !s.trim().equals("-");
    }

    /**
     * enum stts_nikah SIMRS -> HL7 v3 MaritalStatus. JANDA dan DUDHA dipetakan ke W (Widowed),
     * bukan D — di HL7 huruf D berarti Divorced, arti yang berbeda dan tidak bisa kita pastikan
     * dari data SIMRS.
     */
    static String kodeStatusNikah(String stts) {
        String s = (stts == null) ? "" : stts.trim().toUpperCase();
        if (s.equals("MENIKAH")) {
            return "M";
        }
        if (s.equals("JANDA") || s.equals("DUDHA") || s.equals("DUDA")) {
            return "W";
        }
        return "S";
    }

    /** POST Patient. Body dikirim sebagai byte UTF-8: Spring versi lama memakai ISO-8859-1
     *  untuk String, yang merusak nama ber-aksen jadi tanda tanya. */
    private String postPatient(ObjectNode body) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        HttpEntity<byte[]> req = new HttpEntity<byte[]>(
                mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8), h);
        System.out.println("URL : " + link + "/Patient");
        System.out.println("Request JSON : " + mapper.writeValueAsString(body));
        ResponseEntity<String> respon = api.getRest()
                .exchange(link + "/Patient", HttpMethod.POST, req, String.class);
        System.out.println("Result JSON : " + respon.getBody());
        return mapper.readTree(respon.getBody()).path("id").asText();
    }

    /**
     * IHS number pasien berdasarkan NIK; "" bila tidak ketemu/NIK tidak valid/gagal.
     * Bila pasien belum terdaftar di SATUSEHAT, ia didaftarkan dulu — lihat
     * {@link #cekIHSAtauBuat(String)} untuk syarat kapan pendaftaran boleh berjalan.
     */
    public String tampilIDPasien(String cari) {
        idpasien = cekIHSAtauBuat(cari).ihs;
        return idpasien;
    }


    public String tampilIDParktisi(String cari) {
        idpasien = "";
        cari = (cari == null) ? "" : cari.trim();

        if (Sequel.cariInteger("select count(nikpegawai) from satu_sehat_ihs_practitioner where nikpegawai='" + cari + "'") > 0) {
            idpasien = Sequel.cariIsi("select ihspegawai from satu_sehat_ihs_practitioner where nikpegawai='" + cari + "'");
        } else if (!cari.matches("\\d{16}")) {
            // NIK wajib 16 digit numerik; SATUSEHAT selalu balas 400 untuk NIK malformed -> lewati panggilan API (hindari 400 sia-sia di log).
            System.out.println("Notifikasi CekNIK : NIK tidak valid (bukan 16 digit), lewati lookup Practitioner. nik=" + cari);
        } else {
            try{
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                requestEntity = new HttpEntity(headers);
                System.out.println("URL : "+link+"/Practitioner?identifier=https://fhir.kemkes.go.id/id/nik|"+cari);
                json=api.getRest().exchange(link+"/Practitioner?identifier=https://fhir.kemkes.go.id/id/nik|"+cari, HttpMethod.GET, requestEntity, String.class).getBody();
                System.out.println("JSON : "+json);
                root = mapper.readTree(json);
                response = root.path("entry");
                for(JsonNode list:response){
                   idpasien=list.path("resource").path("id").asText();
                   if(!idpasien.equals("")){
                       Sequel.menyimpan("satu_sehat_ihs_practitioner","?,?","IHS Pasien",2,new String[]{
                           cari,idpasien
                       });
                   }
                }
            }catch(Exception e){
                idpasien="";
                System.out.println("Notifikasi : "+e);
            }
        }
        return idpasien;
    }

    public String getImagingStudyID(String noOrder) {
        String idimagingstudy = "";
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+link+"/ImagingStudy?identifier=http://sys-ids.kemkes.go.id/acsn/"+koneksiDB.IDSATUSEHAT()+"|"+noOrder);
            json=api.getRest().exchange(link+"/ImagingStudy?identifier=http://sys-ids.kemkes.go.id/acsn/"+koneksiDB.IDSATUSEHAT()+"|"+noOrder, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("JSON : "+json);
            root = mapper.readTree(json);
            response = root.path("entry");
            for(JsonNode list:response){
               idimagingstudy=list.path("resource").path("id").asText();
               if(!idimagingstudy.equals("")){
                   break;
               }
            }

            if(idimagingstudy.equals("")){
                System.out.println("URL : "+link+"/ImagingStudy?identifier=http://sys-ids.kemkes.go.id/imagingstudy/"+koneksiDB.IDSATUSEHAT()+"|"+noOrder);
                json=api.getRest().exchange(link+"/ImagingStudy?identifier=http://sys-ids.kemkes.go.id/imagingstudy/"+koneksiDB.IDSATUSEHAT()+"|"+noOrder, HttpMethod.GET, requestEntity, String.class).getBody();
                System.out.println("JSON : "+json);
                root = mapper.readTree(json);
                response = root.path("entry");
                for(JsonNode list:response){
                   idimagingstudy=list.path("resource").path("id").asText();
                   if(!idimagingstudy.equals("")){
                       break;
                   }
                }
            }
        }catch(Exception e){
            idimagingstudy="";
            System.out.println("Notifikasi : "+e);
        }
        return idimagingstudy;
    }
    
    /**
     * Cari Patient di SATUSEHAT berdasarkan NIK, mengembalikan Bundle searchset APA ADANYA.
     * Bundle kosong berarti pasien tidak terdaftar; MELEMPAR exception bila panggilannya sendiri
     * gagal (token/jaringan/HTTP error). Perbedaan itu wajib dipertahankan: "tidak terdaftar" boleh
     * menghapus cache lokal dan boleh memicu pembuatan Patient baru, "gagal menghubungi server"
     * tidak boleh dua-duanya.
     *
     * Dipakai untuk memvalidasi cache satu_sehat_ihs_patient: pencarian by NIK adalah satu-satunya
     * cara yang sahih untuk memastikan sebuah id benar milik NIK tersebut, karena NIK di dalam
     * resource Patient dikembalikan bertopeng.
     */
    private JsonNode bundlePatientByNik(String nik) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        HttpEntity<String> req = new HttpEntity<String>(h);
        String url = link + "/Patient?identifier=https://fhir.kemkes.go.id/id/nik|" + nik;
        System.out.println("URL : " + url);
        String hasil = api.getRest().exchange(url, HttpMethod.GET, req, String.class).getBody();
        return mapper.readTree(hasil);
    }

    /** true bila Bundle hasil pencarian memuat minimal satu resource Patient ber-id. */
    static boolean adaEntryPatient(JsonNode bundle) {
        for (JsonNode list : bundle.path("entry")) {
            if (!list.path("resource").path("id").asText().equals("")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pilih resource Patient dari Bundle searchset: HANYA entry yang benar-benar membawa
     * identifier ihs-number. Entry pertama belum tentu Patient definitif — bisa Patient
     * provisional yang tidak punya ihs-number, dan itulah yang dulu mencemari cache NIK->IHS.
     * Mengembalikan null bila tidak ada entry yang memenuhi syarat.
     */
    static JsonNode pilihPatientBerIhs(JsonNode bundle) {
        for (JsonNode list : bundle.path("entry")) {
            JsonNode resource = list.path("resource");
            if (resource.path("id").asText().equals("")) {
                continue;
            }
            for (JsonNode ident : resource.path("identifier")) {
                if (ident.path("system").asText().endsWith("/ihs-number")
                        && !ident.path("value").asText().equals("")) {
                    return resource;
                }
            }
        }
        return null;
    }
}
