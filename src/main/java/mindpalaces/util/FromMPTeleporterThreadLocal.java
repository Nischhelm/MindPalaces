package mindpalaces.util;

public class FromMPTeleporterThreadLocal {
    public static ThreadLocal<Boolean> fromMPTeleporter = ThreadLocal.withInitial(() -> false);

    public static boolean get(){
        return fromMPTeleporter.get();
    }

    public static void set(boolean newValue){
        fromMPTeleporter.set(newValue);
    }
}
