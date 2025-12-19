package com.app.recychool.repository;

import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    // PLACE: 하루 1팀 (PENDING + CONFIRMED 차단)
    boolean existsBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            List<ReserveStatus> statuses,
            LocalDate startDate
    );

    // PARKING: 활성 예약 수 (PENDING + CONFIRMED)
    long countBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            List<ReserveStatus> statuses,
            LocalDate startDate
    );

    // PARKING: 대기번호 계산
    @Query("""
        SELECT MAX(r.waitingOrder)
        FROM Reserve r
        WHERE r.school.id = :schoolId
          AND r.reserveType = com.app.recychool.domain.enums.ReserveType.PARKING
          AND r.startDate = :startDate
    """)
    Integer findMaxWaitingOrder(Long schoolId, LocalDate startDate);

    // 유저 제한

    // 주차: 유저 1건 제한
    boolean existsByUserIdAndReserveTypeAndReserveStatusIn(
            Long userId,
            ReserveType reserveType,
            List<ReserveStatus> statuses
    );

    // 장소대여: 유저 최대 2건
    long countByUserIdAndReserveTypeAndReserveStatusIn(
            Long userId,
            ReserveType reserveType,
            List<ReserveStatus> statuses
    );

    List<Reserve> findBySchoolIdAndReserveTypeAndReserveStatus(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus
    );
}
