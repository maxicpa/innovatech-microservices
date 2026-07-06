package cl.innovatech.msproject.service;

import cl.innovatech.msproject.model.Proyecto;
import cl.innovatech.msproject.model.Tarea;
import cl.innovatech.msproject.repository.ProyectoRepository;
import cl.innovatech.msproject.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;

    public TareaService(TareaRepository tareaRepository, ProyectoRepository proyectoRepository) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public List<Tarea> listarPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId);
    }

    public Tarea guardar(Long proyectoId, Tarea tarea) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + proyectoId));
        tarea.setProyecto(proyecto);
        return tareaRepository.save(tarea);
    }

    public void eliminar(Long id) {
        tareaRepository.deleteById(id);
    }
}