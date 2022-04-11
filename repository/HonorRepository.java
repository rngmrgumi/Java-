package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HonorRepository extends JpaRepository<Honor,Integer> {
    Optional<Honor> findByNum(String num);
    List<Honor> findByName(String name);

    @Query(value = "select max(id) from Honor  ")
    Integer getMaxId();

    @Query(value = "from Honor where ?1='' or num like %?1% or name like %?1% ")
    List<Honor> findHonorListByNumName(String numName);



}
