package com.example.coffeeshop.dto;




import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    // We will get the userId from the authenticated user,
    // so we only need the list of items.
    private List<CartItemDto> items;
}
