package com.vi5hnu.codesprout.specifications;

import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProblemArchiveSpecification {
    public static Specification<ProblemArchive> hasLanguage(ProblemLanguage language) {
        return (Root<ProblemArchive> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (language == null) {
                return criteriaBuilder.conjunction(); // No filter if language is null
            }
            return criteriaBuilder.equal(root.get("language"), language);
        };
    }

    public static Specification<ProblemArchive> hasCategory(ProblemCategory category) {
        return (Root<ProblemArchive> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction(); // No filter if language is null
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }
}