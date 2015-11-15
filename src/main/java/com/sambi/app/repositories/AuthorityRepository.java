package com.sambi.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sambi.app.domain.AuthorityEntity;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String> {

    @Query(nativeQuery=true, value ="SELECT a.* from authorities a, users u WHERE a.username=:username AND a.username=u.username AND u.password=:password")
    List<AuthorityEntity> findBy(@Param("username") String username, @Param("password") String password);
}
