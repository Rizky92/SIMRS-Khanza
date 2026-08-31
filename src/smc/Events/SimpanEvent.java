package smc.events;

import java.util.Map;

public class SimpanEvent {
    private final Status status;
    private final Map properties;
    private final Exception thrownException;

    public SimpanEvent(Map properties, Exception thrownException) {
        this.properties = properties;
        this.thrownException = thrownException;
        this.status = Status.Gagal;
    }

    public SimpanEvent(Map properties) {
        this.properties = properties;
        this.thrownException = null;
        this.status = Status.Berhasil;
    }

    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public Exception getThrownException() {
        return thrownException;
    }

    public boolean berhasilDisimpan() {
        return this.status == Status.Berhasil;
    }

    public boolean gagalDisimpan() {
        return this.status == Status.Gagal;
    }

    static enum Status {
        Berhasil,
        Gagal
    };
}
