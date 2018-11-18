package de.berlin.lostberlin.model.business.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUpdateDto {

    private String fName;

    private String lName;

    @Email
    private String eMail;

    private String phone;

    private String description;

    private String serviceLocation;

    private byte[] photo;

    private String username;

    private String password;


}
