package com.citamedica.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.availability")
public class AvailabilityProperties {

    /**
     * When true, internal bookings must match an AVAILABLE pre-generated slot (and concurrency rules).
     * Cal.com bookings (calBookingId set) skip slot acquisition.
     */
    private boolean enforced = true;

    @NestedConfigurationProperty
    private final SlotGeneration slotGeneration = new SlotGeneration();

    @NestedConfigurationProperty
    private final Sync sync = new Sync();

    public boolean isEnforced() {
        return enforced;
    }

    public void setEnforced(boolean enforced) {
        this.enforced = enforced;
    }

    public SlotGeneration getSlotGeneration() {
        return slotGeneration;
    }

    public Sync getSync() {
        return sync;
    }

    public static class SlotGeneration {
        /** Spring cron for nightly slot generation (default 03:00 server time). */
        private String cron = "0 0 3 * * *";
        private int horizonDays = 90;
        private int batchSize = 100;
        private long maxDurationAlertMs = 300_000L;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public int getHorizonDays() {
            return horizonDays;
        }

        public void setHorizonDays(int horizonDays) {
            this.horizonDays = horizonDays;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public long getMaxDurationAlertMs() {
            return maxDurationAlertMs;
        }

        public void setMaxDurationAlertMs(long maxDurationAlertMs) {
            this.maxDurationAlertMs = maxDurationAlertMs;
        }
    }

    public static class Sync {
        private long fixedDelayMs = 21_600_000L;
        private boolean enabled = true;

        public long getFixedDelayMs() {
            return fixedDelayMs;
        }

        public void setFixedDelayMs(long fixedDelayMs) {
            this.fixedDelayMs = fixedDelayMs;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
