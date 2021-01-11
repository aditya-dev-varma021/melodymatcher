import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main {

    public static Mixer mainmixer;
    public static Clip mainClip;

    public static void main(String[] args) {
        playAudio();

    }

    /* Prints all open audio lines on your computer along with
    their description.
     */
    public static void openLines() {
        Mixer.Info[] totalInfo = AudioSystem.getMixerInfo();
        for(Mixer.Info info : totalInfo) {
            System.out.println(info.getName() + " : " + info.getDescription());
        }
    }

    /* Plays Audio from an existing WAVE file. */
    public static void playAudio() {
        Mixer.Info[] totalInfo = AudioSystem.getMixerInfo(); //contains all the open line info
        //Default audio device is generally the primary index.
        mainmixer = AudioSystem.getMixer(totalInfo[3]); // gets the default audio device
        DataLine.Info dataInfo = new DataLine.Info(Clip.class, null); //gets the line (info about the line before getting the line)
        try {
            mainClip = (Clip)mainmixer.getLine(dataInfo);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        try {
            URL sound = Main.class.getResource("rec.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(sound);
            mainClip.open(audioStream);
        }catch(LineUnavailableException e) {
            e.printStackTrace();
        }catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainClip.start();
        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (mainClip.isActive());
        System.out.println(mainClip.getLineInfo() + ":" + mainClip.getFrameLength());
    }

    /* Captures Audio clips using a TargetDataLine. */
    public static void captureAudio() {
        try {
            AudioFormat f = getAudioFormat();
            DataLine.Info secure = new DataLine.Info(TargetDataLine.class, f);
            if (!AudioSystem.isLineSupported(secure)) {
                System.err.println("Unsupported Line");
            }
            TargetDataLine tLine = (TargetDataLine)AudioSystem.getLine(secure);
            System.err.println(tLine.toString());
            System.err.println(tLine.getLineInfo());
            System.out.println("Starting recording...");
            tLine.open(f);
            tLine.start();
            File writeTo = new File("input.wav");
            Thread t = new Thread(){
                    public void run() {
                        try {
                            AudioInputStream is = new AudioInputStream(tLine);
                            AudioSystem.write(is, AudioFileFormat.Type.WAVE, writeTo);
                        } catch(IOException e) {
                            System.err.println("Encountered system I/O error in recording:");
                            e.printStackTrace();
                        }
                    }
            };
            t.start();
            Thread.sleep(7500);
            tLine.stop();
            tLine.close();
            System.out.println("Recording has ended.");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] loadAsBytes(String name) {
        assert name.contains(".wav");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File retrieve = new File("src/"+ name);
        try {
            InputStream input = AudioSystem.getAudioInputStream(retrieve);

            int read;
            byte[] b = new byte[1024];
            while ((read = input.read(b)) > 0) {
                out.write(b, 0, read);
            }
            out.flush();
            byte[] full = out.toByteArray();
            return full;

        } catch(UnsupportedAudioFileException e) {
            System.err.println("The File " + name + " is unsupported on this system.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Input-Output Exception on retrieval of file " + name);
            e.printStackTrace();
        }
        return null;

    }


    public static Line obtainLine(String name) {
        try {
            Mixer.Info[] aggInfo = AudioSystem.getMixerInfo();
            for (Mixer.Info i : aggInfo) {
                Mixer e = AudioSystem.getMixer(i);
                if (i.getName().equals(name)) {
                    try {
                        return e.getLine(e.getTargetLineInfo()[0]);
                    } catch (LineUnavailableException lue) {
                        lue.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   static AudioFormat getAudioFormat() {
        float sampleRate = 22050;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }


    public static void recordAudio() {

    }

}

