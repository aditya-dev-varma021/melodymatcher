import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static Mixer mainmixer;
    public static Clip mainClip;

    public static void main(String[] args) {
        openLines();
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




}
