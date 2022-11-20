package com.parking.domain.parking.repository;

import com.parking.domain.parking.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ParkingRepository extends JpaRepository<Parking, Long> {
    Optional<Parking> findByVehiclePlateAndPaidIsFalse(final String plate);
    Optional<Parking> findByVehicleIdAndPaidIsTrueAndExitDateIsNull(final Long idVehicle);

    Optional<Parking> findByVehicleIdAndExitDateIsNull(final Long idVehicle);
    Optional<List<Parking>> findByVehiclePlateOrderByIdDesc(final String plate);
}
