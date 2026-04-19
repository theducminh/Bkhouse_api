package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.InfoType;
import com.api.bkhouse.repository.InfoPostRepository;
import com.api.bkhouse.repository.InfoTypeRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InfoTypeService {
    @Autowired
    private InfoTypeRepository repository;

    @Autowired
    private InfoPostRepository infoPostRepository;

    @Transactional
    public InfoType createInfoType(InfoType infoType) {
        return repository.save(infoType);
    }

    @Transactional
    public InfoType updateInfoType(InfoType infoType) {
        return repository.save(infoType);
    }

    public List<InfoType> getAllSkip6() {
        return repository.findAll()
                .stream()
                .filter(e -> e.getId() > 6)
                .collect(Collectors.toList());
    }

    public List<InfoType> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteInfoType(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteInfoPostByInfoTypeId(Integer id) {
        infoPostRepository.deleteAllInfoPostByInfoTypeId(id);
    }

    public InfoType findById(Integer id) {
        Optional<InfoType> infoTypeOptional = repository.findById(id);
        if (infoTypeOptional.isEmpty()) {
            return null;
        }
        return infoTypeOptional.get();
    }

    public List<InfoType> getTinTucInfoType(Integer id) {
        return repository.findByIdGreaterThanEqual(id);
    }
}
