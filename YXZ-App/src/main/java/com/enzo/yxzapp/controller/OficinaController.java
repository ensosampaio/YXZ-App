package com.enzo.yxzapp.controller;

import com.enzo.yxzapp.dto.common.PageResponse;
import com.enzo.yxzapp.dto.oficina.CreateOficinaRequest;
import com.enzo.yxzapp.dto.oficina.OficinaResponse;
import com.enzo.yxzapp.model.Oficina;
import org.springframework.data.domain.Page;

public class OficinaController {
    private final OficinaService oficinaService;

    public OficinaController(OficinaService oficinaService){
        this.oficinaService = oficinaService;
    }

    public PageResponse<OficinaResponse> list(){
        return oficinaService.list();
    }

    public OficinaResponse create(CreateOficinaRequest request){
        return oficinaService.create(request);
    }

    public OficinaResponse update(Long id, CreateOficinaRequest request){
        return oficinaService.update(id,request);
    }


}
