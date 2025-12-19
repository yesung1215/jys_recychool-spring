package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController("privateReserveApi")
@RequestMapping("/api/private/schools")
@RequiredArgsConstructor
public class ReserveApi {

    private final ReserveService reserveService;

    // 장소대여 예약
    @PostMapping("/{schoolId}/place/reserves")
    public ReserveCreateResponseDTO reservePlace(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long schoolId,
            @RequestBody ReserveCreateRequestDTO requestDTO
    ) {
        return reserveService.createReserve(
                userEmail,
                schoolId,
                ReserveType.PLACE,
                requestDTO
        );
    }

    // 주차 예약
    @PostMapping("/{schoolId}/parking/reserves")
    public ReserveCreateResponseDTO reserveParking(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long schoolId,
            @RequestBody ReserveCreateRequestDTO requestDTO
    ) {
        return reserveService.createReserve(
                userEmail,
                schoolId,
                ReserveType.PARKING,
                requestDTO
        );
    }
}
