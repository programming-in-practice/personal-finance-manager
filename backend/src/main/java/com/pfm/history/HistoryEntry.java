package com.pfm.history;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class HistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "HistoryEntry id (generated by application)", required = true)
  private Long id;

  @ApiModelProperty(value = "Time event happened", required = true)
  private LocalDateTime date;

  @ApiModelProperty(value = "HistoryEntry entry", required = true)
  @ElementCollection
  private List<String> entry;

  private Long userId;
}