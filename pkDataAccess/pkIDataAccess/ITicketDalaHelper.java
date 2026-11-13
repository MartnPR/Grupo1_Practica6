import java.util.List;

public interface ITicketDataHelper extends IDataHelperSQL<Ticket, Integer> {
    List<Ticket> buscarPorValido(boolean valido);
}
