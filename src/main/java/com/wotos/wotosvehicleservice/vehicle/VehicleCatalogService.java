package com.wotos.wotosvehicleservice.vehicle;

import com.wotos.wotosvehicleservice.client.wot.WotTankopediaFeignClient;
import com.wotos.wotosvehicleservice.client.wot.vehicle.WotVehicle;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static com.wotos.wotosvehicleservice.config.Settings.WG_APP_ID;

/**
 * Thin read/write layer over the local vehicle mirror. Reads are read-through:
 * a row present locally wins; a miss falls back to the WoT Tankopedia API so the
 * service keeps serving even for vehicles that have not been ingested yet. The
 * fallback result is mapped but not persisted — population is the ingestion job's
 * job (G5), not a side effect of a GET.
 */
@Service
public class VehicleCatalogService {

    private final VehicleRepository vehicleRepository;
    private final WotTankopediaFeignClient wotTankopediaFeignClient;

    public VehicleCatalogService(VehicleRepository vehicleRepository,
                                 WotTankopediaFeignClient wotTankopediaFeignClient) {
        this.vehicleRepository = vehicleRepository;
        this.wotTankopediaFeignClient = wotTankopediaFeignClient;
    }

    /** Persists (inserts or updates) a vehicle in the local mirror. */
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public boolean exists(Integer id) {
        return vehicleRepository.existsById(id);
    }

    /**
     * Read-through lookup: the local mirror first, then the WoT API on a miss.
     *
     * @param id the WoT {@code tank_id}
     * @return the vehicle from the local mirror, or a transient WoT-sourced vehicle,
     *         or {@link Optional#empty()} if WoT has no such vehicle either.
     */
    public Optional<Vehicle> getVehicle(Integer id) {
        return vehicleRepository.findById(id).or(() -> fetchFromWot(id));
    }

    private Optional<Vehicle> fetchFromWot(Integer id) {
        var response = wotTankopediaFeignClient.getVehicles(
                WG_APP_ID, null, "en", null, null, null,
                new Integer[]{id}, null, null
        );
        if (response.getBody() == null || response.getBody().getData() == null) {
            return Optional.empty();
        }
        WotVehicle wot = response.getBody().getData().get(id);
        return Optional.ofNullable(wot).map(w -> toVehicle(id, w));
    }

    private Vehicle toVehicle(Integer id, WotVehicle wot) {
        Vehicle vehicle = new Vehicle(id, wot.getName(), wot.getNation(), wot.getTier(), wot.getType());
        vehicle.setShortName(wot.getShortName());
        vehicle.setCanonicalName(canonicalName(wot.getName()));
        return vehicle;
    }

    private String canonicalName(String name) {
        if (name == null) {
            return null;
        }
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
