package com.dmytrocherkes.iamservice.repository;

import com.dmytrocherkes.iamservice.model.entity.User;
import com.dmytrocherkes.iamservice.model.request.UserSearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class UserSearchCriteria implements Specification<User> {

    private final UserSearchRequest request;

    @Override
    public Predicate toPredicate(
            @NonNull Root<User> root,
            CriteriaQuery<?> query,
            @NonNull CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicateList = new ArrayList<>();

        if (Objects.nonNull(request.getUsername()))
            predicateList.add(criteriaBuilder.like(root.get(User.USERNAME_FIELD), "%" + request.getUsername() + "%"));

        if (Objects.nonNull(request.getEmail()))
            predicateList.add(criteriaBuilder.like(root.get(User.EMAIL_FIELD), "%" + request.getEmail() + "%"));

        if (Objects.nonNull(request.getRegistrationStatus()))
            predicateList.add(criteriaBuilder.equal(root.get(User.REGISTRATION_STATUS_FILED), request.getRegistrationStatus()));

        if (Objects.nonNull(request.getKeyword())) {
            Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(User.USERNAME_FIELD), "%" + request.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(User.EMAIL_FIELD), "%" + request.getKeyword() + "%")
            );
            predicateList.add(keywordPredicate);
        }

        sort(root, criteriaBuilder, query);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    private void sort(Root<User> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query) {
        if (Objects.nonNull(request.getSortField())) {
            switch (request.getSortField()) {
                case USERNAME -> query.orderBy(criteriaBuilder.desc(root.get(User.USERNAME_FIELD)));
                case EMAIL -> query.orderBy(criteriaBuilder.desc(root.get(User.EMAIL_FIELD)));
                default -> query.orderBy(criteriaBuilder.desc(root.get(User.CREATED_AT_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(User.CREATED_AT_FIELD)));
        }
    }
}
