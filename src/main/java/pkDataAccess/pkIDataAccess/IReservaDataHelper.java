package pkDataAccess.pkIDataAccess;

import java.util.Date;
import java.util.List;

import pkDataAccess.pkDTO.Reserva;

public interface IReservaDataHelper extends IDataHelperSQL<Reserva, Integer> {
    List<Reserva> buscarPorFecha(Date fechaCreacion);
    List<Reserva> buscarPorEstudiante(String codigoUnico);
    List<Reserva> buscarPorFranjaHoraria(String franjaHoraria);
    List<Reserva> buscarPorProfesor(String cedula);
}
