package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import org.springframework.data.jpa.repository.JpaRepository

interface CumulativeClickCountJpaRepository: JpaRepository<CumulativeClickCount, Long>
