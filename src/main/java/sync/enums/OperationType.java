package sync.enums;

public enum OperationType {

    DEBIT("Debit"),
    CREDIT("Credit");

    private final String label;

    OperationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
