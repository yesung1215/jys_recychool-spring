package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.exception.ReserveException;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.repository.SchoolRepository;
import com.app.recychool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    @Override
    public ReserveCreateResponseDTO createReserve(
            Long userId,
            Long schoolId,
            ReserveType reserveType,
            ReserveCreateRequestDTO requestDTO
    ) {

        // 0. 사용자 예약 횟수 검증( 주차 1 / 장소대여 2 )
        if (reserveType == ReserveType.PARKING) {
            validateParkingLimit(userId);
        }

        if (reserveType == ReserveType.PLACE) {
            validatePlaceLimit(userId);
        }

        // 1. 사용자 / 학교 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReserveException("존재하지 않는 사용자입니다."));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ReserveException("존재하지 않는 학교입니다."));

        LocalDate startDate = requestDTO.getStartDate();

        // 2. 타입별 분기
        if (reserveType == ReserveType.PLACE) {
            return createPlaceReserve(user, school, startDate);
        } else {
            return createParkingReserve(user, school, startDate);
        }
    }

    // PLACE 예약 (하루 1팀)
    private ReserveCreateResponseDTO createPlaceReserve(
            User user,
            School school,
            LocalDate date
    ) {

        // PENDING + CONFIRMED 모두 차단
        boolean exists =
                reserveRepository.existsBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
                        school.getId(),
                        ReserveType.PLACE,
                        List.of(ReserveStatus.PENDING, ReserveStatus.CONFIRMED),
                        date
                );

        if (exists) {
            throw new ReserveException("이미 장소대여 예약이 존재합니다.");
        }

        // 결제 전 → PENDING
        Reserve reserve = Reserve.builder()
                .user(user)
                .school(school)
                .reserveType(ReserveType.PLACE)
                .reserveStatus(ReserveStatus.PENDING)
                .startDate(date)
                .endDate(date)
                .reservePrice(0)
                .reserveDeposit(50_000)
                .build();

        Reserve saved = reserveRepository.save(reserve);

        return ReserveCreateResponseDTO.builder()
                .reserveId(saved.getId())
                .reserveStatus(saved.getReserveStatus())
                .price(saved.getReservePrice())
                .deposit(saved.getReserveDeposit())
                .build();
    }

    // PARKING 예약 (선착순 + 대기)
    private ReserveCreateResponseDTO createParkingReserve(
            User user,
            School school,
            LocalDate date
    ) {

        int maxParking = school.getSchoolLand() / 25;

        long activeCount =
                reserveRepository.countBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
                        school.getId(),
                        ReserveType.PARKING,
                        List.of(ReserveStatus.PENDING, ReserveStatus.CONFIRMED),
                        date
                );

        Reserve reserve;

        if (activeCount < maxParking) {
            // 자리 있음 → PENDING
            reserve = Reserve.builder()
                    .user(user)
                    .school(school)
                    .reserveType(ReserveType.PARKING)
                    .reserveStatus(ReserveStatus.PENDING)
                    .startDate(date)
                    .endDate(date.plusMonths(1))
                    .reservePrice(30_000)
                    .reserveDeposit(0)
                    .build();
        } else {
            // 자리 없음 → WAITING
            Integer maxOrder =
                    reserveRepository.findMaxWaitingOrder(school.getId(), date);

            int nextOrder = (maxOrder == null) ? 1 : maxOrder + 1;

            reserve = Reserve.builder()
                    .user(user)
                    .school(school)
                    .reserveType(ReserveType.PARKING)
                    .reserveStatus(ReserveStatus.WAITING)
                    .waitingOrder(nextOrder)
                    .startDate(date)
                    .endDate(date.plusMonths(1))
                    .reservePrice(30_000)
                    .reserveDeposit(0)
                    .build();
        }

        Reserve saved = reserveRepository.save(reserve);

        return ReserveCreateResponseDTO.builder()
                .reserveId(saved.getId())
                .reserveStatus(saved.getReserveStatus())
                .price(saved.getReservePrice())
                .deposit(saved.getReserveDeposit())
                .waitingOrder(saved.getWaitingOrder())
                .build();
    }

    private void validateParkingLimit(Long userId) {
        boolean exists =
                reserveRepository.existsByUserIdAndReserveTypeAndReserveStatusIn(
                        userId,
                        ReserveType.PARKING,
                        List.of(ReserveStatus.PENDING, ReserveStatus.CONFIRMED)
                );

        if (exists) {
            throw new ReserveException("주차 예약은 1건만 가능합니다. 취소 후 다시 예약해주세요.");
        }
    }

    private void validatePlaceLimit(Long userId) {
        long count =
                reserveRepository.countByUserIdAndReserveTypeAndReserveStatusIn(
                        userId,
                        ReserveType.PLACE,
                        List.of(ReserveStatus.PENDING, ReserveStatus.CONFIRMED)
                );

        if (count >= 2) {
            throw new ReserveException("장소대여는 최대 2건까지 예약 가능합니다.");
        }
    }

}
