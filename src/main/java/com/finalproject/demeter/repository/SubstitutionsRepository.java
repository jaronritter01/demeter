package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.Substitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubstitutionsRepository extends JpaRepository<Substitution, Long> {
    List<Substitution> findByMissingItemId(long missing_item_id);
}
