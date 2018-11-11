package de.berlin.lostberlin.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFullDao {
    private String name;
    private String phone;
    private String eMail;
    private LocalDate date;
    private String time;
    private long participantsNr;
    private String description;
    private Long businessId;
    private Enum status;
    private String orderNr;
}
