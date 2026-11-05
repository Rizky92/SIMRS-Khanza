package fungsi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import widget.LoadingOverlaySMC;

/**
 * Membungkus koneksi database supaya setiap eksekusi query yang dipanggil di dalam {@link LoadingOverlaySMC#run} atau
 * {@link LoadingOverlaySMC#call} dijalankan di latar belakang melalui {@link LoadingOverlaySMC#background}. Selama query berjalan, layar tetap
 * digambar ulang dan animasi loading tetap berputar. Di luar cakupan tersebut, query tetap dijalankan langsung seperti biasa.
 * <p>
 * Hanya pemanggilan {@code execute*} yang dipindahkan. Hasil query sudah dibaca seluruhnya oleh driver saat eksekusi, sehingga membaca
 * {@code ResultSet} tetap dilakukan di tempat.
 */
public final class KoneksiLatarBelakangSMC {
    private KoneksiLatarBelakangSMC() {
    }

    public static Connection bungkus(Connection koneksi) {
        if (koneksi == null || Proxy.isProxyClass(koneksi.getClass())) {
            return koneksi;
        }

        return (Connection) Proxy.newProxyInstance(KoneksiLatarBelakangSMC.class.getClassLoader(), new Class<?>[] {Connection.class}, (proxy, method, args) -> {
            Object hasil = panggil(koneksi, method, args);
            if (hasil instanceof Statement) {
                return bungkusStatement((Statement) hasil, method.getReturnType());
            }
            return hasil;
        });
    }

    private static Statement bungkusStatement(Statement statement, Class<?> tipe) {
        Class<?> antarmuka = Statement.class;
        if (CallableStatement.class.isAssignableFrom(tipe)) {
            antarmuka = CallableStatement.class;
        } else if (PreparedStatement.class.isAssignableFrom(tipe)) {
            antarmuka = PreparedStatement.class;
        }

        return (Statement) Proxy.newProxyInstance(KoneksiLatarBelakangSMC.class.getClassLoader(), new Class<?>[] {antarmuka}, (proxy, method, args) -> {
            if (!method.getName().startsWith("execute")) {
                return panggil(statement, method, args);
            }

            return LoadingOverlaySMC.background(() -> panggil(statement, method, args));
        });
    }

    private static Object panggil(Object target, Method method, Object[] args) throws Exception {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable penyebab = e.getCause();
            if (penyebab instanceof Exception) {
                throw (Exception) penyebab;
            } else if (penyebab instanceof Error) {
                throw (Error) penyebab;
            }
            throw e;
        }
    }
}
