package vip.ourcraft.programs.bossshopexcelinitiator;

import java.io.*;

class Util {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    static String readFile(File file) {
        if (!file.exists()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }
}
