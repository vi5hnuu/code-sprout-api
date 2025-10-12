package com.vi5hnu.codesprout.specifications;

import com.vi5hnu.codesprout.entity.File;
import com.vi5hnu.codesprout.entity.Folder;
import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.Visibility;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FileMgmtSpecification {
    public static Specification<Folder> getFoldersBy(
            String ownerId,
            String id,
            String name,
            String parentId,
            Visibility visibility,
            Boolean isDeleted
    ) {
        return (Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Default to public if not specified
            if (visibility != null) predicates.add(cb.equal(root.get("visibility"), visibility));

            // Default to false if not specified
            predicates.add(cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false));

            predicates.add(cb.equal(root.get("ownerId"), ownerId));
            if (id != null) predicates.add(cb.equal(root.get("id"), id));
            if (name != null) predicates.add(cb.equal(root.get("name"), name));
            if (parentId != null) predicates.add(cb.equal(root.get("parentId"), parentId));
            else predicates.add(cb.isNull(root.get("parentId")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Folder> getFoldersBy(
            String ownerId,
            String id,
            String name,
            Visibility visibility,
            Boolean isDeleted
    ) {
        return (Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Default to public if not specified
            if (visibility != null) predicates.add(cb.equal(root.get("visibility"), visibility));

            // Default to false if not specified
            predicates.add(cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false));

            predicates.add(cb.equal(root.get("ownerId"), ownerId));
            if (id != null) predicates.add(cb.equal(root.get("id"), id));
            if (name != null) predicates.add(cb.equal(root.get("name"), name));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<File> getFilesBy(
            String ownerId,
            String id,
            String name,
            String folderId,
            Visibility visibility,
            List<FileAccess> accesses,
            Boolean isDeleted
    ) {
        return (Root<File> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Default to public if not specified
            predicates.add(cb.equal(root.get("visibility"), visibility!=null ? visibility : Visibility.PUBLIC));

            // Default to false if not specified
            predicates.add(cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false));

            predicates.add(cb.equal(root.get("ownerId"), ownerId));
            if (id != null) predicates.add(cb.equal(root.get("id"), id));
            if (name != null) predicates.add(cb.equal(root.get("name"), name));
            if(accesses!=null && !accesses.isEmpty()) predicates.add(root.get("access").in(accesses));
            if (folderId != null) predicates.add(cb.equal(root.get("folderId"), folderId));
            else predicates.add(cb.isNull(root.get("folderId")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<File> getFilesBy(
            String ownerId,
            String id,
            String name,
            Visibility visibility,
            Boolean isDeleted
    ) {
        return (Root<File> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Default to public if not specified
            predicates.add(cb.equal(root.get("visibility"), visibility!=null ? visibility : Visibility.PUBLIC));

            // Default to false if not specified
            predicates.add(cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false));

            predicates.add(cb.equal(root.get("ownerId"), ownerId));
            if (id != null) predicates.add(cb.equal(root.get("id"), id));
            if (name != null) predicates.add(cb.equal(root.get("name"), name));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}