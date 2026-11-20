package bridging;

import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Ole32Util;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import widget.LoadingOverlaySMC;

/**
 * Membaca isi jendela aplikasi lain melalui UI Automation Windows, untuk aplikasi yang kontrolnya tidak berupa jendela Windows bawaan (misalnya
 * WPF). Dipakai {@link AplikasiBiometrikSMC} untuk mengenali form login aplikasi sidik jari, kolom yang sedang aktif, dan isi kolom tersebut.
 * <p>
 * Seluruh pemanggilan COM berjalan di satu thread khusus (MTA). Bila UI Automation tidak tersedia, setiap pembacaan mengembalikan {@code null}
 * dan pemanggil kembali memakai jeda waktu.
 */
public final class PembacaUIASMC {
    public static final int STATUS_LOGIN = 1;
    public static final int STATUS_SIAP = 2;

    private static final String CLSID_CUIAUTOMATION = "{ff48dba4-60ef-4201-aa87-54103eef594e}";
    private static final String IID_IUIAUTOMATION = "{30cbe57d-d9d0-452a-ab13-7ac5ac4825ee}";
    private static final int CLSCTX_INPROC_SERVER = 1;
    private static final int COINIT_MULTITHREADED = 0;
    private static final int TREESCOPE_DESCENDANTS = 4;
    private static final int UIA_CONTROLTYPE = 30003;
    private static final int UIA_ISENABLED = 30010;
    private static final int UIA_ISPASSWORD = 30019;
    private static final int UIA_ISOFFSCREEN = 30022;
    private static final int UIA_VALUEVALUE = 30045;
    private static final int UIA_EDIT = 50004;
    private static final short VT_BOOL = 11;
    private static final short VT_I4 = 3;
    private static final short VT_BSTR = 8;

    private static final int RELEASE = 2;
    private static final int AUTOMATION_ELEMENTFROMHANDLE = 6;
    private static final int AUTOMATION_GETFOCUSEDELEMENT = 8;
    private static final int AUTOMATION_CREATEPROPERTYCONDITION = 23;
    private static final int AUTOMATION_CREATEANDCONDITION = 25;
    private static final int ELEMENT_FINDFIRST = 5;
    private static final int ELEMENT_GETCURRENTPROPERTYVALUE = 10;
    private static final int ELEMENT_PROCESSID = 20;
    private static final int ELEMENT_CONTROLTYPE = 21;
    private static final int ELEMENT_NAME = 23;
    private static final int ELEMENT_AUTOMATIONID = 29;
    private static final int ELEMENT_CLASSNAME = 30;
    private static final int ELEMENT_ISPASSWORD = 35;

    private static final ExecutorService THREAD = Executors.newSingleThreadExecutor(tugas -> {
        Thread thread = new Thread(tugas, "PembacaUIASMC");
        thread.setDaemon(true);
        return thread;
    });

    private static boolean siap = false;
    private static boolean gagal = false;
    private static Pointer automation, kondisiSandi, kondisiKolom;

    /**
     * Kolom yang sedang menerima ketikan di aplikasi lain.
     */
    public static final class Fokus {
        public final int pid;
        public final boolean teks;
        public final boolean sandi;
        public final String kunci;

        private Fokus(int pid, boolean teks, boolean sandi, String kunci) {
            this.pid = pid;
            this.teks = teks;
            this.sandi = sandi;
            this.kunci = kunci;
        }
    }

    public static class Varian extends Structure implements Structure.ByValue {
        public short vt;
        public short cadangan1;
        public short cadangan2;
        public short cadangan3;
        public long nilai;
        public long cadangan4;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("vt", "cadangan1", "cadangan2", "cadangan3", "nilai", "cadangan4");
        }
    }

    private interface OleAutoSMC extends StdCallLibrary {
        OleAutoSMC INSTANCE = (OleAutoSMC) Native.loadLibrary("oleaut32", OleAutoSMC.class, W32APIOptions.DEFAULT_OPTIONS);

        void SysFreeString(Pointer bstr);

        int VariantClear(Pointer varian);
    }

    private PembacaUIASMC() {
    }

    /**
     * @param jendela jendela aplikasi
     * @return {@link #STATUS_LOGIN} bila kolom kata sandi tampil, {@link #STATUS_SIAP} bila ada kolom isian aktif tanpa kolom kata sandi, atau
     * {@code null} bila tidak ada kolom isian yang terbaca
     */
    public static Integer status(WinDef.HWND jendela) {
        return jalankan(() -> {
            Pointer elemen = elemenDari(jendela);
            if (elemen == null) {
                return null;
            }
            try {
                if (cariPertama(elemen, kondisiSandi)) {
                    return STATUS_LOGIN;
                }
                return cariPertama(elemen, kondisiKolom) ? STATUS_SIAP : null;
            } finally {
                lepas(elemen);
            }
        });
    }

    /**
     * @return kolom yang sedang aktif di seluruh desktop, atau {@code null} bila tidak dapat dibaca
     */
    public static Fokus fokus() {
        return jalankan(() -> {
            PointerByReference hasil = new PointerByReference();
            if (panggil(automation, AUTOMATION_GETFOCUSEDELEMENT, hasil) != 0 || hasil.getValue() == null) {
                return null;
            }
            Pointer elemen = hasil.getValue();
            try {
                IntByReference pid = new IntByReference();
                IntByReference tipe = new IntByReference();
                IntByReference sandi = new IntByReference();
                panggil(elemen, ELEMENT_PROCESSID, pid);
                panggil(elemen, ELEMENT_CONTROLTYPE, tipe);
                panggil(elemen, ELEMENT_ISPASSWORD, sandi);
                String kunci = teks(elemen, ELEMENT_AUTOMATIONID) + "|" + teks(elemen, ELEMENT_CLASSNAME) + "|" + teks(elemen, ELEMENT_NAME);
                return new Fokus(pid.getValue(), UIA_EDIT == tipe.getValue(), sandi.getValue() != 0, kunci);
            } finally {
                lepas(elemen);
            }
        });
    }

    /**
     * @return isi kolom yang sedang aktif, atau {@code null} bila tidak dapat dibaca
     */
    public static String isiFokus() {
        return jalankan(() -> {
            PointerByReference hasil = new PointerByReference();
            if (panggil(automation, AUTOMATION_GETFOCUSEDELEMENT, hasil) != 0 || hasil.getValue() == null) {
                return null;
            }
            Pointer elemen = hasil.getValue();
            Memory varian = new Memory(24);
            varian.clear();
            try {
                if (panggil(elemen, ELEMENT_GETCURRENTPROPERTYVALUE, UIA_VALUEVALUE, varian) != 0 || VT_BSTR != varian.getShort(0)) {
                    return null;
                }
                Pointer bstr = varian.getPointer(8);
                return bstr == null ? "" : bstr.getWideString(0);
            } finally {
                OleAutoSMC.INSTANCE.VariantClear(varian);
                lepas(elemen);
            }
        });
    }

    private interface Tugas<T> {
        T jalankan() throws Exception;
    }

    private static <T> T jalankan(Tugas<T> tugas) {
        if (gagal || Native.POINTER_SIZE != 8) {
            return null;
        }
        try {
            return LoadingOverlaySMC.background(() -> THREAD.submit(() -> {
                if (!siap) {
                    siapkan();
                }
                return tugas.jalankan();
            }).get());
        } catch (Exception e) {
            System.out.println("Notif : UI Automation tidak dapat dipakai, " + e);
            gagal = true;
            return null;
        }
    }

    private static void siapkan() {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, COINIT_MULTITHREADED);
        PointerByReference hasil = new PointerByReference();
        int hr = Ole32.INSTANCE.CoCreateInstance(Ole32Util.getGUIDFromString(CLSID_CUIAUTOMATION), Pointer.NULL, CLSCTX_INPROC_SERVER, Ole32Util.getGUIDFromString(IID_IUIAUTOMATION), hasil).intValue();
        if (hr != 0) {
            throw new IllegalStateException("CoCreateInstance CUIAutomation gagal, HRESULT " + Integer.toHexString(hr));
        }
        automation = hasil.getValue();

        Pointer tampil = kondisi(UIA_ISOFFSCREEN, VT_BOOL, 0);
        kondisiSandi = gabung(kondisi(UIA_ISPASSWORD, VT_BOOL, 0xFFFF), tampil);
        kondisiKolom = gabung(gabung(kondisi(UIA_CONTROLTYPE, VT_I4, UIA_EDIT), kondisi(UIA_ISENABLED, VT_BOOL, 0xFFFF)), kondisi(UIA_ISOFFSCREEN, VT_BOOL, 0));
        siap = true;
    }

    private static Pointer kondisi(int properti, short tipe, long nilai) {
        Varian varian = new Varian();
        varian.vt = tipe;
        varian.nilai = nilai;
        PointerByReference hasil = new PointerByReference();
        int hr = panggil(automation, AUTOMATION_CREATEPROPERTYCONDITION, properti, varian, hasil);
        if (hr != 0) {
            throw new IllegalStateException("CreatePropertyCondition gagal, HRESULT " + Integer.toHexString(hr));
        }
        return hasil.getValue();
    }

    private static Pointer gabung(Pointer a, Pointer b) {
        PointerByReference hasil = new PointerByReference();
        int hr = panggil(automation, AUTOMATION_CREATEANDCONDITION, a, b, hasil);
        if (hr != 0) {
            throw new IllegalStateException("CreateAndCondition gagal, HRESULT " + Integer.toHexString(hr));
        }
        return hasil.getValue();
    }

    private static Pointer elemenDari(WinDef.HWND jendela) {
        PointerByReference hasil = new PointerByReference();
        if (panggil(automation, AUTOMATION_ELEMENTFROMHANDLE, jendela.getPointer(), hasil) != 0) {
            return null;
        }
        return hasil.getValue();
    }

    private static boolean cariPertama(Pointer elemen, Pointer kondisi) {
        PointerByReference hasil = new PointerByReference();
        if (panggil(elemen, ELEMENT_FINDFIRST, TREESCOPE_DESCENDANTS, kondisi, hasil) != 0 || hasil.getValue() == null) {
            return false;
        }
        lepas(hasil.getValue());
        return true;
    }

    private static String teks(Pointer elemen, int indeks) {
        PointerByReference hasil = new PointerByReference();
        if (panggil(elemen, indeks, hasil) != 0 || hasil.getValue() == null) {
            return "";
        }
        try {
            return hasil.getValue().getWideString(0);
        } finally {
            OleAutoSMC.INSTANCE.SysFreeString(hasil.getValue());
        }
    }

    private static void lepas(Pointer objek) {
        if (objek != null) {
            panggil(objek, RELEASE);
        }
    }

    private static int panggil(Pointer objek, int indeks, Object... argumen) {
        Pointer fungsi = objek.getPointer(0).getPointer((long) indeks * Native.POINTER_SIZE);
        Object[] semua = new Object[argumen.length + 1];
        semua[0] = objek;
        System.arraycopy(argumen, 0, semua, 1, argumen.length);
        return Function.getFunction(fungsi, Function.ALT_CONVENTION).invokeInt(semua);
    }
}
