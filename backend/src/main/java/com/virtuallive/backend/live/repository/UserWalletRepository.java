package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, Integer> {
    Optional<UserWallet> findByUserId(Integer userId);
}