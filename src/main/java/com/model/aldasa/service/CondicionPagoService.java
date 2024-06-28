package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;


import com.model.aldasa.entity.CondicionPago;

public interface CondicionPagoService {
 
   Optional<CondicionPago> findById(Integer id);
   CondicionPago save(CondicionPago entity);
   void delete(CondicionPago entity);
   
   List<CondicionPago> findByEstado(boolean estado);
  
}
