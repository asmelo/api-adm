package persistence;

import models.PalavraChave;
import org.skife.jdbi.v2.BeanMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import play.db.Database;

import javax.inject.Inject;
import java.util.List;

public class PalavraChavePersistence {

    Database db;

    @Inject
    public PalavraChavePersistence(Database db) {
        this.db = db;
    }


    public List<PalavraChave> listaPalavraChave() {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<PalavraChave> listaPalavraChave = handle.createQuery("SELECT " +
                    "idPalavraChave \"id\", palavra, idSubcategoria " +
                    "from palavra_chave")
                    .map(new BeanMapper<>(PalavraChave.class))
                    .list();

            return listaPalavraChave;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
