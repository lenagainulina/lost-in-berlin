package de.berlin.lostberlin.model.order.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderShortDao {
    private String name;
    private LocalDate date;
    private String time;
    private long participantsNr;
    private String description;
}
