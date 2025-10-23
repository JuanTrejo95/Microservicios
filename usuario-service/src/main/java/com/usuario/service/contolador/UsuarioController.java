package com.usuario.service.contolador;

import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usuario.service.entidades.Usuario;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;
import com.usuario.service.servicio.UsuarioService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/test")
    public String test() {
        return "Usuario Service activo y respondiendo";
    }
	
	@GetMapping
	public ResponseEntity<List<Usuario>> obtenUsuarios(){
		List<Usuario> usuarios = usuarioService.getAll();
		if(usuarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(usuarios);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> obtenerUsuario(@PathVariable int id){
		Usuario usuario = usuarioService.getUsuarioById(id);
		if(usuario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}
	
	@PostMapping
	public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario u){
		Usuario nuevoUsuario = usuarioService.save(u);
		return ResponseEntity.ok(nuevoUsuario); 
	}
	
	@CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackGetCarros")
	@GetMapping("/carros/{usuarioId}")
	public ResponseEntity<List<Carro>> listarCarros(@PathVariable int usuarioId){
		Usuario usuario = usuarioService.getUsuarioById(usuarioId);
		if(usuario == null) {
			return ResponseEntity.notFound().build();
		}
		List<Carro> carros = usuarioService.getCarros(usuarioId);
		return ResponseEntity.ok(carros);
	}
	
	@CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackGetMotos")
	@GetMapping("/motos/{usuarioId}")
	public ResponseEntity<List<Moto>> listarMotos(@PathVariable int usuarioId){
		Usuario usuario = usuarioService.getUsuarioById(usuarioId);
		if(usuario == null) {
			return ResponseEntity.notFound().build();
		}
		List<Moto> motos = usuarioService.getMotos(usuarioId);
		return ResponseEntity.ok(motos);
	}
	
	@CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackSaveCarro")
	@PostMapping("/carros/{usuarioId}")
	public ResponseEntity<Carro> guardarCarro(@PathVariable int usuarioId, @RequestBody Carro carro){
		Carro nuevoCarro = usuarioService.saveCarro(usuarioId, carro);
		return ResponseEntity.ok(nuevoCarro); 
	}
	
	@CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackSaveMoto")
	@PostMapping("/motos/{usuarioId}")
	public ResponseEntity<Moto> guardarMoto(@PathVariable int usuarioId, @RequestBody Moto moto){
		Moto nuevaMoto = usuarioService.saveMoto(usuarioId, moto);
		return ResponseEntity.ok(nuevaMoto); 
	}
	
	@CircuitBreaker(name = "todosCB", fallbackMethod = "fallBackGetTodos")
	@GetMapping("/todos/{usuarioId}")
	public ResponseEntity<Map<String, Object>> listarTodosLosVehiculos(@PathVariable int usuarioId){
		Map<String, Object> resultado = usuarioService.getUsuarioAndVehiculos(usuarioId);
		return ResponseEntity.ok(resultado);
	}
	
	private ResponseEntity<List<Carro>> fallBackGetCarros(@PathVariable("usuarioId") int id, RuntimeException excepcion){
		return new ResponseEntity("El usuario: " + id + " tiene los autos en el taller", null, HttpStatus.SC_OK);
	}
	
	private ResponseEntity<List<Carro>> fallBackSaveCarro(@PathVariable("usuarioId") int id, @RequestBody Carro carro, RuntimeException excepcion){
		return new ResponseEntity("El usuario: " + id + " no tiene dinero para comprar carro", null, HttpStatus.SC_OK);
	}
	
	private ResponseEntity<List<Moto>> fallBackGetMotos(@PathVariable("usuarioId") int id, RuntimeException excepcion){
		return new ResponseEntity("El usuario: " + id + " tiene las motos en el taller", null, HttpStatus.SC_OK);
	}
	
	private ResponseEntity<List<Moto>> fallBackSaveMoto(@PathVariable("usuarioId") int id, @RequestBody Moto moto, RuntimeException excepcion){
		return new ResponseEntity("El usuario: " + id + " no tiene dinero para comprar moto", null, HttpStatus.SC_OK);
	}
	
	private ResponseEntity<Map<String, Object>> fallBackGetTodos(@PathVariable("usuarioId") int id, RuntimeException excepcion){
		return new ResponseEntity("El usuario: " + id + " tiene las motos en el taller", null, HttpStatus.SC_OK);
	}
	
}
