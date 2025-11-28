package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.match.dto.MatchCreateRequest;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchesController {

    private final MatchService matchService;

    @PostMapping("/add")
    public ResponseEntity<MatchReportDTO> addMatch(@RequestBody MatchCreateRequest request) {
        return ResponseEntity.ok(matchService.recordMatch(request));
    }
}