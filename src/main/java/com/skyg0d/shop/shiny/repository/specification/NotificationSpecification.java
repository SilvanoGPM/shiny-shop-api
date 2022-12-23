package com.skyg0d.shop.shiny.repository.specification;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.payload.search.NotificationParameterSearch;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class NotificationSpecification extends AbstractSpecification {

    public static Specification<Notification> getSpecification(NotificationParameterSearch search) {
        return where(withContent(search.getContent()))
                .and(withCategory(search.getCategory()))
                .and(where(withReadInDateOrAfter(search.getReadInDateOrAfter())))
                .and(where(withReadInDateOrBefore(search.getReadInDateOrBefore())))
                .and(where(withCanceledInDateOrAfter(search.getCanceledInDateOrAfter())))
                .and(where(withCanceledInDateOrBefore(search.getCanceledInDateOrBefore())))
                .and(where(withUserEmail(search.getUserEmail())))
                .and(where(withCreatedInDateOrAfter(search.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(search.getCreatedInDateOrBefore())));
    }

    private static Specification<Notification> withContent(String content) {
        return like(content, "content");
    }

    private static Specification<Notification> withCategory(String category) {
        return like(category, "category");
    }

    private static Specification<Notification> withReadInDateOrAfter(String readInDateOrAfter) {
        return inDateOrAfter(readInDateOrAfter, "readAt");
    }

    private static Specification<Notification> withReadInDateOrBefore(String readInDateOrBefore) {
        return inDateOrBefore(readInDateOrBefore, "readAt");
    }

    private static Specification<Notification> withCanceledInDateOrAfter(String canceledInDateOrAfter) {
        return inDateOrAfter(canceledInDateOrAfter, "canceledAt");
    }

    private static Specification<Notification> withCanceledInDateOrBefore(String canceledInDateOrAfter) {
        return inDateOrBefore(canceledInDateOrAfter, "canceledAt");
    }

    private static Specification<Notification> withUserEmail(String userEmail) {
        return likeUser(userEmail, "email");
    }

    private static Specification<Notification> likeUser(String string, String property) {
        return likeJoin("user", string, property);
    }

}
