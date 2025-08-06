import java.security.Permission;

public class MySecurityManager extends SecurityManager {


    // Check all permissions
    @Override
    public void checkPermission(Permission perm) {
//        super.checkPermission(perm);
    }

    // Check if program can execute files
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec permission exception: " + cmd);
    }

    // Check if program can read files

    @Override
    public void checkRead(String file) {
        System.out.println(file);
        if (file.contains("C:\\code\\yuoj-code-sandbox")) {
            return;
        }
//        throw new SecurityException("checkRead permission exception: " + file);
    }

    // Check if program can write files
    @Override
    public void checkWrite(String file) {
//        throw new SecurityException("checkWrite permission exception: " + file);
    }

    // Check if program can delete files
    @Override
    public void checkDelete(String file) {
//        throw new SecurityException("checkDelete permission exception: " + file);
    }

    // Check if program can connect to network
    @Override
    public void checkConnect(String host, int port) {
//        throw new SecurityException("checkConnect permission exception: " + host + ":" + port);
    }
}
