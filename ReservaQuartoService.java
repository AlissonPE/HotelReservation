package br.com.hotel.cancun.sistema.service;

import br.com.hotel.cancun.sistema.form.ReservaForm;
import br.com.hotel.cancun.sistema.model.Quarto;
import br.com.hotel.cancun.sistema.model.Reserva;
import br.com.hotel.cancun.sistema.repository.QuartoRepository;
import br.com.hotel.cancun.sistema.repository.ReservaRepository;
import br.com.hotel.cancun.sistema.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservarQuartoService {

    private final UsuarioRepository usuarioRepository;
    private final QuartoRepository quartoRepository;
    @Getter
    private final ReservaRepository reservaRepository;

    public Reserva reservarQuarto(ReservaForm reservaForm) {

        var usuario = usuarioRepository.getReferenceById(reservaForm.getIdUsuario());
        if (Optional.ofNullable(usuario).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario não encontrado!");
        }
        var quarto =
                quartoRepository.getReferenceById(reservaForm.getIdQuarto());
        if (Optional.ofNullable(quarto).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quarto não encontrado!");
        }

        final LocalDate MAX_DAYS_BOOK = reservaForm.getDataReserva().plusDays(3);
        var reserva = List.of(reservaForm.getDataReserva(), reservaForm.getDataSaida());


        if (reservaForm.getDataReserva().isAfter(LocalDate.now().plusDays(30))) {
            throw new IllegalArgumentException("Não podemos reservar com mais de 30 dias de antecedência.");
        } else if (reservaForm.getDataSaida().isAfter(MAX_DAYS_BOOK)) {
            throw new IllegalArgumentException("A estadia não pode ser superior a 3 dias ");
        } else if (reservaForm.getDataReserva().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Todas as reservas começam pelo menos no dia seguinte à reserva.");
        }
        isValidToBook(quarto, reserva);

        var newReserva = new Reserva(usuario, quarto, reserva.get(0), reserva.get(1));
        var reservaSalvada = reservaRepository.save(newReserva);
        quarto.getReservas().add(reservaSalvada);
        quartoRepository.save(quarto);


        return reservaSalvada;
    }

    boolean isValidCompareTwoDates(List<LocalDate> reservaConferir, Reserva reservaReservada) {

        // Retorna true caso todas as validações forem true.
        return (reservaConferir.get(0).isBefore(reservaReservada.getDataReserva()) || reservaConferir.get(0).isAfter(reservaReservada.getDataSaida()))
                // reserva é ANTES entrada OU reserva é DEPOIS saida
                &&
                (reservaConferir.get(1).isBefore(reservaReservada.getDataReserva()) ||
                        reservaConferir.get(1).isAfter(reservaReservada.getDataSaida()))
                // saida é ANTES entrada OU saida é DEPOIS da saida
                &&
                (!reservaConferir.get(0).isEqual(reservaReservada.getDataReserva()) &&
                        !reservaConferir.get(0).isEqual(reservaReservada.getDataSaida()))
                // entrada não é IGUAL a entrada OU entrada não é igual a saida
                &&
                (!reservaConferir.get(1).isEqual(reservaReservada.getDataReserva()) ||
                        !reservaConferir.get(1).isEqual(reservaReservada.getDataSaida()));
        // saida não é IGUAL a entrada ou saida não é IGUAL a saida.
    }

    boolean isValidToBook(Quarto quarto, List<LocalDate> reservaConferir) {
        for (Reserva reserva : quarto.getReservas()) {
            // Para cada reserva em lista de reservas
            if (!isValidCompareTwoDates(reservaConferir, reserva)) {
                // Se não for valido para reserva, jogue uma exception.
                throw new IllegalArgumentException("Nessa data o quarto já está ocupado!");
            }
        }
        return true;
    }
}
