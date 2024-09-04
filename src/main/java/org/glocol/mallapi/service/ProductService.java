package org.glocol.mallapi.service;

import org.glocol.mallapi.dto.PageRequestDTO;
import org.glocol.mallapi.dto.PageResponseDTO;
import org.glocol.mallapi.dto.ProductDTO;

public interface ProductService {
    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);
    Long register(ProductDTO productDTO);
    ProductDTO get(Long pno);
    void modify(ProductDTO productDTO);
    void remove(Long pno);
}
