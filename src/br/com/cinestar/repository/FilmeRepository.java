package br.com.cinestar.repository;

import br.com.cinestar.model.Filme;
import br.com.cinestar.model.Genero;

import java.io.IOException;
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FilmeRepository {
    private final List<Filme> filmes = new ArrayList<>();
    private final Path arquivo = Paths.get("filmes.txt");
    private int proximoId = 1;

    public void carregar() throws IOException {
        if (Files.exists(arquivo)) {
            carregarDoArquivo();
            return;
        }

        filmes.add(new Filme(1, "Interestelar", "Christopher Nolan", 2014,
                Genero.FICCAO_CIENTIFICA));
        filmes.add(new Filme(2, "Toy Story", "John Lasseter", 1995, Genero.ANIMACAO));
        filmes.add(new Filme(3, "Titanic", "James Cameron", 1997, Genero.ROMANCE));
        filmes.add(new Filme(4, "O Poderoso Chefão", "Francis Ford Coppola", 1972,
                Genero.DRAMA));
        filmes.add(new Filme(5, "Minha Melhor Amiga", "Susana Garcia", 2026, Genero.COMEDIA));
        proximoId = 6;
        try {
            salvarNoArquivo();
        } catch (IOException e) {
            filmes.clear();
            proximoId = 1;
            throw e;
        }
    }

    public Filme salvar(String titulo, String diretor, int ano, Genero genero) throws IOException {
        Filme filme = new Filme(proximoId, titulo, diretor, ano, genero);
        filmes.add(filme);
        try {
            salvarNoArquivo();
        } catch (IOException e) {
            filmes.remove(filme);
            throw e;
        }
        proximoId++;
        return filme;
    }

    public List<Filme> listarTodos() {
        return new ArrayList<>(filmes);
    }

    public Filme buscarPorId(int id) {
        for (Filme filme : filmes) {
            if (filme.getId() == id) {
                return filme;
            }
        }
        return null;
    }

    public List<Filme> buscarPorTitulo(String titulo) {
        List<Filme> encontrados = new ArrayList<>();
        String termo = normalizarParaBusca(titulo);

        for (Filme filme : filmes) {
            if (normalizarParaBusca(filme.getTitulo()).contains(termo)) {
                encontrados.add(filme);
            }
        }
        return encontrados;
    }

    private String normalizarParaBusca(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }

    public List<Filme> buscarPorGenero(Genero genero) {
        List<Filme> encontrados = new ArrayList<>();
        for (Filme filme : filmes) {
            if (filme.getGenero() == genero) {
                encontrados.add(filme);
            }
        }
        return encontrados;
    }

    public void atualizar(int id, String titulo, String diretor, int ano, Genero genero)
            throws IOException {
        Filme filme = buscarPorId(id);
        if (filme == null) {
            throw new IllegalArgumentException("Não existe filme com o ID " + id + ".");
        }

        String tituloAnterior = filme.getTitulo();
        String diretorAnterior = filme.getDiretor();
        int anoAnterior = filme.getAno();
        Genero generoAnterior = filme.getGenero();

        filme.setTitulo(titulo);
        filme.setDiretor(diretor);
        filme.setAno(ano);
        filme.setGenero(genero);
        try {
            salvarNoArquivo();
        } catch (IOException e) {
            filme.setTitulo(tituloAnterior);
            filme.setDiretor(diretorAnterior);
            filme.setAno(anoAnterior);
            filme.setGenero(generoAnterior);
            throw e;
        }
    }

    public boolean excluir(int id) throws IOException {
        Filme filme = buscarPorId(id);
        if (filme == null) {
            return false;
        }

        int indice = filmes.indexOf(filme);
        filmes.remove(indice);
        try {
            salvarNoArquivo();
        } catch (IOException e) {
            filmes.add(indice, filme);
            throw e;
        }
        return true;
    }

    private void carregarDoArquivo() throws IOException {
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        Set<Integer> ids = new HashSet<>();
        int maiorId = 0;

        for (int i = 0; i < linhas.size(); i++) {
            String[] campos = linhas.get(i).split("\t", -1);
            if (campos.length != 5) {
                throw erroDeFormato(i + 1, null);
            }

            try {
                int id = Integer.parseInt(campos[0]);
                String titulo = decodificarCampo(campos[1]);
                String diretor = decodificarCampo(campos[2]);
                int ano = Integer.parseInt(campos[3]);
                Genero genero = Genero.valueOf(campos[4]);

                if (id < 1 || titulo.isEmpty() || diretor.isEmpty() || !ids.add(id)) {
                    throw erroDeFormato(i + 1, null);
                }

                filmes.add(new Filme(id, titulo, diretor, ano, genero));
                maiorId = Math.max(maiorId, id);
            } catch (IllegalArgumentException | IOException e) {
                throw erroDeFormato(i + 1, e);
            }
        }

        proximoId = maiorId + 1;
    }

    private void salvarNoArquivo() throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Filme filme : filmes) {
            linhas.add(filme.getId() + "\t"
                    + codificarCampo(filme.getTitulo()) + "\t"
                    + codificarCampo(filme.getDiretor()) + "\t"
                    + filme.getAno() + "\t"
                    + filme.getGenero().name());
        }

        Path caminho = arquivo.toAbsolutePath();
        Path temporario = Files.createTempFile(caminho.getParent(), "filmes-", ".tmp");
        try {
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, caminho, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, caminho, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    private String codificarCampo(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String decodificarCampo(String valor) throws IOException {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char caractere = valor.charAt(i);
            if (caractere != '\\') {
                resultado.append(caractere);
                continue;
            }

            if (++i >= valor.length()) {
                throw new IOException("Sequência de escape incompleta.");
            }
            switch (valor.charAt(i)) {
                case '\\':
                    resultado.append('\\');
                    break;
                case 't':
                    resultado.append('\t');
                    break;
                case 'n':
                    resultado.append('\n');
                    break;
                case 'r':
                    resultado.append('\r');
                    break;
                default:
                    throw new IOException("Sequência de escape inválida.");
            }
        }
        return resultado.toString();
    }

    private IOException erroDeFormato(int numeroLinha, Exception causa) {
        String mensagem = "Erro no arquivo de filmes.txt, linha " + numeroLinha
                + ". Verifique os dados antes de continuar.";
        return causa == null ? new IOException(mensagem) : new IOException(mensagem, causa);
    }
}
