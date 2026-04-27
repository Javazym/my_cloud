package org.example.authserver.repository;

import org.example.authserver.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<SysUser, String> {
    SysUser findByUsername(String username);
    SysUser findByEmail(String email);

}
