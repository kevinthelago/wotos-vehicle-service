package com.wotos.wotosvehicleservice.controller;

import com.wotos.wotosvehicleservice.service.VehicleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(VehicleController.class)
public class VehicleControllerTests {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private VehicleService vehicleService;

    @Before
    public void init() {
        when(vehicleService.getVehicles(null, null, null, null, null, null, null, null))
                .thenReturn(null);
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(mvc).isNotNull();
        assertThat(vehicleService).isNotNull();
    }

    @Test
    public void shouldReturn200() throws Exception {
        this.mvc.perform(get("/api/vehicles")).andExpect(status().is2xxSuccessful());
    }
}
