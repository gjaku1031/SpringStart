package com.example.springstart.controller;

import com.example.springstart.domain.user.dto.PasswordUpdateRequestDto;
import com.example.springstart.domain.user.dto.PasswordUpdateResponseDto;
import com.example.springstart.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/{id}/password")
    public ResponseEntity<PasswordUpdateResponseDto> updatePassword(@PathVariable Long id,
                                                                    @Valid @RequestBody PasswordUpdateRequestDto dto) {

        PasswordUpdateResponseDto response = userService.updatePassword(id, dto);
        return ResponseEntity.ok(response);
    }
}