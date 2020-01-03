package o.horbenko.nnettysample.repository;

public class PersistanceDataException extends RuntimeException {

    public PersistanceDataException(Exception e) {
        super(e);
    }

    public PersistanceDataException(String s) {
        super(s);
    }
}
