package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.Servi;

import java.util.List;

public interface ServiService {

    Servi createService(Servi servi);
    Servi updateService(Long serviId, Servi servi);
    void deleteService(Long serviId);
    List<Servi> getAllActiveServices();
    Servi getServiceById(Long serviId);

}
