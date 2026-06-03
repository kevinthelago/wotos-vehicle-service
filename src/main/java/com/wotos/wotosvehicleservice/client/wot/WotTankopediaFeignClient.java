package com.wotos.wotosvehicleservice.client.wot;

import com.wotos.wotosvehicleservice.client.wot.vehicle.WotVehicle;
import com.wotos.wotosvehicleservice.validation.constraints.Language;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// NOTE: Spring Cloud OpenFeign forbids a type-level @RequestMapping on @FeignClient
// interfaces, so the shared "/encyclopedia" prefix is folded into each method path.
@FeignClient(name = "WotVehicleFeignClient", url = "${env.urls.world_of_tanks_api}")
public interface WotTankopediaFeignClient {

    @GetMapping("/encyclopedia/vehicles/")
    ResponseEntity<WotApiResponse<Map<Integer, WotVehicle>>> getVehicles(
            @RequestParam(name = "application_id") String appId,
            @RequestParam(name = "fields", required = false) String[] fields,
            @RequestParam(name = "language", required = false, defaultValue = "en") @Language String language,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "nation", required = false) String[] nations,
            @RequestParam(name = "page_no", required = false) Integer pageNumber,
            @RequestParam(name = "tank_id", required = false) Integer[] vehicleIds,
            @RequestParam(name = "tier", required = false) Integer[] tiers,
            @RequestParam(name = "type", required = false) String[] types
    );

    @GetMapping("/encyclopedia/modules/")
    ResponseEntity<WotApiResponse<?>> getModules(
            @RequestParam(name = "application_id") String appId,
            @RequestParam(name = "extra", required = false) String[] extra,
            @RequestParam(name = "fields", required = false) String[] fields,
            @RequestParam(name = "language", required = false, defaultValue = "en") @Language String language,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "module_id", required = false) Integer[] moduleId,
            @RequestParam(name = "nation", required = false) String[] nations,
            @RequestParam(name = "page_no", required = false) Integer pageNumber,
            @RequestParam(name = "type", required = false) String[] type
    );

}
