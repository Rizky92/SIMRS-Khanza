package smc.events;

@FunctionalInterface
public interface SimpanListener {
    public void onSimpan(SimpanEvent event);
}
