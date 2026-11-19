package pkDataAccess.pkIDataAccess;

import pkDataAccess.pkDTO.UsuarioTipo;

public interface IUsuarioTipoDataHelper
        extends IDataHelperSQL<UsuarioTipo, String> {
    UsuarioTipo buscarPorDescripcion(String descripcion);
}
