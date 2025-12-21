package com.hallvardlaerum.libs.filerogopplasting;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.*;

public interface TekstfilformatKonverteringsMesterAktig {

    static String konverterTekstFil(File lesFil, String lesKaraktersettString) {
        return konverterTekstFil(lesFil,lesKaraktersettString,"UTF-16");
    }

    static String konverterTekstFil(File lesFil, String lesKaraktersettString, String skrivKaraktersettString) {

        // If you know the input encoding, set it explicitly, e.g. "UTF-8" or "ISO-8859-1".
        Charset inputCharset = Charset.forName(lesKaraktersettString);

        // Choose output charset:
        // "UTF-16" writes a BOM and uses UTF-16 with endianness indicated by BOM.
        // "UTF-16LE" or "UTF-16BE" writes little- or big-endian without BOM.
        Charset outputCharset = Charset.forName(skrivKaraktersettString);
        Path lesPath = lesFil.toPath();


        try {
            String konvertertString = Files.readString(lesPath, inputCharset);
            return fjernAlleLineBreaksIStrenger(konvertertString);
        } catch (IOException e) {
            Loggekyklop.bruk().loggFEIL("Klarte ikke å lese filen med stien " + lesPath.toString());
            return null;
        }


    }

    static String fjernAlleLineBreaksIStrenger(String tekstString) {

        String input = "..."; // your text
        Pattern p = Pattern.compile("\"([^\"]*)\"|\"((?:.|\\R)*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String quoted = m.group(1) != null ? m.group(1) : m.group(2);
            if (quoted == null) { m.appendReplacement(sb, "\"\""); continue; }
            String cleaned = quoted.replaceAll("\\R", " "); // replace newlines with space (or "" to remove)
            // escape backslashes and $ for replacement
            cleaned = cleaned.replace("\\", "\\\\").replace("$", "\\$");
            m.appendReplacement(sb, "\"" + cleaned + "\"");
        }
        m.appendTail(sb);
        return sb.toString();

    }
}
