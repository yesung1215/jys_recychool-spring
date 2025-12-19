package com.app.recychool.api.publicapi;

import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.service.ReserveQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("publicReserveApi")
@RequestMapping("/api/public/schools")
@RequiredArgsConstructor
public class ReserveApi {

    private final ReserveQueryService reserveQueryService;

    // 장소대여 페이지
    @GetMapping("/{schoolId}/place")
    public SchoolReservePageResponseDTO getPlaceReservePage(
            @PathVariable Long schoolId
    ) {
        return reserveQueryService.getReservePageInfo(
                schoolId,
                ReserveType.PLACE
        );
    }

    // 주차 예약 페이지
    @GetMapping("/{schoolId}/parking")
    public SchoolReservePageResponseDTO getParkingReservePage(
            @PathVariable Long schoolId
    ) {
        return reserveQueryService.getReservePageInfo(
                schoolId,
                ReserveType.PARKING
        );
    }
}

