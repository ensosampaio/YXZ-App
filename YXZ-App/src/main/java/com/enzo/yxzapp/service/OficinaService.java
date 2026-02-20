package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;
import org.springframework.data.domain.Pageable;


public interface OficinaService {
    OficinaResponse create(CreateOficinaRequest req);
    OficinaResponse update(Long id, UpdateViaModalRequest req);
    PageResponse<OficinaResponse> list(Pageable pageable);
    OficinaResponse getById(Long id);
}
