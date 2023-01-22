import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("ec2-35-170-70-54.compute-1.amazonaws.com", 51000);
        readDataFromServer(socket);

    }

    public static void readDataFromServer(Socket socket) throws IOException {
        String fullMessage = "";
        byte[] messageByte = new byte[100];
        boolean end = false;
        String dataString = "";
        int packetsCount = 0;

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            long startTime = System.nanoTime();
            LocalTime previousPacketTime = LocalTime.now();
            while (!end) {

                packetsCount++;
                int bytesRead = in.read(messageByte);
                dataString = new String(messageByte, 0, bytesRead);
                if (isPaddedZeros(dataString)) {
                    end = true;
                }
                if(end) {
                dataString = dataString.replaceAll("\u0000","");
                }
                fullMessage = fullMessage + dataString;
                LocalTime currentPacketTime = LocalTime.now();
                System.out.println("Received Message :" + dataString + " At Interval : " + previousPacketTime.until(currentPacketTime, ChronoUnit.SECONDS));
                previousPacketTime = currentPacketTime;
                if (isPaddedZeros(dataString)) {
                    end = true;
                }

            }
            long endTime = System.nanoTime();
            long timeTaken = endTime - startTime;
            long timetakenByEachPacket = timeTaken/packetsCount;
            double elapsedTimeInSecond = (double) timetakenByEachPacket / 1_000_000_000;

            System.out.println( "Average Interval Time :" + elapsedTimeInSecond + " seconds");
            System.out.println("Received Message :" + fullMessage);





        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPaddedZeros(String data) {
        for(int i = 0 ; i < data.length() -1 ; i++){
            if(data.charAt(i) == '\u0000' && data.charAt(i+1) == '\u0000'){
                return true;
            }
        }
        return false;
    }
}

