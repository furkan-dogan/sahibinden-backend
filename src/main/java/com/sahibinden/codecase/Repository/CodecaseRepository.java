package com.sahibinden.codecase.Repository;

import com.sahibinden.codecase.model.CodecaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodecaseRepository extends JpaRepository<CodecaseModel, Long> {

}
