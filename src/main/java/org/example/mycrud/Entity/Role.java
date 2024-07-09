package org.example.mycrud.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id")
    private Integer roleId;

    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy = "role")
//    public Set<UserRole> userRole;


}