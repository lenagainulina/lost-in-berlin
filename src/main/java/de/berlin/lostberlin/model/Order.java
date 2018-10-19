package de.berlin.lostberlin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "orderProfile")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_num")
    @GenericGenerator(
            name = "order_num",
            strategy = "de.berlin.lostberlin.model.DatePrefixedSequenceIdGenerator",
            parameters = {@Parameter(name = DatePrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50")}
    )
    private String orderNr;


    private String businessId;

        private String status;
        @NotBlank
        private String name;
        private String phone;
        @NotBlank
        private String eMail;
        private Date date;
        private String time;
        private long participantsNr;
        private String description;

        @Column(nullable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @CreatedDate
        private Date createdAt;

        @Column(nullable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @LastModifiedDate
        private Date updatedAt;

    public Order() {
    }


    public Order(String orderNr, String businessId, String status, String name, String phone, String eMail, Date date, String time, long participantsNr, String description) {
        this.orderNr = orderNr;
        this.businessId = businessId;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.eMail = eMail;
        this.date = date;
        this.time = time;
        this.participantsNr = participantsNr;
        this.description = description;
    }

    public String getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(String orderNr) {
        this.orderNr = orderNr;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getParticipantsNr() {
        return participantsNr;
    }

    public void setParticipantsNr(long participantsNr) {
        this.participantsNr = participantsNr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNr='" + orderNr + '\'' +
                ", businessId='" + businessId + '\'' +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", eMail='" + eMail + '\'' +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", participantsNr=" + participantsNr +
                ", description='" + description + '\'' +
                '}';
    }
}
