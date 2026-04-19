package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.api.bkhouse.constant.enumeric.EProjectType;
import com.api.bkhouse.entity.Project;
import com.api.bkhouse.entity.ProjectInterested;
import com.api.bkhouse.entity.ProjectView;
import com.api.bkhouse.entity.response.IProjectStatistic;
import com.api.bkhouse.payload.response.chart.ChartOption;
import com.api.bkhouse.repository.ProjectInterestedRepository;
import com.api.bkhouse.repository.ProjectParamRepository;
import com.api.bkhouse.repository.ProjectRepository;
import com.api.bkhouse.repository.ProjectViewRepository;
import com.api.bkhouse.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectParamRepository projectParamRepository;

    @Autowired
    private ProjectInterestedRepository projectInterestedRepository;

    @Autowired
    private ProjectViewRepository projectViewRepository;

    @Transactional
    public UUID save(Project project) {
        return repository.save(project).getId();
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteProject(id);
    }

    public Project findById(UUID id, boolean increaseView) {
        if (increaseView) {
            ProjectView projectView = new ProjectView();
            projectView.setId(0L);
            projectView.setProjectId(id);
            projectView.setCreateAt(Util.getCurrentDateTime());
            projectViewRepository.save(projectView);
        }
        Optional<Project> projectOptional = repository.findByIdAndEnable(id, true);
        return projectOptional.orElse(null);
    }

    public List<Project> findByUserId(UUID userId) {
        return repository.findByCreateByAndEnable(userId, true, Sort.by(Sort.Direction.DESC, "createAt"));
    }

    public List<Project> findAll() {
        return repository.findByEnable(true, Sort.by(Sort.Direction.DESC, "createAt"));
    }

    public boolean existsByIdAndEnable(UUID id) {
        return repository.existsByIdAndEnable(id, true);
    }

    public boolean paramExistsByIdAndProjectId(Long id, UUID projectId) {
        return projectParamRepository.existsByIdAndProjectId(id, projectId);
    }

    @Transactional
    public void deleteParam(Long id) {
        projectParamRepository.deleteById(id);
    }

    public boolean paramBelongToUser(UUID userId, Long id) {
        long result = projectParamRepository.paramIsBelongToUser(id, userId);
        if (result > 0) {
            return true;
        }
        return false;
    }

    public boolean projectBelongToUser(UUID userId, UUID id) {
        return repository.existsByIdAndEnableAndCreateBy(id, true, userId);
    }
    public List<Project> findByTypePageable(Integer page, Integer pageSize, EProjectType type) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createAt").descending());
        return repository.findByTypeAndEnable(type, true, pageable);
    }

    public Long increaseView(UUID id) {
        ProjectView projectView = new ProjectView();
        projectView.setId(0L);
        projectView.setProjectId(id);
        projectView.setCreateAt(Util.getCurrentDateTime());
        return projectViewRepository.save(projectView).getId();
    }

    public Project findById(UUID id) {
        Optional<Project> projectOptional = repository.findByIdAndEnable(id, true);
        return projectOptional.orElse(null);
    }

    public boolean isInterested(UUID userId, UUID realEstatePostId, String deviceId) {
        if (deviceId != null && deviceId.length() > 0 && (userId == null || userId.toString().length() == 0)) {
            return projectInterestedRepository.existsByDeviceIdAndProjectId(deviceId, realEstatePostId);
        }
        return projectInterestedRepository.existsByUserIdAndProjectId(userId, realEstatePostId);
    }

    public Optional<ProjectInterested> findByDeviceIdAndProjectId(String deviceId, UUID projectId) {
        return projectInterestedRepository.findByDeviceIdAndProjectId(deviceId, projectId);
    }

    public Optional<ProjectInterested> findByUserIdAndRealEstatePostId(UUID userId, UUID projectId) {
        return projectInterestedRepository.findByUserIdAndProjectId(userId, projectId);
    }

    @Transactional
    public ProjectInterested saveInterested(ProjectInterested interested) {
        return projectInterestedRepository.save(interested);
    }

    @Transactional
    public void deleteInterested(Long id) {
        projectInterestedRepository.deleteById(id);
    }

    public ChartOption getChartOption(Integer id, Integer year) {
        ChartOption chartOption = new ChartOption();
        List<IProjectStatistic> response;
        if (id == 1) {
            response = repository.getAllInYear(year);
        } else if (id == 2) {
            response = repository.getByInterestedInYear(year);
        } else if (id == 3) {
            response = repository.getByViewInYear(year);
        } else {
            response = repository.getByCommentInYear(year);
        }
        response
                .stream()
                .parallel()
                .forEach(e -> {
                    chartOption.getXaxis().add(e.getLabel());
                    chartOption.getSeries().add(e.getCnt());
                });
        return chartOption;
    }

    public List<Project> findAllProjectsInterestedByUser(UUID userId) {
        List<ProjectInterested> projectInterests = projectInterestedRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createAt"));
        List<Project> response = new ArrayList<>();
        projectInterests
                .stream()
                .forEach(e -> {
                    Optional<Project> projectOptional = repository.findByIdAndEnable(e.getProjectId(), true);
                    if (projectOptional.isPresent()) {
                        response.add(projectOptional.get());
                    }
                });
        return response;
    }
}
