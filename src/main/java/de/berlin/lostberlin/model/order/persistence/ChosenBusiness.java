package de.berlin.lostberlin.model.order.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import reactor.util.annotation.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "chosenBusiness")
@EntityListeners(AuditingEntityListener.class)

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChosenBusiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long businessId;
    @NotNull
    private String orderNr;
}
