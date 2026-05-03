package com.portfolio.service.impl;

import com.portfolio.dto.request.SkillRequest;
import com.portfolio.dto.response.SkillResponse;
import com.portfolio.entity.Skill;
import com.portfolio.exception.AppException;
import com.portfolio.mapper.SkillMapper;
import com.portfolio.repository.SkillRepository;
import com.portfolio.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional
    @CacheEvict(value = "skills", allEntries = true)
    public SkillResponse create(SkillRequest request) {
        return skillMapper.toResponse(skillRepository.save(skillMapper.toEntity(request)));
    }

    @Override
    @Transactional
    @CacheEvict(value = "skills", allEntries = true)
    public SkillResponse update(UUID id, SkillRequest request) {
        Skill skill = findOrThrow(id);
        skillMapper.updateEntity(request, skill);
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    @CacheEvict(value = "skills", allEntries = true)
    public void delete(UUID id) {
        skillRepository.delete(findOrThrow(id));
    }

    @Override
    @Cacheable(value = "skills", unless = "#result == null")
    public List<SkillResponse> getAll() {
        return skillRepository.findByIsVisibleTrueOrderByDisplayOrderAsc()
                .stream().map(skillMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "skills", key = "#category", unless = "#result == null")
    public List<SkillResponse> getByCategory(String category) {
        return skillRepository.findByCategoryOrderByDisplayOrderAsc(category)
                .stream().map(skillMapper::toResponse).toList();
    }

    private Skill findOrThrow(UUID id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Skill", id));
    }
}