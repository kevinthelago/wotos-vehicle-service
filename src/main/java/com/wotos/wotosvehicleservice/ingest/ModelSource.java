package com.wotos.wotosvehicleservice.ingest;

/** Fetches a tank's {@code .glb} model bytes from its community source. */
public interface ModelSource {

    byte[] fetchModel(Tier1Tank tank);
}
