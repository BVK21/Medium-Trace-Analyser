import java.io.IOException;
import java.net.Socket;


public class ServerConnection {

    public static final String HostName = "localhost";
    public static final int PortNum = 51000;

    private String hostName;
    private int portNum;
    private boolean isConAlive;

    public ServerConnection(String hostName, int  portNum) {
        this.hostName = hostName;
        this.portNum = portNum;
        this.isConAlive = false;
    }

    public Socket getSocketConnection(){
            try (Socket socket = new Socket(hostName, portNum)) {
            this.isConAlive = true;
            return socket;

        } catch (IOException ex) {
           this.isConAlive = false;
           System.out.println("Unable to Find the Server : " + ex.getMessage());
           return null;
        }
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public boolean isConAlive() {
        return isConAlive;
    }

    public void setConAlive(boolean conAlive) {
        isConAlive = conAlive;
    }
}
