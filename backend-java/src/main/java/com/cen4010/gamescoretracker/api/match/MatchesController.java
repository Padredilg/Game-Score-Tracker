package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.match.dto.MatchCreateRequest;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import com.cen4010.gamescoretracker.api.match.dto.UserMatchesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserMatchesDTO> getMatchesForUser(@PathVariable UUID userId) {
        UserMatchesDTO dto = matchService.getUserMatches(userId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable UUID id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok("Match deleted successfully.");
    }
}