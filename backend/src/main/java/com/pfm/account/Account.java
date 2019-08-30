package com.pfm.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.currency.Currency;
import com.pfm.history.HistoryField;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Access(AccessType.PROPERTY)
public final class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Account id (generated by application)", required = true, example = "1")
  private Long id;

  @ApiModelProperty(value = "Account name", required = true, example = "Alior Bank savings account")
  @HistoryField
  private String name;

  @ApiModelProperty(value = "Account's balance", required = true, example = "1438.89")
  @HistoryField
  private BigDecimal balance;

  @ManyToOne // TODO should not return all currency information, only id
  @ApiModelProperty(value = "Account's currency", required = true, example = "USD")
  private Currency currency;

  @ApiModelProperty(value = "Account's last verification date", example = "2019-01-31")
  private LocalDate lastVerificationDate;

  @JsonIgnore
  private Long userId;

  @HistoryField
  @ApiModelProperty(value = "Information if account is archived", example = "false")
  private boolean archived;

}
