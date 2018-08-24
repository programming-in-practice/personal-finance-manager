package com.pfm.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Account id (generated by application)", required = true, example = "1")
  @Column(columnDefinition = "INTEGER")
  private Long id;

  @ApiModelProperty(value = "Account name", required = true, example = "Alior Bank savings account")
  private String name;

  @ApiModelProperty(value = "Account's balance", required = true, example = "1438.89")
  private BigDecimal balance;
}