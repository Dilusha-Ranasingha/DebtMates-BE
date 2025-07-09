package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.GroupDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupDetailsRepository extends JpaRepository<GroupDetails, Long> {
}

//This repository provides CRUD operations for the GroupDetails entity.