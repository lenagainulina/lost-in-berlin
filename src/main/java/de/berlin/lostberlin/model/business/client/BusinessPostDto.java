package de.berlin.lostberlin.model.business.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessPostDto {
    @NotBlank
    private String fName;
    @NotBlank
    private String lName;

    @NotBlank
    @Email
    private String eMail;

    private String phone;

    private String description;

    private String serviceLocation;

    private byte[] photo;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}