package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.match.dto.MatchCreateRequest;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchesController {

    private final MatchService matchService;

    @PostMapping("/add")
    public ResponseEntity<MatchReportDTO> addMatch(@RequestBody MatchCreateRequest request) {
        return ResponseEntity.ok(matchService.recordMatch(request));
    }

    @GetMapping("/user/{userId}") //TODO -- UPDATE RETURN OBJECT TO BE MORE TAILORED FOR THIS SPECIFIC ENDPOINT
    public ResponseEntity<List<MatchReportDTO>> getMatchesForUser(@PathVariable UUID userId) {
        List<MatchReportDTO> matches = matchService.getMatchesForUser(userId);
        return ResponseEntity.ok(matches);
    }
}