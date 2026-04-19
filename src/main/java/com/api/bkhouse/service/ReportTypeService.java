package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.ReportType;
import com.api.bkhouse.repository.ReportTypeRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReportTypeService {
    @Autowired
    private ReportTypeRepository repository;

    @Transactional
    public ReportType save(ReportType reportType) {
        return repository.save(reportType);
    }

    public List<ReportType> getAllByIsForum(boolean isForum) {
        return repository.findByIsForum(isForum);
    }

    public List<ReportType> getAll() {
        return repository.findAll();
    }

    public Integer countByReportTypeId(Integer reportTypeId) {
        return repository.countByRTId(reportTypeId);
    }

    @Transactional
    public void deletePostReportTypeByReportTypeId(Integer reportTypeId) {
        repository.deletePostReportTypeByReportTypeId(reportTypeId);
    }

    @Transactional
    public void deletePostReportType(Integer id) {
        repository.deleteById(id);
    }
}
