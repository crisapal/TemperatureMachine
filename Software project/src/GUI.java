import com.fazecast.jSerialComm.SerialPort;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.DefaultValueDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GUI extends JFrame {

    private static final int W = 200;
    private static final int H = 2 * W;
    private JButton backToMainMenuButton=new JButton();
    private JButton getTemperatureButton=new JButton("GET TEMPERATURE!"), getWeatherButton=new JButton("   "+"GET FORECAST!"+"   ");
    private JPanel mainPanel=new JPanel(), temperaturePanel=new JPanel(), secondPanel=new JPanel(),backPanel=new JPanel(),panel=new JPanel();
    private JLabel temperatureLabel = new JLabel("Valoarea temperaturii");
    private double value;



    public GUI() {
        //setting temperature pane;

        setAppButtons(getTemperatureButton,getWeatherButton,backToMainMenuButton);
        setSecondaryPanels(mainPanel,backPanel,secondPanel,panel,temperaturePanel);

        //setting our app frame
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(500, 475);
        this.setLocation(400,100);
        this.setVisible(true);

    }

    public void setSecondaryPanels(JPanel mainPanel,JPanel backPanel,JPanel secondPanel, JPanel panel,JPanel temperaturePanel){

        //design MAIN PANEL
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(107, 155, 181));

        try { //logo image
            BufferedImage mainImage=  ImageIO.read(new File("assets/weather-tiny.png"));
            JLabel imageLabel= new JLabel(new ImageIcon(mainImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel.setBorder(new EmptyBorder(60,0,0,0));
            mainPanel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;
        c.weighty = .25;
        c.insets = new Insets(30, 0, 5, 0);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;

        mainPanel.add(getTemperatureButton,c); // buttons for accessing the main features
        mainPanel.add(getWeatherButton,c);




        // setting the frame panel which switches visibility between the main and second panel
        panel.setBackground(new Color(107, 155, 181));
        panel.add(mainPanel);
        panel.add(secondPanel);

        //design back panel
        backPanel.setLayout(new BorderLayout());
        backPanel.setSize(this.getWidth(),backPanel.getHeight());
        backPanel.add(backToMainMenuButton,BorderLayout.WEST);
        backPanel.add(temperatureLabel,BorderLayout.CENTER);

        //design SECOND PANEL - temperature+ back Panel
        secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.PAGE_AXIS));
        secondPanel.setBackground(new Color(107, 155, 181));
        secondPanel.add(backPanel);
        secondPanel.add(temperaturePanel);



        //visibility
        mainPanel.setVisible(true);
        secondPanel.setVisible(false);
    }


    public void setTemperaturePanel(JPanel temperaturePanel){
        //design setting
        temperaturePanel.setLayout(new GridLayout());
        temperatureLabel.setFont (temperatureLabel.getFont ().deriveFont (20.0f));
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);


        //setting celsius plot
        DefaultValueDataset datasetCelsius = new DefaultValueDataset(value);
        ThermometerPlot plotCelsius = new ThermometerPlot(datasetCelsius);
        plotCelsius.setSubrangePaint(0, Color.green.darker());
        plotCelsius.setSubrangePaint(1, Color.orange);
        plotCelsius.setSubrangePaint(2, Color.red.darker());
        JFreeChart celsius = new JFreeChart("Celsius",
                JFreeChart.DEFAULT_TITLE_FONT, plotCelsius, true);
        temperaturePanel.add(new ChartPanel(celsius, W, H, W, H, W, H,
                false, true, true, true, true, true));
        //setting fahrenheit plot
        DefaultValueDataset datasetFahrenheit = new DefaultValueDataset((value*1.8)+32);
        ThermometerPlot plotFahrenheit = new ThermometerPlot(datasetFahrenheit);
        plotFahrenheit.setUnits(ThermometerPlot.UNITS_FAHRENHEIT);
        plotFahrenheit.setUpperBound(212);
        plotFahrenheit.setSubrangePaint(0, Color.green.darker());
        plotFahrenheit.setSubrangePaint(1, Color.orange);
        plotFahrenheit.setSubrangePaint(2, Color.red.darker());
        JFreeChart fahrenheit = new JFreeChart("Fahrenheit",
                JFreeChart.DEFAULT_TITLE_FONT, plotFahrenheit, true);
        temperaturePanel.add(new ChartPanel(fahrenheit, W, H, W, H, W, H,
                false, true, true, true, true, true));

    }


    public void setAppButtons(JButton getTemperatureButton,JButton getWeatherButton,JButton backToMainMenuButton){
        //designing layout and alignment
        getTemperatureButton.setFont(new Font("Trebuchet", Font.PLAIN, 15));

        getTemperatureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getTemperatureButton.setSize(new Dimension(80, 60));

        getWeatherButton.setFont(new Font("Trebuchet", Font.PLAIN, 15));
        getWeatherButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getWeatherButton.setSize(new Dimension(80, 60));


        //designing the back to main menu button
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("assets/back-super-tiny.png"));
            backToMainMenuButton.setIcon(new ImageIcon(buttonIcon));
            backToMainMenuButton.setMargin(new Insets(0, 0, 0, 0));
            backToMainMenuButton.setBorder(BorderFactory.createLineBorder(new Color(0f,0f,0f,.0f ),8));
            backToMainMenuButton.setOpaque( false );


        } catch (Exception ex) {
            System.out.println(ex);
        }

        //adding listeners to our buttons
        backToMainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondPanel.setVisible(false);
                mainPanel.setVisible(true);
                temperaturePanel.removeAll();
            }

        });

        getTemperatureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    temperatureConnection();
                    setTemperaturePanel(temperaturePanel);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

                mainPanel.setVisible(false);
                secondPanel.setVisible(true);
            }
        });

        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final ImageIcon icon = new ImageIcon("assets/progress.png");
                JOptionPane.showMessageDialog(new JFrame("Weather API"), "This functionality is in progress!","Weather API", JOptionPane.INFORMATION_MESSAGE, icon);



            }
        });
    }

    public void temperatureConnection() throws IOException,InterruptedException{
        // port number for MAC Computer
        SerialPort sp = SerialPort.getCommPort("/dev/cu.usbserial-2101837374711");
        // connection setting according VHDL project
        sp.setComPortParameters(300, 8, 1, 0); // default connection settings for Arduino
        // block until bytes can be written
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // block until bytes can be written



        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }

            try {
                System.out.println("da");
                InputStream is = sp.getInputStream();
                int buf1;
                int buf2;
                buf1 = is.read();
                buf2 = is.read();
                this.value=(double)(buf1+buf2*256)*0.0625;
                System.out.println(this.value+"cris");
            } catch (IOException e) {
                e.printStackTrace();
            }


        if (sp.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }
    }


}
