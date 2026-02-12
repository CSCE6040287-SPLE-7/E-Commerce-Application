package com.app.repositories;

import com.app.entites.VCCPaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VCCPaymentInfoRepo extends JpaRepository<VCCPaymentInfo, Long> {

}
