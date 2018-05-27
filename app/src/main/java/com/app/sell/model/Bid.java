package com.app.sell.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bid {

    String id;
    String offerId;
    String bidderId;
    Double bid;
    Long timestamp;
}
