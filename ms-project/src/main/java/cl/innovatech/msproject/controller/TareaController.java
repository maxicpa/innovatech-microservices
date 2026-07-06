package cl.innovatech.msproject.controller;

import cl.innovatech.msproject.model.Tarea;
import cl.innovatech.msproject.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proyectos/{proyectoId}/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listar(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(tareaService.listarPorProyecto(proyectoId));
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@PathVariable Long proyectoId, @Valid @RequestBody Tarea tarea) {
        Tarea nueva = tareaService.guardar(proyectoId, tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}