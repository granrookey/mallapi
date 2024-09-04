package org.glocol.mallapi.service;

import java.util.List;

import org.glocol.mallapi.dto.CartItemDTO;
import org.glocol.mallapi.dto.CartItemListDTO;

import jakarta.transaction.Transactional;

@Transactional
public interface CartService {
    public List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO);

    public List<CartItemListDTO> getCartItems(String email);
    
    public List<CartItemListDTO> remove(Long cino);
}
