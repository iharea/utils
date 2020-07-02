import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RadList {

    public static Map<String, CustomPair> getFields(Path path) {
        Map<String, CustomPair> map = new TreeMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\s+");
                if (fields.length > 7) {
                    String macAddress = fields[4].replace(":", "");
                    String vlanId = fields[5];
                    String isid = fields[6];
                    if (macAddress.length() == 12)
                        map.put(macAddress, new CustomPair(vlanId, isid));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void generateEntries (Map<String, CustomPair> map, Path path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));

        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, CustomPair> entry = (Map.Entry<String, CustomPair>) iterator.next();
            writer.write(String.format("%s Cleartext-Password :=\"%s\"\n" +
                            "\tService-Type = Framed-User,\n" +
                            "\tFramed-Protocol = PPP,\n" +
                            "\tAuth-Type := Accept,\n" +
                            "\tFabric-Attach-ISID = \"%s:%s\"\n", entry.getKey(), entry.getKey(),
                    entry.getValue().getVlanId(), entry.getValue().getIsid()));
            writer.write("\n");
        }
        writer.close();
    }


    public static void main(String[] args) throws IOException {

        Path originalPath = Paths.get("/home/ion2910/Downloads/radList");
        Path newPath = Paths.get("/home/ion2910/Downloads/newRadList");
        if (!Files.exists(newPath)) {
            Files.createFile(newPath);
        }

        Map<String, CustomPair> map = RadList.getFields(originalPath);
        RadList.generateEntries(map, newPath);

    }

    public  static class CustomPair {
        private String vlanId;
        private String isid;

        public CustomPair(String vlanId, String isid) {
            this.vlanId = vlanId;
            this.isid = isid;
        }

        public String getVlanId() {
            return vlanId;
        }

        public String getIsid() {
            return isid;
        }

        @Override
        public String toString() {
            return "(" + vlanId + "," + isid +")";
        }
    }
}



