package com.api.bkhouse.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.api.bkhouse.constant.PayContent;
import com.api.bkhouse.constant.enumeric.ERole;
import com.api.bkhouse.entity.*;
import com.api.bkhouse.entity.response.IAgencyRep;
import com.api.bkhouse.entity.response.IDistrict;
import com.api.bkhouse.payload.dto.DistrictDTO;
import com.api.bkhouse.payload.dto.SpecialAccountDTO;
import com.api.bkhouse.payload.request.AgencyRegisterRequest;
import com.api.bkhouse.payload.response.AgencyInfoResponse;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.repository.SpecialAccountPayRepository;
import com.api.bkhouse.repository.SpecialAccountRepository;
import com.api.bkhouse.repository.UserRepository;
import com.api.bkhouse.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpecialAccountService {
    private final SpecialAccountRepository repository;

    private final UserRepository userRepository;

    private final SpecialAccountPayRepository specialAccountPayRepository;

    private final ModelMapper modelMapper;  

    public SpecialAccountService(SpecialAccountRepository repository, UserRepository userRepository, SpecialAccountPayRepository specialAccountPayRepository, ModelMapper modelMapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.specialAccountPayRepository = specialAccountPayRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public SpecialAccount addSpecialAccount(SpecialAccount specialAccount) {
        return repository.save(specialAccount);
    }

    @Transactional
    public BaseResponse agencyRegister(AgencyRegisterRequest request) {
        try {
            UUID userId = request.getUserId();

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return new BaseResponse(null, "Không tìm thấy thông tin người dùng này.", HttpStatus.NO_CONTENT);
            }
            User user = userOptional.get();
            List<DistrictDTO> districtDTOS = request.getDistricts();

            int totalPaid = Util.agencyMonthlyPaid(districtDTOS);
            if (totalPaid > user.getAccountBalance()) {
                return new BaseResponse(null, "Tài khoản của người dùng không đủ để đăng ký.", HttpStatus.NO_CONTENT);
            }
            userRepository.agencyRegister(request.getUserId());

            SpecialAccount specialAccount = new SpecialAccount();
            specialAccount.setUserId(request.getUserId());
            specialAccount.setAgency(true);
            specialAccount.setLastPaid(Util.getCurrentDateTime());
            specialAccount.setMonthlyCharge(totalPaid);
            repository.save(specialAccount);

            SpecialAccountPay specialAccountPay = new SpecialAccountPay();
            specialAccountPay.setUser(user);
            specialAccountPay.setId(null);
            specialAccountPay.setAmount(totalPaid);
            specialAccountPay.setAccountBalance(user.getAccountBalance() - totalPaid);
            specialAccountPay.setContent(PayContent.MONTHLY_CHARGE);
            specialAccountPay.setMonthlyPay(true);
            specialAccountPay.setCreateBy(userId);
            specialAccountPay.setCreateAt(Util.getCurrentDateTime());
            specialAccountPayRepository.save(specialAccountPay);

            for (DistrictDTO districtDTO: districtDTOS) {
                userRepository.agencyDistrictInsert(userId, districtDTO.getCode());
            }

            user.setAccountBalance(user.getAccountBalance() - totalPaid);
            user.setUpdatedAt(Util.getCurrentDateTime());
            user.setUpdatedBy(user.getId());
            userRepository.save(user);

            return new BaseResponse(null, "Đăng ký tài khoản môi giới thành công.", HttpStatus.OK);
        } catch (Exception e) {
            return new BaseResponse(null,
                    "Đã xảy ra lỗi khi đăng ký tài khoản môi giới. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public BaseResponse agencyUpdate(AgencyRegisterRequest request) {
        try {
            UUID userId = request.getUserId();

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return new BaseResponse(null, "Không tìm thấy thông tin người dùng này.", HttpStatus.NO_CONTENT);
            }
            User user = userOptional.get();
            List<DistrictDTO> districtDTOS = request.getDistricts();

            int totalPaid = Util.agencyMonthlyPaid(districtDTOS);

            Optional<SpecialAccount> specialAccountOptional = repository.findByUserId(request.getUserId());
            if (specialAccountOptional.isEmpty()) {
                return new BaseResponse(null, "Không tìm thấy thông tin người dùng này.", HttpStatus.NO_CONTENT);
            }
            SpecialAccount specialAccount = specialAccountOptional.get();
            if (totalPaid - specialAccount.getMonthlyCharge() > user.getAccountBalance()) {
                return new BaseResponse(null, "Tài khoản của người dùng không đủ để đăng ký.", HttpStatus.NO_CONTENT);
            }

            if (totalPaid > specialAccount.getMonthlyCharge()) {
                int delta = totalPaid - specialAccount.getMonthlyCharge();
                SpecialAccountPay specialAccountPay = new SpecialAccountPay();
                specialAccountPay.setUser(user);
                specialAccountPay.setId(null);
                specialAccountPay.setAmount(delta);
                specialAccountPay.setAccountBalance(user.getAccountBalance() - delta);
                specialAccountPay.setContent(PayContent.EXTRA_CHARGE);
                specialAccountPay.setMonthlyPay(false);
                specialAccountPay.setCreateBy(userId);
                specialAccountPay.setCreateAt(Util.getCurrentDateTime());
                specialAccountPayRepository.save(specialAccountPay);

                user.setAccountBalance(user.getAccountBalance() - delta);
                user.setUpdatedAt(Util.getCurrentDateTime());
                user.setUpdatedBy(user.getId());
                userRepository.save(user);
            }

            specialAccount.setMonthlyCharge(totalPaid);
            repository.save(specialAccount);

            repository.agencyDistrictDeleteByUserId(userId);
            for (DistrictDTO districtDTO: districtDTOS) {
                userRepository.agencyDistrictInsert(userId, districtDTO.getCode());
            }
            return new BaseResponse(null, "Cập nhật thông tin đăng ký tài khoản môi giới thành công.", HttpStatus.OK);
        } catch (Exception e) {
            return new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật thông tin đăng ký tài khoản môi giới. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteByUserId(UUID userId) throws Exception {
        repository.deleteByUserId(userId);
    }

    @Transactional
    public void agencyDistrictDeleteByUserId(UUID userId) throws Exception {
        repository.agencyDistrictDeleteByUserId(userId);
    }

    @Transactional
    public void userRoleDeleteByUserId(UUID userId) throws Exception {
        repository.userRoleDeleteByUserId(userId);
    }

    public AgencyInfoResponse findAgencyInfo(UUID userId) {
        Optional<SpecialAccount> specialAccountOptional = repository.findByUserId(userId);
        if (specialAccountOptional.isEmpty()) {
            return null;
        }
        List<IDistrict> districts = repository.findAllDistrictsAgency(userId);
        if (districts.isEmpty()) {
            return null;
        }
        List<DistrictDTO> districtDTOS = new ArrayList<>();
        for (IDistrict iDistrict: districts) {
            DistrictDTO districtDTO = new DistrictDTO();
            districtDTO.setCode(iDistrict.getCode());
            districtDTO.setCodeName(iDistrict.getCode_name());
            districtDTO.setName(iDistrict.getName());
            districtDTO.setFullName(iDistrict.getFull_name());
            districtDTO.setFullNameEn(iDistrict.getFull_name_en());
            districtDTO.setProvinceCode(iDistrict.getProvince_code());
            districtDTO.setAdministrativeUnitId(iDistrict.getAdministrative_unit_id());
            districtDTO.setNameEn(iDistrict.getName_en());
            districtDTOS.add(districtDTO);
        }
        AgencyInfoResponse agencyInfoResponse = new AgencyInfoResponse();
        agencyInfoResponse.setSpecialAccount(modelMapper.map(specialAccountOptional.get(), SpecialAccountDTO.class));
        agencyInfoResponse.setDistricts(districtDTOS);
        return agencyInfoResponse;
    }

    public boolean isAgency(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> ERole.ROLE_AGENCY.equals(role.getName())))
                .orElse(false);
    }

    public List<String> getAllDistrictCodeOfAgency(UUID userId) {
        return repository.getAllDistrictCodeOfAgency(userId);
    }

    public SpecialAccount findById(UUID userId) {
       return repository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public void update(SpecialAccount specialAccount) {
        repository.save(specialAccount);
    }

    public List<IAgencyRep> listAgencyByRepDistrict(UUID realEstatePostId) {
        return repository.listAgencyByRepDistrict(realEstatePostId);
    }
}
