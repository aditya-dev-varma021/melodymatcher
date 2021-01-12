import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    public static Mixer mainmixer;
    public static Clip mainClip;

    public static void main(String[] args) {
        //recordAudio();
        if (!soundless(loadAsBytes("input.wav"))) {
            System.out.println("The recording has sound!");
        }
        AudioFormat f = new AudioFormat(48000, 16, 2, true, false);
        //DataLine.Info inf = new DataLine.Info(SourceDataLine.class, f);
        try {
            TargetDataLine line = AudioSystem.getTargetDataLine(f);
            DataLine.Info test = new DataLine.Info(TargetDataLine.class, f);
            TargetDataLine other = (TargetDataLine)AudioSystem.getLine(test);
            String output = line.equals(other) ? "Yes" : "No";
            System.out.println(output);
            System.out.println(line.toString());
            for (Mixer.Info i : AudioSystem.getMixerInfo()) {
                Line.Info[] tli = AudioSystem.getMixer(i).getTargetLineInfo();
                if (tli.length != 0) {
                   Line comp = AudioSystem.getLine(tli[0]);
                    System.out.println(comp.toString() + ":" +i.getName());
                   if (comp.equals(line)) {
                       System.out.println("The TargetDataLine is from " + i.getName());
                   }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* for (Line.Info inf : allTDL()) {
            recordAudio(inf);
            try {
                Thread.sleep(5000);
            } catch(Exception e) {
                e.printStackTrace();
            }
            if (!soundless(loadAsBytes("input.wav"))) {
                System.out.println("The recording with " + inf.toString() + " has sound!");
            }
            System.out.println("The last recording with " + inf.toString() + " was soundless.");
        } */

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

    static Line getCorrectLine() {
        for (Mixer.Info i : AudioSystem.getMixerInfo()) {
            Mixer m = AudioSystem.getMixer(i);
            System.out.println(i.getName() + " : " + m.getTargetLineInfo().length);
        }
        return null;


    }



    static boolean soundless(byte[] s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length; i += 1) {
            if (s[i] != 0) {
                return false;
            }
        }
        return true;
    }



   public static void recordAudio(Line.Info inf) {
        System.out.println("Recording...");
        AudioFormat f = new AudioFormat(48000, 16, 2, true, false);
        //DataLine.Info inf = new DataLine.Info(SourceDataLine.class, f);
        try {
            final TargetDataLine line = (TargetDataLine)AudioSystem.getLine(inf);
            Thread record = new Thread(){
                public void run() {
                    AudioInputStream inputStream = new AudioInputStream(line);
                    File to = new File("src/input.wav");
                    try {
                        line.open(f);
                        line.start();
                        AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, to);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            record.start();
            Thread.sleep(5000);
            line.stop();
            line.close();
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        } catch (ClassCastException cce) {
            try {
                System.out.println("Was unable to cast " + AudioSystem.getLine(inf).toString() + " to a TargetDataLine.");
            } catch (LineUnavailableException lue2) {
                lue2.printStackTrace();
            }
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("End recording.");
    }


    public static void recordAudio() {
        System.out.println("Recording...");
        AudioFormat f = new AudioFormat(48000, 16, 2, true, false);
        //DataLine.Info inf = new DataLine.Info(SourceDataLine.class, f);
        try {
            final TargetDataLine line = AudioSystem.getTargetDataLine(f);
            System.err.println(line.getLineInfo() + "\n" + line.toString());
            Thread record = new Thread(){
                public void run() {
                    AudioInputStream inputStream = new AudioInputStream(line);
                    File to = new File("src/input.wav");
                    try {
                        line.open(f);
                        line.start();
                        AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, to);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            record.start();
            Thread.sleep(5000);
            line.stop();
            line.close();
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        } catch (ClassCastException cce) {
            try {
                System.out.println("Was unable to cast " + AudioSystem.getTargetDataLine(f).toString() + " to a TargetDataLine.");
            } catch (LineUnavailableException lue2) {
                lue2.printStackTrace();
            }
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("End recording.");
    }

    public static ArrayList<Line.Info> allTDL() {
        ArrayList<Line.Info> all = new ArrayList<>();
        for (Mixer.Info i : AudioSystem.getMixerInfo()) {
            Line.Info[] tli = AudioSystem.getMixer(i).getTargetLineInfo();
            if (tli.length != 0) {
                for (int f = 0; f < tli.length; f += 1) {
                    all.add(tli[f]);
                }
            }
        }
        return all;
    }




}

