package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 大小写敏感用户名匹配（MySQL BINARY 比较）
     */
    @Query(value = "SELECT * FROM users WHERE BINARY username = :username LIMIT 1", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 大小写敏感邮箱匹配（MySQL BINARY 比较）
     */
    @Query(value = "SELECT * FROM users WHERE BINARY email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 大小写敏感用户名或邮箱匹配（MySQL BINARY 比较）
     */
    @Query(value = "SELECT * FROM users WHERE BINARY username = :username OR BINARY email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * 大小写敏感存在性检查（用户名）
     */
    @Query(value = "SELECT COUNT(1) FROM users WHERE BINARY username = :username", nativeQuery = true)
    long countByUsernameCase(@Param("username") String username);

    /**
     * 大小写敏感存在性检查（邮箱）
     */
    @Query(value = "SELECT COUNT(1) FROM users WHERE BINARY email = :email", nativeQuery = true)
    long countByEmailCase(@Param("email") String email);
}
