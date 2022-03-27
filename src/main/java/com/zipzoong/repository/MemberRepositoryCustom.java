package com.zipzoong.repository;

import com.zipzoong.dto.MemberSearchCondition;
import com.zipzoong.dto.MemberTeamDto;
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
