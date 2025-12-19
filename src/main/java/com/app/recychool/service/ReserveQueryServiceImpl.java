package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReserveQueryServiceImpl implements ReserveQueryService {

    private final SchoolRepository schoolRepository;
    private final ReserveRepository reserveRepository;

    @Override
    public SchoolReservePageResponseDTO getReservePageInfo(
            Long schoolId,
            ReserveType reserveType
    ) {

        // 학교 조회 (Repository 수정 없이)
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("학교가 존재하지 않습니다."));

        // 예약 불가 날짜 계산
        List<LocalDate> unavailableDates =
                getUnavailableDates(schoolId, reserveType);

        // 타입별 정책 값
        int price;
        int deposit;
        String usageTime;

        if (reserveType == ReserveType.PLACE) {
            price = 0;
            deposit = 50_000;
            usageTime = "09:00 ~ 17:00";
        } else {
            price = 30_000;
            deposit = 0;
            usageTime = "18:00 ~ 08:00";
        }

        // DTO 반환
        return SchoolReservePageResponseDTO.builder()
                .schoolId(school.getId())
                .schoolName(school.getSchoolName())
                .schoolAddress(school.getSchoolAddress())
                .schoolPhone(school.getSchoolPhone())
                .price(price)
                .deposit(deposit)
                .usageTime(usageTime)
                .unavailableDates(unavailableDates)
                .build();
    }

    // 캘린더 예약 불가 날짜
    private List<LocalDate> getUnavailableDates(
            Long schoolId,
            ReserveType reserveType
    ) {

        // PLACE: 확정된 날짜 전부 불가
        if (reserveType == ReserveType.PLACE) {
            return reserveRepository
                    .findBySchoolIdAndReserveTypeAndReserveStatus(
                            schoolId,
                            ReserveType.PLACE,
                            ReserveStatus.CONFIRMED
                    )
                    .stream()
                    .map(Reserve::getStartDate)
                    .toList();
        }

        // PARKING: 현재는 제한 없음 (정책 확장 가능)
        return List.of();
    }
}
