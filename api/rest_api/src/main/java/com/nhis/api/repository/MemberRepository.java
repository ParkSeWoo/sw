package com.nhis.api.repository;

import com.nhis.api.domain.Member;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sewoo on 2017. 1. 11..
 */


@RepositoryRestResource
public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {}