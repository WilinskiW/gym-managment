package com.task.gymmanagment.infrastructure;

import com.task.gymmanagment.domain.GymManagmentFacade;
import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gym")
@RequiredArgsConstructor
public class GymManagmentController {
    private final GymManagmentFacade managmentFacade;

    @PostMapping
    public ResponseEntity<Long> addGym(@RequestBody AddGymRequestDto gymRequestDto) {
        return ResponseEntity.ok(managmentFacade.addGym(gymRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<GymInfoResponseDto>> getAllGyms() {
        return ResponseEntity.ok(managmentFacade.getAllGyms());
    }
}
