package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Role;
import com.nikshcherbakov.vacanciesfinder.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Finds for a role in the database by name
     * @param name name by which role is found
     * @return role if one exists in the database, null otherwise
     */
    public Role findByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);
        return role.orElse(null);
    }

    /**
     * Saves role to the database
     * @param role role that needs to be saved
     */
    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
