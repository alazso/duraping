package sh.redkey.mc.duraping.util;

public class AlertState {
    public int lastBucket = 0;           // 0 none, 1 warn, 2 danger, 3 critical
    public int lastPct = 100;            // last observed percentage
    public long lastAlertAt = 0L;        // last sound/toast/flash
    public boolean armed = true;         // can fire again on next downward crossing
}

