package br.com.cinestar.model;

public enum Genero {
    ACAO("Ação"),
    AVENTURA("Aventura"),
    COMEDIA("Comédia"),
    DRAMA("Drama"),
    FICCAO_CIENTIFICA("Ficção científica"),
    TERROR("Terror"),
    ROMANCE("Romance"),
    ANIMACAO("Animação"),
    DOCUMENTARIO("Documentário"),
    OUTRO("Outro");

    private final String descricao;

    Genero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
