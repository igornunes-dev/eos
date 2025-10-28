package ${package};

/**
 * Enum ${className} gerado automaticamente.
 * Adicione atributos, construtores e métodos conforme necessário.
 */
public enum ${className} {
    // TODO: adicionar valores do enum

    ;

    // Exemplo de atributos opcionais
    private final int code;
    private final String description;

    ${className}() {
        this.code = 0;
        this.description = "";
    }

    ${className}(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static ${className} fromCode(int code) { ... }
    public static ${className} fromDescription(String description) { ... }
}
