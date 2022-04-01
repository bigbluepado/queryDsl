package com.zipzoong.sample.repository;

import com.zipzoong.sample.entity.Member;
import com.zipzoong.sample.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
