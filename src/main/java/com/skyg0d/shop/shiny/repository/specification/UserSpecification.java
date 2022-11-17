package com.skyg0d.shop.shiny.repository.specification;

import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.search.UserParameterSearch;
import com.skyg0d.shop.shiny.util.RoleUtils;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class UserSpecification extends AbstractSpecification {

    public static Specification<User> getSpecification(UserParameterSearch search) {
        return where(withUsername(search.getUsername()))
                .and(where(withFullName(search.getFullName())))
                .and(where(withEmail(search.getEmail())))
                .and(where(withPhotoURL(search.getPhotoURL())))
                .and(where(withCountry(search.getCountry())))
                .and(where(withState(search.getState())))
                .and(where(withCity(search.getCity())))
                .and(where(withNeighborhood(search.getNeighborhood())))
                .and(where(withStreet(search.getStreet())))
                .and(where(withNumber(search.getNumber())))
                .and(where(withReferencePoint(search.getReferencePoint())))
                .and(where(withRole(search.getRole())))
                .and(where(withCreatedInDateOrAfter(search.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(search.getCreatedInDateOrBefore())));
    }

    public static Specification<User> withUsername(String username) {
        return like(username, "username");
    }

    public static Specification<User> withFullName(String fullName) {
        return like(fullName, "fullName");
    }

    public static Specification<User> withEmail(String email) {
        return like(email, "email");
    }

    public static Specification<User> withPhotoURL(String photoURL) {
        return like(photoURL, "photoURL");
    }

    public static Specification<User> withCountry(String country) {
        return likeJoinAddress(country, "country");
    }

    public static Specification<User> withState(String state) {
        return likeJoinAddress(state, "state");
    }

    public static Specification<User> withCity(String city) {
        return likeJoinAddress(city, "city");
    }

    public static Specification<User> withNeighborhood(String neighborhood) {
        return likeJoinAddress(neighborhood, "neighborhood");
    }

    public static Specification<User> withStreet(String street) {
        return likeJoinAddress(street, "street");
    }

    public static Specification<User> withNumber(String number) {
        return likeJoinAddress(number, "number");
    }

    public static Specification<User> withReferencePoint(String referencePoint) {
        return likeJoinAddress(referencePoint, "referencePoint");
    }

    public static Specification<User> withRole(String role) {
        ERole eRole = RoleUtils.getRoleByStringOrNull(role);

        return getSpec(role, (root, query, builder) -> (
                builder.equal(builder.lower(root.join("roles").get("name")), eRole)
        ));
    }

    private static Specification<User> likeJoinAddress(String string, String property) {
        return likeJoin("address", string, property);
    }

}
