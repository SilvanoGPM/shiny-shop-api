package com.skyg0d.shop.shiny.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class AbstractSpecification {

    protected static <T> Specification<T> withCreatedAt(String createdAt) {
        return getSpec(createdAt, (root, query, builder) -> (
                builder.equal(root.get("createdAt"), LocalDateTime.parse(createdAt))
        ));
    }

    protected static <T> Specification<T> withCreatedInDateOrAfter(String createdInDateOrAfter) {
        return inDateOrAfter(createdInDateOrAfter, "createdAt");
    }

    protected static <T> Specification<T> withCreatedInDateOrBefore(String createdInDateOrBefore) {
        return inDateOrBefore(createdInDateOrBefore, "createdAt");
    }

    protected static <T> Specification<T> inDateOrAfter(String date, String property) {
        return getSpec(date, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get(property), LocalDateTime.of(
                        LocalDate.parse(date),
                        LocalTime.MIN
                ))
        ));
    }

    protected static <T> Specification<T> inDateOrBefore(String date, String property) {
        return getSpec(date, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get(property), LocalDateTime.of(
                        LocalDate.parse(date),
                        LocalTime.MAX
                ))
        ));
    }

    protected static <T> Specification<T> withBoolean(int value, String property) {
        if (value != 0 && value != 1) return null;

        boolean isTrue = value == 1;

        return getSpec(isTrue, (root, query, builder) -> (
                builder.equal(root.get(property), isTrue)
        ));
    }


    protected static <T> Specification<T> greaterThanOrEqual(BigDecimal value, String property) {
        if (value.compareTo(BigDecimal.ZERO) < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> greaterThanOrEqual(double value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> greaterThanOrEqual(long value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThanOrEqual(BigDecimal value, String property) {
        if (value.compareTo(BigDecimal.ZERO) < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThanOrEqual(double value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThanOrEqual(long value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThanOrEqualTo(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> greaterThan(BigDecimal value, String property) {
        if (value.compareTo(BigDecimal.ZERO) < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> greaterThan(double value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> greaterThan(long value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.greaterThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThan(BigDecimal value, String property) {
        if (value.compareTo(BigDecimal.ZERO) < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThan(double value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> lessThan(long value, String property) {
        if (value < 0) return null;

        return getSpec(value, (root, query, builder) -> (
                builder.lessThan(root.get(property), value)
        ));
    }

    protected static <T> Specification<T> like(String string, String property) {
        string = string == null ? "" : string;

        String liked = "%" + string.toLowerCase() + "%";

        return getSpec(string, (root, query, builder) -> (
                builder.like(builder.lower(root.get(property)), liked)
        ));
    }

    protected static <T> Specification<T> likeJoin(String join, String string, String property) {
        string = string == null ? "" : string;

        String liked = "%" + string.toLowerCase() + "%";

        return getSpec(string, (root, query, builder) -> (
                builder.like(builder.lower(root.join(join).get(property)), liked)
        ));
    }

    protected static <T, E> Specification<E> getSpec(T value, Specification<E> spec) {
        if (value == null) {
            return null;
        }

        if (value instanceof String && ((String) value).isEmpty()) {
            return null;
        }

        return spec;
    }

}