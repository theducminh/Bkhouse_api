package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.bkhouse.entity.AdministrativeUnit;
import com.api.bkhouse.payload.dto.AdministrativeUnitDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.AdministrativeUnitService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/no-auth/ad-unit")
public class AdministrativeUnitController {
    @Autowired
    private AdministrativeUnitService administrativeUnitService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        try {
            List<AdministrativeUnit> serviceResponse = administrativeUnitService.getAll();
            if (serviceResponse.size() == 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Khong tim thay ban ghi nao", HttpStatus.NOT_FOUND));
            } else {
                return ResponseEntity.ok(new BaseResponse(serviceResponse.stream().map(this::convertToDto).collect(Collectors.toList()), "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null, "Da xay ra loi khi thuc hien", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private AdministrativeUnitDTO convertToDto(AdministrativeUnit user) {
        return modelMapper.map(user, AdministrativeUnitDTO.class);
    }
}
