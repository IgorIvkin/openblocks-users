package ru.openblocks.users.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.openblocks.users.persistence.entity.UserDataEntity;

import java.util.List;
import java.util.Locale;

/**
 * Спецификации для поиска по пользователям.
 */
public class UserSpecification {

    public static Specification<UserDataEntity> byLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(lastName)) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        lastName.toLowerCase(Locale.getDefault()) + "%"
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<UserDataEntity> byQueryWords(List<String> queryWords) {
        return (root, query, criteriaBuilder) -> {
            if (!CollectionUtils.isEmpty(queryWords)) {
                List<Predicate> allWordsPredicates =
                        queryWords.stream()
                                .map(word -> getPredicateAnyPartOfName(criteriaBuilder, root, word))
                                .toList();
                return criteriaBuilder.and(allWordsPredicates.toArray(new Predicate[0]));
            }
            return criteriaBuilder.conjunction();
        };
    }

    private static Predicate getPredicateAnyPartOfName(CriteriaBuilder criteriaBuilder,
                                                       Root<UserDataEntity> root,
                                                       String word) {
        return criteriaBuilder.or(
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        word.toLowerCase(Locale.getDefault()) + "%"),
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        word.toLowerCase(Locale.getDefault()) + "%"),
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("patronymicName")),
                        word.toLowerCase(Locale.getDefault()) + "%")
        );
    }
}
