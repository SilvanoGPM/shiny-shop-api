package com.skyg0d.shop.shiny.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Username of user")
    private String username;

    @NotBlank
    @JsonProperty("full_name")
    @Schema(description = "Full name of user")
    private String fullName;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Email of user")
    private String email;

    @Embedded
    @Schema(description = "Username of user")
    private Address address;

    @Schema(description = "Photo image of user")
    private String photoURL;

    @NotBlank
    @Size(max = 150)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Password of user")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Schema(description = "Permissions of user")
    private Set<Role> roles = new HashSet<>();

    @Schema(description = "Id of customer in stripe")
    private String customerId;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
