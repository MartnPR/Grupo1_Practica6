import java.util.List;

public interface IAlimentoDataHelper extends IDataHelperSQL<Alimento, Integer> {
    List<Alimento> buscarPorTipo(String categoria);
}
