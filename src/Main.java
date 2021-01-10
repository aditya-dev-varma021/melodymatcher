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
        openLines();
        captureAudio();
        byte[] in = loadAsBytes("input.wav");
        System.out.println("Playing Audio...");
        playAudio();
        System.out.println("Audio finished.");

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
            URL sound = Main.class.getResource("input.wav");
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
    }

    /* Captures Audio clips using a TargetDataLine. */
    public static void captureAudio() {
        try {
            AudioFormat f = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            // WAVE files are Little-Endian
            DataLine.Info secure = new DataLine.Info(TargetDataLine.class, f);
            if (!AudioSystem.isLineSupported(secure)) {
                System.err.println("Unsupported Line");
            }
            TargetDataLine tLine = (TargetDataLine)AudioSystem.getLine(secure);
            System.out.println("TargetDataLine is " + tLine.toString());
            int user = 0;
            System.out.println("Are you ready to begin recording? (Enter '1')");
            user = new java.util.Scanner(System.in).nextInt(); // generally do not advise ad-hoc scanner production like this
            if (user == 1) {
                System.out.println("Starting recording...");
                tLine.open();
                tLine.start();
                Thread recorder = new Thread() {
                    @Override public void run() {
                        AudioInputStream inp = new AudioInputStream(tLine);
                        File audio = new File("src/input.wav");

                        try {
                            AudioSystem.write(inp, AudioFileFormat.Type.WAVE, audio);
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }

                };
                recorder.start();
                Thread.sleep(5000);
                tLine.stop();
                tLine.close();
                System.out.println("Ended recording.");

            }

            

        } catch(Exception e) {

        }
    }

    public static byte[] loadAsBytes(String name) {
        assert name.contains(".wav");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File retrieve = new File(name);
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


}
