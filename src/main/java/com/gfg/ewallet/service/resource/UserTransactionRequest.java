package com.gfg.ewallet.service.resource;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTransactionRequest {


    private Long receiverId;
    private Double amount;
    private String description;
}
