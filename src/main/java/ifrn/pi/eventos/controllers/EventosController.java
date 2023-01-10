package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.models.convidado;
import ifrn.pi.eventos.repositores.ConvidadoRepository;
import ifrn.pi.eventos.repositores.EventoRepository;

@Controller
@RequestMapping("/eventos")
public class EventosController {

	@Autowired
	private EventoRepository er;
	@Autowired
	private ConvidadoRepository cr;

	@RequestMapping("/form")
	public String form() {
		return "eventos/formEvento";
	}

	@PostMapping
	public String adicionar(Evento evento) {

		System.out.println(evento);
		er.save(evento);

		return "eventos/evento-adicionado";
	}

	@GetMapping
	public ModelAndView listar() {
		List<Evento> eventos = er.findAll();
		ModelAndView mv = new ModelAndView("eventos/lista");
		mv.addObject("eventos", eventos);
		return mv; 
	}

	@GetMapping("/{id}")
	public ModelAndView detalhar(@PathVariable Long id) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id); 
		if (opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}
		md.setViewName("eventos/detalhes");
		Evento evento = opt.get();
		md.addObject("evento", evento);

		List<convidado> convidados = cr.findByEvento(evento);
		md.addObject("convidados", convidados);
		return md;
	}
	
	@PostMapping("/{codigo}")
	public String salvarConvidado(@PathVariable Long codigo, ifrn.pi.eventos.models.convidado convidado) {
		System.out.println("Id do evento: " + codigo);
		System.out.println("convidado");
		
		Optional<Evento> opt = er.findById(codigo);
			if(opt.isEmpty()) {
				return "redirect:/eventos";
			}
			
			Evento evento = opt.get();
			convidado.setEvento(evento);
			
			cr.save(convidado);
		return "redirect:/eventos/{codigo}";
	}
	@GetMapping("/{id}/remover")
	public String apagarEvento(@PathVariable Long id) {
		
		Optional<Evento> opt = er.findById(id);
		if(!opt.isEmpty()) {
			Evento evento = opt.get();
			
			List<convidado> convidados = cr.findByEvento(evento);
			
			cr.deleteAll(convidados);
			
			er.delete(evento);
			
		}
		return "redirect:/eventos";
	}
}
