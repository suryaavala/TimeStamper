package com.sardox.timestamper.types;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class JetUUID implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long high;
    private final long low;


    public static final JetUUID Zero = new JetUUID();


    public static JetUUID randomUUID() {
        return fromUUID(UUID.randomUUID());
    }

    public static JetUUID fromUUID(UUID uuid) {
        if (uuid == null)
            return Zero;
        return new JetUUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static JetUUID fromString(String str) {
        if (str == null || "".equals(str)) {
            return Zero;
        }
        return fromUUID(UUID.fromString(str));
    }

    public static JetUUID fromLongs(long high, long low) {
        return fromUUID(new UUID(high, low));
    }

    private JetUUID(long high, long low) {
        this.high = high;
        this.low = low;
    }


    public JetUUID() {
        this.high = 0L;
        this.low = 0L;
    }

    public boolean isZero() {
        return this.high == 0L && this.low == 0L;
    }

    public UUID toSqlUuid() {
        if (isZero())
            return null;
        return toUuid();
    }

    public UUID toUuid() {
        return new UUID(high, low);
    }

    /**
     * Ex. 2864bd4d-6e29-4cbe-9662-db423cc31df5
     */
    @Override
    public String toString() {
        return toUuid().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JetUUID jetUUID = (JetUUID) o;

        if (high != jetUUID.high) return false;
        return low == jetUUID.low;
    }

    @Override
    public int hashCode() {
        int result = (int) (high ^ (high >>> 32));
        result = 31 * result + (int) (low ^ (low >>> 32));
        return result;
    }

    public static JetUUID fromBytes(byte[] bytes) {
        if (bytes == null) {
            return Zero;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return fromLongs(high, low);
    }

    public byte[] toBytes() {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(high);
        bb.putLong(low);
        return bb.array();
    }
}
