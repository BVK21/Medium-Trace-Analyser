

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TraceAnalyser {

    private static final String filePath = "/Users/bhim/IdeaProjects/Medium-Trace-Analyser/src/main/resources/MAC_testdata1";
    private static final Double threshold = 0.4;
    private static final int packetTransmissionTime = 177;
    private static final int ackTransmissionTime = 16;

    private static List<Double> numericalList;
    private static int packets = 0;
    private static int packetsWithAck = 0;
    private static int freshPackets = 0;
    private static int freshPacketsWithAck = 0;


    public static void  getListElements(String filePath) throws Exception {
       if(filePath == null || filePath.isEmpty())
           throw new Exception("Invalid FilePath");

       numericalList = new ArrayList<>();

        try(BufferedReader numbers = new BufferedReader(new FileReader(filePath))) {

            String numberString;
            while ((numberString = numbers.readLine()) != null ) {
                if (numberString.isEmpty())
                    continue;
                double realNum;
                double complexNum;
                double number;
                realNum = Double.parseDouble(numberString.split(" ")[0]);
                complexNum = Double.parseDouble(numberString.split(" ")[1]);
                number = Math.sqrt((realNum * realNum) + (complexNum * complexNum));
                numericalList.add(number);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }








    public static void findFreshPackets(){

        boolean prevPacket = true;
        boolean prevPacketWithAck = true;

        for(int i = 0 ; i < numericalList.size()-1 ; i++){
            boolean currentPacket = false;
            boolean currentPacketWithAck = false;
            if(numericalList.get(i) >= threshold){

                if(checkIsAsPacket(i)){
                    i+=packetTransmissionTime;
                    if(findPacketsAck(i)) {
                       i+=ackTransmissionTime;
                       currentPacketWithAck  = true;
                       currentPacket = false;
                    }else{
                        currentPacket = true;
                        currentPacketWithAck = false;
                   }
                  if(!prevPacket && !prevPacketWithAck && !currentPacket && currentPacketWithAck){
                      packets++;
                      packetsWithAck++;
                  }
                  else if(!prevPacket && !prevPacketWithAck && currentPacket && !currentPacketWithAck){
                      packets++;

                  }
                  else if(!prevPacket && prevPacketWithAck && !currentPacket && currentPacketWithAck){
                      freshPacketsWithAck++;
                      packets++;
                      freshPackets++;
                      packetsWithAck++;

                  }
                  else if(!prevPacket && prevPacketWithAck && currentPacket && !currentPacketWithAck){
                      packets++;
                      freshPackets++;
                  }else if(prevPacket && !prevPacketWithAck && !currentPacket && currentPacketWithAck){
                      packets++;
                      packetsWithAck++;
                  }else if(prevPacket && !prevPacketWithAck && currentPacket && !currentPacketWithAck){
                      packets++;
                  }

                    prevPacket = currentPacket;
                    prevPacketWithAck = currentPacketWithAck;
                }
            }
        }
    }

    public static boolean  findPacketsAck(int index){
        if(index + ackTransmissionTime < numericalList.size() -1) {
            for (int i = index; i < index + 16; i++) {
                if (numericalList.get(i) > 0.2 && numericalList.get(i) < 0.3)
                    return true;
            }
        }
        return false;
    }




    public static boolean checkIsAsPacket(int index){
       int i = index ;
       int count = 0;
       int newCount = 0;
       boolean drop = true;
       while(numericalList.get(i) >= 0.3 && i < numericalList.size() - 1 && index < numericalList.size() -1 ){
           i++;
           count++;
       }

       for(int j = index ; j < index+177 -1  ;j++){
           if(j < numericalList.size() - 1 && numericalList.get(j) >= 0.3){
               newCount++;
           }
       }

        for(int j = index ; j < index+177 - 2 ;j++){
            if(j < numericalList.size() - 1  && numericalList.get(j) < 0.3 && numericalList.get(j+1) < 0.3 && numericalList.get(j+2) < 0.3){
                drop = false;
            }
        }


        return count > 141 && newCount > 141 ;
    }

    public static void printResult(){
        System.out.println("Total number of packets from node A                                 "+": " + packets);
        System.out.println("Total number of packets with ACK from node A                        " + ": "+ packetsWithAck);
        System.out.println("Total number of fresh packets from node A                           "+": " + freshPackets);
        System.out.println("Total number of fresh packets with ACK from node A                  "+": "+freshPacketsWithAck);
    }


    public static void main(String[] args) throws Exception {
      getListElements(filePath);
      try {
          findFreshPackets();
      }catch (Exception e){
          e.printStackTrace();
      }
      printResult();
    }
}
