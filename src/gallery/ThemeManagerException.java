package gallery;

/**
 * ThemeManager가 메소드를 수행하는 도중
 * 문제가 있을 때 발생하는 예외 클래스.
 */
public class ThemeManagerException extends Exception {
    public ThemeManagerException(String msg) {
        super(msg);
    }
    public ThemeManagerException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
