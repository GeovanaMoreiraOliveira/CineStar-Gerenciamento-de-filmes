package br.com.cinestar.model;

public class Filme {
    private final int id;
    private String titulo;
    private String diretor;
    private int ano;
    private Genero genero;

    public Filme(int id, String titulo, String diretor, int ano, Genero genero) {
        this.id = id;
        this.titulo = titulo;
        this.diretor = diretor;
        this.ano = ano;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        String anoFormatado = ano > 0 ? String.valueOf(ano) : "ano não informado";
        return String.format(
                "Filme #%d | %s (%s)%n   Diretor: %s | Gênero: %s",
                id, titulo, anoFormatado, diretor, genero
        );
    }
}
