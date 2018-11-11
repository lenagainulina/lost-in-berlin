package de.berlin.lostberlin.model.Business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessShortDao {
    private String fName;

    private String lName;

    private String description;

    private String serviceLocation;

    private byte[] photo;
}
