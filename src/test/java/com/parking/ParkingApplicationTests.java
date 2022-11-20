package com.parking;

import com.parking.domain.parking.api.v1.ParkingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ParkingApplication.class)
@TestPropertySource(
		locations = "classpath:application.properties")
class ParkingApplicationTests {

	@Autowired
	private ParkingController controller;

	@Test
	void contextsLoads() {
			assertThat(controller).isNotNull();
	}

}
