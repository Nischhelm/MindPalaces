package mindpalaces.util;

public class FromMPTeleporterThreadLocal {
    public static ThreadLocal<Boolean> fromMPTeleporter = ThreadLocal.withInitial(() -> false);

    public static boolean get(){
        boolean wasFromMPTeleport = fromMPTeleporter.get();
        remove();
        return wasFromMPTeleport;
    }

    public static void remove(){
        fromMPTeleporter.remove();
    }

    public static void set(boolean newValue){
        fromMPTeleporter.set(newValue);
    }
}
