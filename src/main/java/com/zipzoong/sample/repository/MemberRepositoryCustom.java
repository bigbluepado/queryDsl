package com.zipzoong.sample.repository;

import com.zipzoong.sample.dto.MemberSearchCondition;
import com.zipzoong.sample.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition,
                                                Pageable pageable);
    Page<MemberTeamDto> searchPageComplexV2(MemberSearchCondition condition,
                                                  Pageable pageable);
    Page<MemberTeamDto> searchPageComplexV3(MemberSearchCondition condition,
                                                  Pageable pageable);
}
