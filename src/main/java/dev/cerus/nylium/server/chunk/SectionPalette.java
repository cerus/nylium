package dev.cerus.nylium.server.chunk;

import java.util.LinkedHashMap;
import java.util.Map;

public class SectionPalette {

    private final Map<Integer, Integer> localToGlobal = new LinkedHashMap<>();
    private final Map<Integer, Integer> globalToLocal = new LinkedHashMap<>();

    public SectionPalette() {
        // Map air
        this.map(0, 0);
    }

    public int bitsPerBlock() {
        return (int) Math.ceil(Math.log(this.localToGlobal.keySet().stream()
                .mapToInt(value -> value)
                .max()
                .orElse(0)) / Math.log(2));
    }

    public int nextFreeLocal(final int max) {
        for (int i = 0; i < max; i++) {
            if (this.localToGlobal.containsKey(i)) {
                continue;
            }
            return i;
        }
        throw new IllegalStateException();
    }

    public boolean contains(final int global) {
        return this.globalToLocal.containsKey(global);
    }

    public void map(final int local, final int global) {
        this.localToGlobal.put(local, global);
        this.globalToLocal.put(global, local);
    }

    public void unmapG(final int global) {
        this.localToGlobal.remove(this.globalToLocal.remove(global));
    }

    public void unmapL(final int local) {
        this.globalToLocal.remove(this.localToGlobal.remove(local));
    }

    public int localOrMap(final int global, final int local) {
        if (!this.globalToLocal.containsKey(global)) {
            this.map(local, global);
        }
        return this.local(global);
    }

    public int local(final int global) {
        return this.globalToLocal.get(global);
    }

    public int globalOrMap(final int local, final int global) {
        if (!this.localToGlobal.containsKey(local)) {
            this.map(local, global);
        }
        return this.global(local);
    }

    public int global(final int local) {
        return this.localToGlobal.get(local);
    }

    public Map<Integer, Integer> getGlobalToLocal() {
        return this.globalToLocal;
    }

    public Map<Integer, Integer> getLocalToGlobal() {
        return this.localToGlobal;
    }

}
