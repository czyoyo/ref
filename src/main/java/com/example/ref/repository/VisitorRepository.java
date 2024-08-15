package com.example.ref.repository;

import com.example.ref.entity.Visitor;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    boolean existsByIpAndVisitDate(String ip, LocalDate visitDate);

}
