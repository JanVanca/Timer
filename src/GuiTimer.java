import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GuiTimer extends JFrame {
    Timer time;
    private JButton startTimer;
    private JButton stopTimer;
    private JButton countTimer;
    private JButton restartTimer;
    private JLabel datum;
    private JLabel stopWatch;
    private JPanel rootPanel;
    private JLabel hour;
    private JLabel minute;
    private JLabel second;
    private String todayDatum;
    private String actualTime;
    private boolean isRunning;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private boolean fromStart;
    private boolean fromStop;
    private boolean printCountedTime = false;
    private boolean counter = false;
    private boolean alreadyRestarted = false;
    private boolean alreadyCounted = false;

    public GuiTimer() {
        add(rootPanel);
        isRunning = false;
        setSize(400, 250);
        setDate();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /**
         * Button start: starts timer.
         */
        startTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning == false) {
                    isRunning = true;
                    fromStart = true;
                    fromStop = false;
                    counter = true;
                    alreadyRestarted = false;
                    alreadyCounted = false;
                    actualTime();
                    start();
                    try {
                        printStreamToTxt();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        });

        /**
         * Button stop: stops timer.
         */
        stopTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning == true) {
                    isRunning = false;
                    fromStart = false;
                    fromStop = true;
                    actualTime();
                    stop();
                    try {
                        printStreamToTxt();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    return;
                }
            }
        });

        /**
         * Button count - Calculates the elapsed time between on and off
         */
        countTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printCountedTime = true;
                try {
                    counter();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        /**
         * Button restart - restarts the time on the counter
         */
        restartTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    restart();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * method writes text to the file
     *
     * @throws IOException
     */
    private void printStreamToTxt() throws IOException {

        try (FileWriter fw = new FileWriter("Timer.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            if (fromStart == true) {
                out.println("----------");
                out.write(todayDatum + " " + actualTime + " - ");
            } else if (printCountedTime == true && counter == true) {
                out.println("Hodiny: " + hours + " " + "Minuty: " + minutes + " " + "Sekundy: " + seconds);
            } else if (fromStop == true) {
                out.println(actualTime);
            }

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    /**
     * Current date to set.
     */
    private void setDate() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.now();
        datum.setText(localDate.format(dateFormat));
        this.todayDatum = datum.getText();
    }

    /**
     * Remembers the current time.
     */
    private void actualTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        this.actualTime = dateFormat.format(date);
    }


    /**
     * Turn on the time meter.
     */
    private void start() {
        timer();
        time.start();

    }

    /**
     * Shows time.
     */
    private void timer() {
        time = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if (seconds < 10) {
                    second.setText("0" + seconds);
                } else if (seconds >= 10 && seconds <= 59) {
                    second.setText("" + seconds);
                } else {
                    second.setText("00");
                }
                if (seconds >= 60) {
                    seconds = 0;
                    minutes++;

                    if (minutes < 10) {
                        minute.setText("0" + minutes);
                    } else if (minutes >= 10 && minutes <= 59) {
                        minute.setText("" + minutes);
                    } else {
                        minute.setText("00");
                    }
                }
                if (minutes >= 60) {
                    minutes = 0;
                    hours++;

                    if (hours < 10) {
                        hour.setText("0" + hours);
                    } else {
                        hour.setText("" + hours);
                    }
                }
            }
        });
    }


    /**
     * Turn off the time meter.
     */
    private void stop() {
        time.stop();

    }

    /**
     * Restarts the time meter.
     *
     * @throws IOException
     */
    private void restart() throws IOException {

        if (alreadyRestarted == true || alreadyCounted == true) {
            seconds = 0;
            minutes = 0;
            hours = 0;
            second.setText("00");
            minute.setText("00");
            hour.setText("00");
            return;
        } else {
            if (isRunning == true) {
                isRunning = false;
                fromStart = false;
                fromStop = true;
                counter = true;
                actualTime();
                stop();
                printStreamToTxt();
                printCountedTime = true;
                counter();
                printCountedTime = false;
            } else if (isRunning == false) {
                isRunning = false;
                fromStart = false;
                fromStop = true;
                counter = true;
                actualTime();
                stop();
                printCountedTime = true;
                counter();
                printCountedTime = false;

            }
            seconds = 0;
            minutes = 0;
            hours = 0;
            second.setText("00");
            minute.setText("00");
            hour.setText("00");
            alreadyRestarted = true;
        }
    }

    /**
     * Recalculates the elapsed time since switching on.
     *
     * @throws IOException
     */
    private void counter() throws IOException {
        alreadyCounted = true;

        if (fromStart == true) {
            printCountedTime = false;
            counter = false;
            return;
        } else if (counter == true && fromStop == true) {
            printStreamToTxt();
            printCountedTime = false;
            counter = false;
        } else {
            printCountedTime = false;
            counter = false;
            return;
        }
    }
}
