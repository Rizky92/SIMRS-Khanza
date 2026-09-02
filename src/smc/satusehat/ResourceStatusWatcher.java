package smc.satusehat;

public interface ResourceStatusWatcher {
    boolean isProcessStopped();

    void retryUntil(int retry, long seconds);
}
