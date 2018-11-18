package de.berlin.lostberlin.model.business.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "businessProfile")

@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({ "id", "createdAt", "updatedAt" })
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Business implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

        private Long id;

        private String fName;

        private String lName;

        @Column (unique = true)
        private String eMail;

        private String phone;

        private String description;

        private String serviceLocation;

        private byte[] photo;

        private String username;

        private String password;


        @Column(nullable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @CreatedDate
        private Date createdAt;


        @Column(nullable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @LastModifiedDate
        private Date updatedAt;


    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
