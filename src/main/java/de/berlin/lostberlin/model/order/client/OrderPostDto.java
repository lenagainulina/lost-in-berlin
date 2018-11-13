package de.berlin.lostberlin.model.order.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPostDto {
    @NotNull
    private Long[] chosenBusinessIds;
    @NotBlank
    private String name;
    private String phone;
    @NotBlank
    @Email
    private String eMail;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String time;
    private long participantsNr;
    private String description;
}

