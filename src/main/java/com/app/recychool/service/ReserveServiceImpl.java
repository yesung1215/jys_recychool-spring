package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.repository.SchoolRepository;
import com.app.recychool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    @Override
    public ReserveCreateResponseDTO createReserve(
            String userEmail,
            Long schoolId,
            ReserveType reserveType,
            ReserveCreateRequestDTO requestDTO
    ) {

        // 1. 사용자 / 학교 조회
        User user = userRepository.findIdByUserEmail(userEmail);
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 학교입니다."));

        LocalDate startDate = requestDTO.getStartDate();
        LocalDate endDate = requestDTO.getStartDate(); // 기본: 하루

        // 2. 타입별 분기
        if (reserveType == ReserveType.PLACE) {
            return createPlaceReserve(user, school, startDate);
        } else {
            return createParkingReserve(user, school, startDate);
        }
    }

    /* =========================
       PLACE 예약 (하루 1팀)
       ========================= */
    private ReserveCreateResponseDTO createPlaceReserve(
            User user,
            School school,
            LocalDate date
    ) {

        boolean exists = reserveRepository
                .existsBySchoolIdAndReserveTypeAndReserveStatusAndStartDate(
                        school.getId(),
                        ReserveType.PLACE,
                        ReserveStatus.CONFIRMED,
                        date
                );

        if (exists) {
            throw new IllegalStateException("이미 장소대여 예약이 존재합니다.");
        }

        Reserve reserve = Reserve.builder()
                .user(user)
                .school(school)
                .reserveType(ReserveType.PLACE)
                .reserveStatus(ReserveStatus.CONFIRMED)
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

    /* =========================
       PARKING 예약 (선착순 + 대기)
       ========================= */
    private ReserveCreateResponseDTO createParkingReserve(
            User user,
            School school,
            LocalDate date
    ) {

        // 최대 주차 가능 대수 계산 (예시: 대지면적 / 25)
        int maxParking = school.getSchoolLand() / 25;

        long confirmedCount =
                reserveRepository.countBySchoolIdAndReserveTypeAndReserveStatusAndStartDate(
                        school.getId(),
                        ReserveType.PARKING,
                        ReserveStatus.CONFIRMED,
                        date
                );

        Reserve reserve;

        if (confirmedCount < maxParking) {
            // 바로 확정
            reserve = Reserve.builder()
                    .user(user)
                    .school(school)
                    .reserveType(ReserveType.PARKING)
                    .reserveStatus(ReserveStatus.CONFIRMED)
                    .startDate(date)
                    .endDate(date.plusMonths(1)) // 최대 1개월
                    .reservePrice(30_000)
                    .reserveDeposit(0)
                    .build();
        } else {
            // 대기
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
}
