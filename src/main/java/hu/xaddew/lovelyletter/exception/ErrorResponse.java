package hu.xaddew.lovelyletter.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

  private String errorMessage;
  private Integer errorCode;
  private Integer errorSubCode;
  private String userTitle;
  private String userMessage;
  private String transactionId;
  private String nodeId;

}
