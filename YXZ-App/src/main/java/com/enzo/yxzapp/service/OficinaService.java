package com.enzo.yxzapp.service;

import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.dto.oficina.UpdateViaModalRequest;


public interface OficinaService {
    OficinaResponse create(CreateOficinaRequest req);
    OficinaResponse update(Long id, UpdateViaModalRequest req);
}
