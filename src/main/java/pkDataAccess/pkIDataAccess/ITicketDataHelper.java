package pkDataAccess.pkIDataAccess;

import java.util.List;

import pkDataAccess.pkDTO.Ticket;

public interface ITicketDataHelper extends IDataHelperSQL<Ticket, Integer> {
    List<Ticket> buscarPorValido(boolean valido);
}
