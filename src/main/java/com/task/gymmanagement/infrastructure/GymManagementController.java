package com.task.gymmanagement.infrastructure;

import com.task.gymmanagement.domain.GymManagementFacade;
import com.task.gymmanagement.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagement.domain.dto.response.GymDto;
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
public class GymManagementController {
    private final GymManagementFacade managementFacade;

    @PostMapping
    public ResponseEntity<Long> addGym(@RequestBody AddGymRequestDto gymRequestDto) {
        return ResponseEntity.ok(managementFacade.addGym(gymRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<GymDto>> getAllGyms() {
        return ResponseEntity.ok(managementFacade.getAllGyms());
    }
}
