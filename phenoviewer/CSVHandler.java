package phenoviewer;

import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JRadioButton;
import javax.swing.event.*;
import java.util.Arrays;
import java.awt.Color;
import java.awt.event.*;
import com.opencsv.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;


public class CSVHandler {

  CSVHandler() {
  }

  private int calculaDia(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_YEAR);
  }

  private int calculaAno(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.YEAR);
  }

  private int calculaHora(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.HOUR_OF_DAY);
  }

  public void WriteCSV(ArrayList<File> imageList, File mask) {
    try {
      JFrame parentFrame = new JFrame();
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
      int result = fileChooser.showSaveDialog(parentFrame);
      if (result == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(fileToSave.getAbsolutePath()));
        CSVWriter writer = new CSVWriter(out);

        AvgRgb avg = new AvgRgb(imageList, mask);
        MeanH meanh = new MeanH(imageList, mask);
        ExcGreen excg = new ExcGreen(imageList, mask);

        ArrayList<ColorRGB> avgArray = avg.process();
        ArrayList<Float> meanHArray = meanh.process();
        ArrayList<Float> excgArray = excg.process();

        String[] title = ("filename,year,day,hour,avgR,avgG,avgB,meanH,excG").split(",");
        writer.writeNext(title);

        for (int i=0; i<avgArray.size(); i++) {
          String[] entries = (imageList.get(i).getName()+","+calculaAno(imageList.get(i))+","+calculaDia(imageList.get(i))+","+calculaHora(imageList.get(i))+","+avgArray.get(i).toCSV()+","+meanHArray.get(i)+","+excgArray.get(i)).split(",");
          writer.writeNext(entries);
        }
        writer.close();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public JFrame AnalyzeCSV(String path) {
    try {
      CSVReader reader = new CSVReader(new FileReader(path),',');

      List<String[]> entriesList = reader.readAll();
      String[][] rowData = entriesList.toArray(new String[0][]);
      entriesList.remove(0);

      String[][] graphData = entriesList.toArray(new String[0][]);

      JTable table = new JTable(graphData, rowData[0]);
      String[] legend = rowData[0];

      CSVAnalyzer analyzer = new CSVAnalyzer(table, legend, graphData);
      return analyzer;
    }
    catch (FileNotFoundException ex) {
      System.out.println("ERROR: File error!");
    }
    catch (IOException ex) {
      System.out.println("ERROR: File error!");
    }
    return null;
  }
}