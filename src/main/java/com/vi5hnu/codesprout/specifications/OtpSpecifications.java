package com.vi5hnu.codesprout.specifications;

import com.vi5hnu.codesprout.entity.user.OtpModel;
import com.vi5hnu.codesprout.enums.OtpReason;
import com.vi5hnu.codesprout.enums.OtpStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OtpSpecifications {
    public static Specification<OtpModel> getLatestActiveOtps(@NonNull String userId, OtpReason reason, OtpStatus status) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            // Order by createdAt DESC
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            // Build predicates
            Predicate userPredicate = criteriaBuilder.equal(root.get("userId"), userId);
            Predicate reasonPredicate = reason!=null ?  criteriaBuilder.equal(root.get("reason"), reason) : null;
            Predicate statusPredicate = status!=null ? criteriaBuilder.equal(root.get("status"), status) : null;

            // Add condition for expireAt > current timestamp
            Predicate expiryPredicate = criteriaBuilder.greaterThan(root.get("expireAt"), criteriaBuilder.currentTimestamp());

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(userPredicate);
            predicates.add(expiryPredicate);
            if (reasonPredicate != null) predicates.add(reasonPredicate);
            if (statusPredicate != null) predicates.add(statusPredicate);

            // Return combined predicates
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
