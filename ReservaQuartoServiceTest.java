package br.com.hotel.cancun.sistema.service;

import static org.mockito.ArgumentMatchers.any;

import br.com.hotel.cancun.sistema.form.ReservaForm;
import br.com.hotel.cancun.sistema.model.Quarto;
import br.com.hotel.cancun.sistema.model.Reserva;
import br.com.hotel.cancun.sistema.model.Usuario;
import br.com.hotel.cancun.sistema.repository.QuartoRepository;
import br.com.hotel.cancun.sistema.repository.ReservaRepository;
import br.com.hotel.cancun.sistema.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

class ReservarQuartoServiceTest {

    @InjectMocks
    ReservarQuartoService reservarQuartoService;

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    QuartoRepository quartoRepository;
    @Mock
    ReservaRepository reservaRepository;

    @Mock
    private ReservaForm reservaForm;

    @BeforeEach
    private void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.reservarQuartoService = new ReservarQuartoService(usuarioRepository, quartoRepository, reservaRepository);
    }

    @Test
    void when_have_stimulus_create_success_book() {
        Mockito.when(usuarioRepository.getReferenceById(getReservaForm().getIdUsuario())).thenReturn(getUsuario());
        Mockito.when(quartoRepository.getReferenceById(getReservaForm().getIdQuarto())).thenReturn(getQuarto());
        Mockito.when(reservaRepository.save(any(Reserva.class))).thenReturn(getReservaStub());

        var relevante = reservarQuartoService.reservarQuarto(getReservaForm());


        Mockito.verify(quartoRepository).save(any(Quarto.class));
        Assertions.assertTrue(relevante.getQuarto().getReservas().contains(relevante));
    }


    private Usuario getUsuario() {
        var usuario = new Usuario();
        usuario.setId(0L);
        usuario.setNome("alisson");
        usuario.setCpf("3522");

        return usuario;
    }

    private Quarto getQuarto() {
        var quarto = new Quarto();
        List<Reserva> lista = new ArrayList<>();
        quarto.setId(0L);
        quarto.setNome("301");
        quarto.setReservas(lista);

        return quarto;
    }

    private List<Reserva> getListReserva() {
        List<Reserva> list = new ArrayList<>();
        var amanha = LocalDate.now().plusDays(1);
        list.add(new Reserva(getUsuario(), getQuarto(), LocalDate.now(), amanha));
        return list;
    }

    private ReservaForm getReservaForm() {
        var amanha = LocalDate.now().plusDays(1);
        return new ReservaForm(getQuarto().getId(), getUsuario().getId(), LocalDate.now(), amanha);
    }

    private Reserva getReserva() {
        var amanha = LocalDate.now().plusDays(1);
        var reserva = new Reserva(getUsuario(), getQuarto(), LocalDate.now(), amanha);
        reserva.setId(1L);
        return reserva;
    }

    private Reserva getReservaStub() {
        var amanha = LocalDate.now().plusDays(1);
        List<Reserva> listaDeReserva = new ArrayList<>();
        var quarto = getQuarto();
        quarto.setReservas(listaDeReserva);

        var reserva = new Reserva(getUsuario(), quarto, LocalDate.now(), amanha);
        listaDeReserva.add(reserva);
        return reserva;
    }

}
