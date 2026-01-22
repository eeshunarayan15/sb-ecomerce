package com.ecommerce.sbecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder
@Table(
        name = "users"

)
public class User extends BaseModel implements UserDetails {

//    @NotBlank
//    @Size(min = 3, max = 50)
//    @Column(nullable = false)
//    private String username;

    //    @NotBlank
//    @Size(min = 8)
//    @Column(nullable = false)
    private String password;

    //    @NotBlank
//    @Email
//    @Column(nullable = false)
    private String email;
    //
//    @Size(max = 50)
    private String firstName;

    //    @Size(max = 50)
    private String lastName;
    private String phone;

    //    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
// Remove the old @OneToOne with address and replace with:
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    private String imageUrl;

    @Column(nullable = false)
    private boolean enabled = true;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider = Provider.LOCAL;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @ToString.Exclude // Prevent circular dependency in ToString
    @JsonManagedReference // Jackson ko pata chalega ki ye parent side hai
    private List<RefreshToken> refreshTokens=new ArrayList<>();

//    @OneToOne(mappedBy = "user")
//    @JsonIgnore
//    private Cart cart;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
