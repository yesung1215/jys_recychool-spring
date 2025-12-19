package com.app.recychool.repository;

import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    Reserve save(Reserve reserve);

    Reserve findById(long id);

    List<Reserve> findAll();

    // PLACE 예약 체크 - 하루 1팀만 가능

    boolean existsBySchoolIdAndReserveTypeAndReserveStatusAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus,
            LocalDate startDate
    );

    // PARKING 예약 수 조회 - 현재 확정된 주차 예약 수

    long countBySchoolIdAndReserveTypeAndReserveStatusAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus,
            LocalDate startDate
    );

    // PARKING 대기번호 조회 - 가장 마지막 대기번호
    @Query("""
        SELECT MAX(r.waitingOrder)
        FROM Reserve r
        WHERE r.school.id = :schoolId
          AND r.reserveType = com.app.recychool.domain.enums.ReserveType.PARKING
          AND r.startDate = :startDate
    """)
    Integer findMaxWaitingOrder(Long schoolId, LocalDate startDate);

    // PLACE: 이미 확정된 날짜 조회 (하루 1팀)
    List<Reserve> findBySchoolIdAndReserveTypeAndReserveStatus(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus
    );
}
