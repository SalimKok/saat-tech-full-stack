package com.saattech.specification.builder;

import com.saattech.entity.Content;
import com.saattech.enums.ContentStatus;
import com.saattech.specification.dto.ContentFilterDto;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ContentSpecificationBuilder {

    public static Specification<Content> build(ContentFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            } else {
                predicates.add(criteriaBuilder.notEqual(root.get("status"), ContentStatus.DELETED));
            }

            if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("metadata").get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }

            if (filter.getContentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("contentType"), filter.getContentType()));
            }


            if (filter.getGenre() != null && !filter.getGenre().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("metadata").get("genre")),
                        "%" + filter.getGenre().toLowerCase().trim() + "%"));
            }

            if (filter.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.join("metadata").get("imdbRating"),
                        filter.getMinRating()));
            }

            if (filter.getYear() != null) {
                LocalDate startOfYear = LocalDate.of(filter.getYear(), 1, 1);
                LocalDate endOfYear = LocalDate.of(filter.getYear(), 12, 31);
                predicates.add(criteriaBuilder.between(
                        root.join("metadata").get("released"),
                        startOfYear,
                        endOfYear));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}