package com.parking.domain.vehicle.service;

import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.repository.VehicleRepository;
import com.parking.exception.ApiException;
import com.parking.exception.Error;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void isPlateInvalid(final String plate) {
        if(!Strings.isNotEmpty(plate)) throw new ApiException(Error.PLATE_BLANK_INVALID);
        Pattern pattern = Pattern.compile("\\b[A-Z]{3}-(\\d{4})");
        if(!pattern.matcher(plate).matches()) throw new ApiException(Error.PLATE_FORMAT_INVALID);
    }

    @Transactional
    @Override
    public Vehicle create(final String plate){
        Vehicle vehicle = new Vehicle(plate);
        return this.vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findByPlate(final String plate){
        return this.vehicleRepository.findByPlate(plate);
    }
}
