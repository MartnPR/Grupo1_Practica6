package pkDataAccess.pkIDataAccess;

import pkDataAccess.pkDTO.UsuarioTipoDTO;

public interface IUsuarioTipoDataHelper
        extends IDataHelperSQL<UsuarioTipoDTO, String> {
    UsuarioTipoDTO buscarPorDescripcion(String descripcion);
}
