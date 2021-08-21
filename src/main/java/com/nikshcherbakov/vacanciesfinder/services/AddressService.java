package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Address;
import com.nikshcherbakov.vacanciesfinder.models.MetroStation;
import com.nikshcherbakov.vacanciesfinder.repositories.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    private final MetroStationService metroStationService;

    public AddressService(AddressRepository addressRepository, MetroStationService metroStationService) {
        this.addressRepository = addressRepository;
        this.metroStationService = metroStationService;
    }

    /**
     * Removes address from the database
     * @param address address to be removed
     */
    public void removeAddress(Address address) {
        // Deleting address and closest metro
        MetroStation metro = address.getMetro();
        if (metro != null) {
            metro.getAddresses().remove(address);
            address.setMetro(null);
            if (metro.getAddresses().isEmpty()) {
                metroStationService.removeStation(metro);
            }
        }

        addressRepository.delete(address);
    }

    /**
     * Saves address to the database and returns saved object with id
     * @param address address that needs to be saved
     * @return saved address with id from the database
     */
    public Address save(Address address) {
        MetroStation metro = address.getMetro();
        if (metro != null) {
            MetroStation savedMetro = metroStationService.save(metro);
            address.setMetro(savedMetro);
        }

        return addressRepository.save(address);
    }

    /**
     * Looks up for a given address in the database
     * @param id id by which a search is performed
     * @return address if one exists in the database, null otherwise
     */
    public Address findById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return address.orElse(null);
    }
}
