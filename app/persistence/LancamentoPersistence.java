package persistence;

import models.ChaveValor;
import models.Lancamento;
import models.PalavraChave;
import org.skife.jdbi.v2.BeanMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import play.db.Database;

import javax.inject.Inject;
import java.util.List;

public class LancamentoPersistence {

    Database db;

    @Inject
    public LancamentoPersistence(Database db) {
        this.db = db;
    }


    public int salvarLancamento(Lancamento lancamento) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            handle.execute("INSERT INTO lancamento(idConta, cdTipo, data, descricao, valor, idSubcategoria, referenciaOfx) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    lancamento.getIdConta(),
                    lancamento.getCdTipo(),
                    lancamento.getDataCompleta(),
                    lancamento.getDescricao(),
                    lancamento.getValor(),
                    lancamento.getSubcategoria(),
                    lancamento.getReferenciaOfx());

            Integer idLancamento = handle.createQuery("SELECT max(idLancamento) \"idLancamento\" FROM financeiro.lancamento")
                                .mapTo(Integer.class)
                                .first();

            return idLancamento;

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void excluirLancamento(int idLancamento) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            handle.execute("DELETE FROM lancamento WHERE idLancamento = ?", idLancamento);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public void atualizarLancamento(int idLancamento, Lancamento lancamento) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            handle.execute("UPDATE lancamento SET idConta = ?, cdTipo = ?, data = ?, descricao = ?, valor = ?, idSubcategoria = ? WHERE idLancamento = ?",
                    lancamento.getIdConta(),
                    lancamento.getCdTipo(),
                    lancamento.getDataCompleta(),
                    lancamento.getDescricao(),
                    lancamento.getValor(),
                    lancamento.getSubcategoria(),
                    idLancamento);

        } catch (Exception ex) {
            throw ex;
        }
    }

    public Lancamento consultaLancamento(int idLancamento) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            Lancamento lancamento = handle.createQuery("SELECT " +
                    "idLancamento \"id\", idConta, cdTipo, data \"dataCompleta\", descricao, valor, idSubcategoria \"subcategoria\", referenciaOfx " +
                    "FROM lancamento WHERE idLancamento = :idLancamento ")
                    .bind("idLancamento", idLancamento)
                    .map(new BeanMapper<>(Lancamento.class))
                    .first();

            return lancamento;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Lancamento verificaReferenciaOfx(String referenciaOfx, String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            Lancamento lancamento = handle.createQuery("SELECT " +
                    "idLancamento \"id\", idConta, cdTipo, data \"dataCompleta\", descricao, valor, idSubcategoria \"subcategoria\", referenciaOfx " +
                    "FROM lancamento WHERE referenciaOfx = :referenciaOfx and YEAR(data) = :ano AND MONTH(data) = :mes " +
                    "order by data")
                    .bind("referenciaOfx", referenciaOfx)
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(Lancamento.class))
                    .first();

            return lancamento;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<Lancamento> listaLancamentos(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<Lancamento> listaLancamentos = handle.createQuery("SELECT " +
                    "idLancamento \"id\", idConta, cdTipo, data \"dataCompleta\", descricao, valor, idSubcategoria \"subcategoria\", referenciaOfx " +
                    "FROM lancamento WHERE YEAR(data) = :ano AND MONTH(data) = :mes")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(Lancamento.class))
                    .list();

            return listaLancamentos;
        } catch (Exception ex) {
            throw ex;
        }
    }


    /*
        Consulta dos relatórios
     */

    public List<ChaveValor> listaSalarios(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaSalarios = handle.createQuery("SELECT " +
                    "        S.descricao chave, SUM(L.valor) valor " +
                    "    FROM " +
                    "        lancamento L " +
                    "            JOIN " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria " +
                    "            JOIN " +
                    "        categoria C ON S.idCategoria = C.idCategoria " +
                    "    WHERE " +
                    "        YEAR(L.data) = :ano " +
                    "    AND MONTH(L.data) = :mes " +
                    "            AND C.nome = 'Salário' " +
                    "    GROUP BY S.descricao " +
                    "    ORDER BY valor DESC")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaSalarios;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ChaveValor> listaSubcategoriasMonitoramento(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaSubcategorias = handle.createQuery("SELECT " +
                    "        S.descricao chave, SUM(L.valor * -1) valor " +
                    "    FROM " +
                    "        lancamento L " +
                    "            JOIN " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria " +
                    "    WHERE " +
                    "        YEAR(L.data) = :ano AND MONTH(L.data) = :mes AND L.cdTipo = 2 " +
                    "            AND S.cdtipo = 1" +
                    "    GROUP BY S.descricao " +
                    "    ORDER BY valor DESC")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaSubcategorias;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ChaveValor> listaSubcategorias(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaSubcategorias = handle.createQuery("SELECT " +
                    "        S.descricao chave, SUM(L.valor * -1) valor " +
                    "    FROM " +
                    "        lancamento L " +
                    "            JOIN " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria " +
                    "    WHERE " +
                    "        YEAR(L.data) = :ano AND MONTH(L.data) = :mes AND L.cdTipo = 2 " +
                    "            AND L.idsubcategoria <> 31 " +
                    "    GROUP BY S.descricao " +
                    "    ORDER BY valor DESC")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaSubcategorias;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ChaveValor> listaCategorias(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaCategorias = handle.createQuery("SELECT " +
                    "        C.nome chave, SUM(L.valor * -1) valor " +
                    "    FROM " +
                    "        lancamento L " +
                    "            JOIN " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria " +
                    "            JOIN " +
                    "        categoria C ON S.idCategoria = C.idCategoria " +
                    "    WHERE " +
                    "        YEAR(L.data) = :ano AND MONTH(L.data) = :mes AND L.cdTipo = 2 " +
                    "            AND L.idsubcategoria <> 31 " +
                    "    GROUP BY C.nome " +
                    "    ORDER BY valor DESC")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaCategorias;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ChaveValor> listaEntradas(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaEntradas = handle.createQuery("SELECT  " +
                    "        S.descricao chave, SUM(L.valor) valor " +
                    "    FROM  " +
                    "        lancamento L  " +
                    "            JOIN  " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria  " +
                    "    WHERE  " +
                    "        YEAR(L.data) = :ano AND MONTH(L.data) = :mes AND L.cdTipo = 1  " +
                    "            AND L.idsubcategoria <> 31  " +
                    "    GROUP BY S.descricao  " +
                    "    ORDER BY S.descricao")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaEntradas;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ChaveValor> listaTransferencias(String ano, String mes) {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<ChaveValor> listaTransferencias = handle.createQuery("SELECT " +
                    "        S.descricao chave, L.valor " +
                    "    FROM " +
                    "        lancamento L " +
                    "            JOIN " +
                    "        subcategoria S ON L.idsubcategoria = S.idSubcategoria " +
                    "    WHERE " +
                    "        YEAR(L.data) = :ano AND MONTH(L.data) = :mes " +
                    "            AND L.idsubcategoria = 31 " +
                    "    ORDER BY L.cdtipo, S.descricao")
                    .bind("ano", ano)
                    .bind("mes", mes)
                    .map(new BeanMapper<>(ChaveValor.class))
                    .list();

            return listaTransferencias;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
